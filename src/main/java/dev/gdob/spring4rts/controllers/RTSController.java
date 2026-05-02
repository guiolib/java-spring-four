package dev.gdob.spring4rts.controllers;

import java.time.Duration;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.sevice.ReactiveMensagemService;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/rts")
public class RTSController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RTSController.class);
    private final ReactiveMensagemService reactiveMensagemService;
    // private final Sinks.Many<MensagemDto> sinkMensagem;

    RTSController(ReactiveMensagemService reactiveMensagemService) {
        this.reactiveMensagemService = reactiveMensagemService;
        // this.sinkMensagem = sinkMensagem;
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<String>> index() {
        return Flux.just("um", "dois", "tres").map(List::of)
                .delayElements(Duration.ofSeconds(1));
    }

    @GetMapping(path = "mensagens", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MensagemDto> mensagens() {
        log.info("SSE mensagem");
        return reactiveMensagemService.getAllMessages()
            .delayElements(Duration.ofSeconds(1))
            .doOnCancel(() -> log.info("Cliente desconectado"))
            .doOnTerminate(() -> log.info("Fluxo de mensagens completo"));

        // return toSseEmitter(reactiveMensagemService.getAllMessages());
    }

    @GetMapping(path = "{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MensagemDto> mensagensById(@PathVariable String id) {
        log.info("SSE mensagem by id: %s".formatted(id));
        return reactiveMensagemService.getById(id)
            .delayElements(Duration.ofSeconds(1))
            .doOnCancel(() -> log.info("Cliente desconectado"))
            .doOnTerminate(() -> log.info("Fluxo de mensagens completo"));
    }

}
