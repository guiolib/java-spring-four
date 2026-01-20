package dev.gdob.spring4rts.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import dev.gdob.spring4rts.controllers.WebSocketController;
import dev.gdob.spring4rts.repository.MensagemReactiveRepository;
import dev.gdob.spring4rts.repository.MensagemRepo;
import dev.gdob.spring4rts.sevice.ReactiveMensagemService;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ReactiveMensagemService mensagemReactiveRepository;

    @Autowired
    public WebSocketConfig(ReactiveMensagemService mensagemReactiveRepository) {
        this.mensagemReactiveRepository = mensagemReactiveRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketController(mensagemReactiveRepository, objectMapper), "websocket")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

}
