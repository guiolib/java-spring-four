package dev.gdob.spring4rts.sevice;

import org.springframework.stereotype.Service;

import dev.gdob.spring4rts.Entities.MensagemEntity;
import dev.gdob.spring4rts.repository.MensagemReactiveRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class ReactiveMensagemService {

    private final Sinks.Many<MensagemEntity> sink;
    private final MensagemReactiveRepository repository;

    public ReactiveMensagemService(MensagemReactiveRepository repository) {
        this.repository = repository;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    public Flux<MensagemEntity> getAllMessages() {
        return repository.findAll();
    }

    public Flux<MensagemEntity> getById(String id) {
        return repository.findByIDFlux(id);
    }

    public void publishMessage(MensagemEntity mensagem) {
        repository.save(mensagem).subscribe();
        sink.tryEmitNext(mensagem);
    }

}
