package dev.gdob.spring4rts.controllers;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.sevice.ReactiveMensagemService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

public class WebSocketController implements WebSocketHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebSocketController.class);
    private final ReactiveMensagemService reactiveMensagemService;
    private final ObjectMapper objectMapper;

    public WebSocketController(ReactiveMensagemService reactiveMensagemService, ObjectMapper objectMapper) {
        this.reactiveMensagemService = reactiveMensagemService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        log.info("WebSocket: Conexão estabelecida com sucesso [Session ID: {}]", session.getId());

        // 1. OUTBOUND: Fluxo contínuo enviando mensagens do Redis + mensagens em tempo real (Sink) para o cliente
        Flux<WebSocketMessage> outbound = reactiveMensagemService.getById("rts")
                .flatMap(dto -> {
                    try {
                        String payload = objectMapper.writeValueAsString(dto);
                        log.info("Outbound [{}]: enviando para cliente -> {}", session.getId(), dto.mensagem());
                        return Mono.just(session.textMessage(payload));
                    } catch (Exception e) {
                        log.error("Erro ao serializar mensagem de saída", e);
                        return Mono.empty(); // ignora mensagem malformatada sem fechar o WebSocket
                    }
                })
                .onErrorResume(e -> {
                    log.error("Erro no fluxo de envio do WebSocket", e);
                    return Flux.empty();
                });

        // 2. INBOUND: Processa mensagens recebidas do cliente (Insomnia/Browser) sem derrubar a conexão se o JSON for inválido
        Mono<Void> inbound = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .filter(payload -> !payload.trim().isEmpty())
                .flatMap(payload -> {
                    try {
                        MensagemDto dto = objectMapper.readValue(payload, MensagemDto.class);
                        log.info("Inbound [{}]: mensagem recebida -> {}:{}", session.getId(), dto.mensagem(), dto.id());
                        return reactiveMensagemService.publishMessage(dto.toEntity())
                                .onErrorResume(e -> {
                                    log.error("Erro ao salvar mensagem no Redis", e);
                                    return Mono.empty();
                                });
                    } catch (Exception e) {
                        log.warn("Inbound [{}]: formato inválido ignorado (não é um MensagemDto válido): '{}'", session.getId(), payload);
                        return Mono.empty(); // evita que o erro de conversão lance onError e feche o socket
                    }
                })
                .then(); // Conclui quando o cliente desconectar a transmissão

        // 3. Executa ambos os fluxos e mantém a sessão ativa enquanto a conexão estiver aberta
        return Mono.zip(session.send(outbound), inbound).then();
    }
}

