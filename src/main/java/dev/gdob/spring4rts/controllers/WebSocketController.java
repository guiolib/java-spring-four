package dev.gdob.spring4rts.controllers;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.sevice.ReactiveMensagemService;
import tools.jackson.databind.ObjectMapper;

public class WebSocketController extends TextWebSocketHandler {

    private final ReactiveMensagemService reactiveMensagemService;
    private final ObjectMapper objectMapper;

    public WebSocketController(ReactiveMensagemService reactiveMensagemService, ObjectMapper objectMapper) {
        this.reactiveMensagemService = reactiveMensagemService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        MensagemDto jsonNode = objectMapper.readValue(message.getPayload(), MensagemDto.class);
        System.out.println("Mensagem recebida - %s:%s ".formatted(jsonNode.mensagem(), jsonNode.id()));

        // mensagemRepo.save(jsonNode.toEntity());
        reactiveMensagemService.publishMessage(jsonNode.toEntity());
        session.sendMessage(new TextMessage("Mensagem recebida com sucesso!"));
    }

}
