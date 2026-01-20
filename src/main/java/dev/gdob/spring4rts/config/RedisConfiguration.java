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
    ReactiveRedisOperations<String, MensagemDto> redisOperations(ReactiveRedisConnectionFactory factory) {
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
    public ReactiveRedisMessageListenerContainer redisMessageListenerContainer(MensagemReactiveRepository mensagemRepo,
            ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ObjectMapper objectMapper) throws IOException {
        ReactiveRedisMessageListenerContainer container = new ReactiveRedisMessageListenerContainer(
                reactiveRedisConnectionFactory);
                // container.receive(ChannelTopic.of("mensagem"));
        container.receive(ChannelTopic.of("mensagem"))
                .map(p -> p.getMessage())
                .map(m -> {
                    MensagemEntity mensagem = objectMapper.readValue(m, MensagemEntity.class);
                    return mensagem;
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException()))
                .flatMap(p -> mensagemRepo.save(p))
                .subscribe(c -> log.info("Mensagem saved."));
        return container;
    }

}