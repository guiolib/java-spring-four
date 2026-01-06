package dev.gdob.spring4rts.controllers;

import java.time.Duration;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gdob.spring4rts.Entities.MensagemEntity;
import dev.gdob.spring4rts.repository.MensagemReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/rts")
public class RTSController {

    private final Sinks.Many<MensagemEntity> sink = Sinks.many().multicast().onBackpressureBuffer();

    private final MensagemReactiveRepository msgOps;

    RTSController(MensagemReactiveRepository msgOps) {
        this.msgOps = msgOps;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<String>> index() {
        return Flux.just("um", "dois", "tres").map(List::of)
                .delayElements(Duration.ofSeconds(1));
    }
    @GetMapping(path = "mensagens",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Object> mensagens() {
        return Flux.merge(msgOps.findAll("mensagem"), sink.asFlux());
    }

}
