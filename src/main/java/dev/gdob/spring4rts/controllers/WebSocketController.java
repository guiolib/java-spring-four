package dev.gdob.spring4rts.controllers;

import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;

import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.sevice.ReactiveMensagemService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import tools.jackson.databind.ObjectMapper;

public class WebSocketController implements WebSocketHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WebSocketController.class);
    private final ReactiveMensagemService reactiveMensagemService;
    private final ObjectMapper objectMapper;
    private ConcurrentHashMap<String, WebSocketSession> concurrentSessions;
    private Sinks.Many<MensagemDto> sink = Sinks.many().multicast().onBackpressureBuffer();

    public WebSocketController(ReactiveMensagemService reactiveMensagemService, ObjectMapper objectMapper) {
        this.reactiveMensagemService = reactiveMensagemService;
        this.objectMapper = objectMapper;
        this.concurrentSessions = new ConcurrentHashMap<>();
    }

    @Override
    public Mono<Void> handle(org.springframework.web.reactive.socket.WebSocketSession session) {
        // outbound: current state for id "rts" (or could be getAllMessages)
        Flux<org.springframework.web.reactive.socket.WebSocketMessage> outbound = reactiveMensagemService.getById("rts")
                .map(entidade -> {
                    try {
                        String payload = objectMapper.writeValueAsString(entidade);
                        log.info("Outbound: sending to client -> %s".formatted(entidade.mensagem()));
                        return session.textMessage(payload);
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao serializar mensagem", e);
                    }
                });

        // when a message comes in, save and transform the saved value into a reply
        // Flux<MensagemDto> requestReplies = session.receive()
        Flux<WebSocketMessage> requestReplies = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .map(payload -> {
                    try {
                        return objectMapper.readValue(payload, MensagemDto.class);
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao desserializar mensagem", e);
                    }
                })
                // .doOnNext(dto -> reactiveMensagemService.publishMessage(dto.toEntity()))
                .doOnNext(dto -> log.info("Inbound: mensagem recebida - %s:%s".formatted(dto.mensagem(), dto.id())))
                .flatMap(dto -> reactiveMensagemService.publishMessage(dto.toEntity())
                        .map(saved -> {
                            try {
                                String payload = objectMapper.writeValueAsString(saved);
                                log.info("Reply: sent saved message back -> %s".formatted(saved.mensagem()));
                                return session.textMessage(payload);
                            } catch (Exception e) {
                                throw new RuntimeException("Erro ao serializar mensagem", e);
                            }
                        })
                    )
                ;

        // merge general outbound with immediate replies
        Flux<org.springframework.web.reactive.socket.WebSocketMessage> merged = Flux.merge(outbound, requestReplies);

        // return session.send(outbound);
        return session.send(merged);
    }

    // @Override
    // public Mono<Void> afterConnectionEstablished(WebSocketSession session) throws
    // Exception {
    // System.out.println("Conexão WebSocket estabelecida: " + session.getId());
    // session.
    // }

    // @Override
    // protected void handleTextMessage(WebSocketSession session, TextMessage
    // message) throws Exception {

    // MensagemDto jsonNode = objectMapper.readValue(message.getPayload(),
    // MensagemDto.class);
    // System.out.println("Mensagem recebida - %s:%s
    // ".formatted(jsonNode.mensagem(), jsonNode.id()));

    // // mensagemRepo.save(jsonNode.toEntity());
    // reactiveMensagemService.publishMessage(jsonNode.toEntity());
    // session.sendMessage(new TextMessage("Mensagem recebida com sucesso!"));
    // }

}
