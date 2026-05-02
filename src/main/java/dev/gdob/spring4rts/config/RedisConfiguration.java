package dev.gdob.spring4rts.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import dev.gdob.spring4rts.Entities.MensagemEntity;
import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.repository.MensagemReactiveRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableRedisRepositories

class RedisConfiguration {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RedisConfiguration.class);

    @Bean
    public RedisConnectionFactory lettuceConnectionFactory() {
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration("localhost", 6379));
    }

    @Bean
    public StringRedisTemplate redisTemplate() {
        StringRedisTemplate template = new StringRedisTemplate(lettuceConnectionFactory());
        // explicitly enable transaction support
        template.setEnableTransactionSupport(true);
        return template;
    }

    @Bean
    public ReactiveRedisOperations<String, MensagemDto> redisOperations(ReactiveRedisConnectionFactory factory) {
        JacksonJsonRedisSerializer<MensagemDto> serializer = new JacksonJsonRedisSerializer(MensagemDto.class);
        RedisSerializationContext.RedisSerializationContextBuilder<String, MensagemDto> builder = RedisSerializationContext
                .newSerializationContext(serializer);

        RedisSerializationContext<String, MensagemDto> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }

    @Bean
    public ReactiveRedisTemplate<String, MensagemEntity> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory,
                RedisSerializationContext.<String, MensagemEntity>newSerializationContext(new StringRedisSerializer())
                        .hashValue(new JacksonJsonRedisSerializer<>(MensagemEntity.class)).build());
    }

    @Bean
    public ReactiveRedisMessageListenerContainer redisMessageListenerContainer(
            MensagemReactiveRepository mensagemRepo,
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory,
            ObjectMapper objectMapper,
            Sinks.Many<MensagemDto> sink) throws IOException {
        ReactiveRedisMessageListenerContainer container =
                new ReactiveRedisMessageListenerContainer(reactiveRedisConnectionFactory);

        container.receive(ChannelTopic.of("mensagem"))
                .map(p -> p.getMessage())
                .map(m -> {
                    return objectMapper.readValue(m, MensagemEntity.class);
                })
                .flatMap(mensagem -> mensagemRepo.save(mensagem)
                        .map(MensagemEntity::toDto)
                        .doOnNext(sink::tryEmitNext))
                .subscribe(c -> log.info("Mensagem saved and emitted."));

        return container;
    }

    @Bean
    public Sinks.Many<MensagemDto> sink() {
        return Sinks.many().multicast().onBackpressureBuffer();
    }

}