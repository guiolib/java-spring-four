package dev.gdob.spring4rts.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import dev.gdob.spring4rts.controllers.WebSocketController;
import dev.gdob.spring4rts.sevice.ReactiveMensagemService;
import tools.jackson.databind.ObjectMapper;

@Lazy
@Configuration
public class WebSocketConfig {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private ReactiveMensagemService mensagemReactiveRepository;

    @Autowired
    public WebSocketConfig(ReactiveMensagemService mensagemReactiveRepository) {
        this.mensagemReactiveRepository = mensagemReactiveRepository;
    }

    // @Override
    // public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    // registry
    // .addHandler(new WebSocketController(mensagemReactiveRepository,
    // objectMapper), "websocket");
    // // .addHandler(new WebSocketController(), "websocket")
    // // .addInterceptors(new HttpSessionHandshakeInterceptor());
    // }

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/websocket", new WebSocketController(mensagemReactiveRepository, objectMapper));

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }
}
