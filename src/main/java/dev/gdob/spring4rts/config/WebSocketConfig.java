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
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MensagemRepo mensagemRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private MensagemReactiveRepository mensagemReactiveRepository;

    @Autowired
    public WebSocketConfig(MensagemRepo mensagemRepo, MensagemReactiveRepository mensagemReactiveRepository) {
        this.mensagemRepo = mensagemRepo;
        this.mensagemReactiveRepository = mensagemReactiveRepository;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketController(mensagemRepo, objectMapper, mensagemReactiveRepository), "websocket")
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

}
