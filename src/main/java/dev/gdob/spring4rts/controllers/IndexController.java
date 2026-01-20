package dev.gdob.spring4rts.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.repository.MensagemReactiveRepository;
import dev.gdob.spring4rts.repository.MensagemRepo;
import reactor.core.publisher.Flux;

@RestController
public class IndexController {

    private final MensagemReactiveRepository reactiveMensagemRepo;

    IndexController(MensagemRepo mensagemRepo, MensagemReactiveRepository reactiveMensagemRepo) {
        this.reactiveMensagemRepo = reactiveMensagemRepo;
    }

    @GetMapping
    public String index() {
        return "Welcome to Spring 4 RTS!";
    }

    @GetMapping("/mensagens")
    public Flux<MensagemDto> mensagens() {
        return reactiveMensagemRepo.findAll().map(ent -> ent.toDto());
    }

    @GetMapping("/mensagens/{id}")
    public Flux<MensagemDto> mensagensByID(@PathVariable String id) {
        return reactiveMensagemRepo.findByIDFlux(id).map(ent -> ent.toDto());
    }

}
