package dev.gdob.spring4rts.repository;

import java.util.logging.Logger;

import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;

import dev.gdob.spring4rts.Entities.MensagemEntity;
import reactor.core.publisher.Flux;

@Component
public class MensagemReactiveRepository {

    private final static String BASE_REDIS_KEY = "mensagem";
    private final ReactiveHashOperations<String, String, MensagemEntity> hashOps;
    private final Logger logger = Logger.getLogger(MensagemReactiveRepository.class.getName());

    public MensagemReactiveRepository(ReactiveRedisTemplate<String, MensagemEntity> reactiveRedisTemplate) {
        this.hashOps = reactiveRedisTemplate.opsForHash();
    }

    public Flux<MensagemEntity> save(MensagemEntity mensagem) {
        return hashOps.put(BASE_REDIS_KEY, mensagem.getId(), mensagem).flatMapMany(v -> Flux.just(mensagem));
    }

    public Flux<MensagemEntity> findAll() {
        logger.info("Buscando todas as mensagens com a chave: " + BASE_REDIS_KEY);
        return hashOps.values(BASE_REDIS_KEY);
    }
 
    public Flux<MensagemEntity> findByIDFlux(String id) {
        logger.info("Buscando a mensagens com a id: " + id);
        return hashOps.values(BASE_REDIS_KEY).filter(msg -> msg.getId().equals(id));
    }
}
