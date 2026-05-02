package dev.gdob.spring4rts.sevice;

import org.springframework.stereotype.Service;

import dev.gdob.spring4rts.Entities.MensagemEntity;
import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.repository.MensagemReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Service
public class ReactiveMensagemService {

    private final Sinks.Many<MensagemDto> sink;
    private final MensagemReactiveRepository repository;

    public ReactiveMensagemService(MensagemReactiveRepository repository, Sinks.Many<MensagemDto> sink) {
        this.repository = repository;
        this.sink = sink;
    }

    public Flux<MensagemDto> getAllMessages() {
        Flux<MensagemDto> stored = repository.findAll().map(MensagemEntity::toDto);
        Flux<MensagemDto> live = sink.asFlux();
        return Flux.concat(stored, live);
    }

    public Flux<MensagemDto> getById(String id) {
        Flux<MensagemDto> stored = repository
                .findByIDFlux(id)
                .map(MensagemEntity::toDto);

        Flux<MensagemDto> live = sink.asFlux()
                .filter(dto -> dto.id().equals(id));

        // primeiro emite os valores já persistidos e depois qualquer atualização
        return Flux.concat(stored, live);
    }

    /**
     * Save the message to redis and emit it on the sink.
     *
     * @return a Mono containing the DTO of the saved message so that callers
     *         can react immediately (e.g. send a response to the websocket
     *         client) without waiting for the global flux.
     */
    public Mono<MensagemDto> publishMessage(MensagemEntity mensagem) {
        return repository.save(mensagem)
                .map(MensagemEntity::toDto)
                .doOnNext(dto -> sink.tryEmitNext(dto)).publishOn(Schedulers.boundedElastic()) // ensure save happens on a separate thread to avoid blocking
                .next(); // convert back to Mono since we only expect one item
    }

}
