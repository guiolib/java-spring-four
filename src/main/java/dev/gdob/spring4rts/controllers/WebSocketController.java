package dev.gdob.spring4rts.controllers;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.repository.MensagemReactiveRepository;
import dev.gdob.spring4rts.repository.MensagemRepo;
import tools.jackson.databind.ObjectMapper;

public class WebSocketController extends TextWebSocketHandler {

    private final MensagemRepo mensagemRepo;
    private final ObjectMapper objectMapper;
    private final MensagemReactiveRepository reactiveMensagemRepo;

    public WebSocketController(MensagemRepo mensagemRepo, ObjectMapper objectMapper, MensagemReactiveRepository reactiveMensagemRepo) {
        this.mensagemRepo = mensagemRepo;
        this.objectMapper = objectMapper;
        this.reactiveMensagemRepo = reactiveMensagemRepo;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        MensagemDto jsonNode = objectMapper.readValue(message.getPayload(), MensagemDto.class);
        System.out.println("Mensagem recebida: " + jsonNode.mensagem());

        // mensagemRepo.save(jsonNode.toEntity());
        reactiveMensagemRepo.save("mensagem", jsonNode.toEntity()).subscribe();
    }

}
