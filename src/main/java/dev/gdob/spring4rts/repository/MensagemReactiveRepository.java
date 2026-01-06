package dev.gdob.spring4rts.repository;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Component;

import dev.gdob.spring4rts.Entities.MensagemEntity;
import reactor.core.publisher.Flux;

@Component
public class MensagemReactiveRepository {
    
    private final ReactiveRedisOperations<String, MensagemEntity> redisOps;

    public MensagemReactiveRepository(ReactiveRedisOperations<String, MensagemEntity> redisOps) {
        this.redisOps = redisOps;
    }

    public Flux<MensagemEntity> save(String key,MensagemEntity mensagem) {
        return redisOps.opsForValue().set(key, mensagem).flatMapMany(v -> Flux.just(mensagem));
    }

    public Flux<MensagemEntity> findAll(String key) {
        return redisOps.ops
    }
}
