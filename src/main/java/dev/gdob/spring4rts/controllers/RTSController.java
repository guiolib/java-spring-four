package dev.gdob.spring4rts.controllers;

import java.time.Duration;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gdob.spring4rts.Entities.MensagemEntity;
import dev.gdob.spring4rts.repository.MensagemReactiveRepository;
import dev.gdob.spring4rts.sevice.ReactiveMensagemService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@RestController
@RequestMapping("/rts")
public class RTSController {

    private final ReactiveMensagemService reactiveMensagemService;

    RTSController(ReactiveMensagemService reactiveMensagemService) {
        this.reactiveMensagemService = reactiveMensagemService;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<String>> index() {
        return Flux.just("um", "dois", "tres").map(List::of)
                .delayElements(Duration.ofSeconds(1));
    }

    @GetMapping(path = "mensagens", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MensagemEntity> mensagens() {
        return reactiveMensagemService.getAllMessages();
        // return Flux.merge(msgOps.findAll(), sink.asFlux()).delayElements(Duration.ofNanos(100));
    }

}
