package dev.gdob.spring4rts.controllers;

import java.time.Duration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.gdob.spring4rts.Entities.MensagemEntity;
import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.repository.MensagemRepo;
import reactor.core.publisher.Flux;

@RestController
public class IndexController {

    private final MensagemRepo mensagemRepo;

    IndexController(MensagemRepo mensagemRepo) {
        this.mensagemRepo = mensagemRepo;
    }

    @GetMapping
    public String index() {
        return "Welcome to Spring 4 RTS!";
    }

    @GetMapping("/mensagens")
    public Flux<MensagemDto> mensagens() {
        return Flux.fromIterable(mensagemRepo.findAll()).map(MensagemEntity::toDto).delayElements(Duration.ofNanos(1));
    }

}
