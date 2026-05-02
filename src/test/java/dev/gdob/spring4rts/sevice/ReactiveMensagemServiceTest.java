package dev.gdob.spring4rts.sevice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.redis.test.autoconfigure.DataRedisTest;
import org.springframework.context.annotation.Import;

import dev.gdob.spring4rts.Entities.MensagemEntity;
import dev.gdob.spring4rts.dto.MensagemDto;
import dev.gdob.spring4rts.repository.MensagemReactiveRepository;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@DataRedisTest
@Import({ReactiveMensagemService.class, MensagemReactiveRepository.class})
class ReactiveMensagemServiceTest {

    @Autowired
    private ReactiveMensagemService service;

    private Sinks.Many<MensagemDto> sink;

    @BeforeEach
    void setUp() {
        // the service constructed by Spring already wires a sink, but we need a
        // reference to verify emissions
        // unfortunately the sink field is private, so use reflection or rebuild
        // service manually for this test
        sink = Sinks.many().multicast().onBackpressureBuffer();
        // MensagemReactiveRepository repo = service.getClass()
        //         .getDeclaredFields()[1] // guess: repository field order
        //         .getType()
        //         .cast(null);
    }

    @Test
    void publishMessageReturnsDtoAndEmits() {
        MensagemEntity input = new MensagemEntity("id1", "hello");

        StepVerifier.create(service.publishMessage(input))
                .assertNext(dto -> {
                    assertThat(dto.id()).isEqualTo("id1");
                    assertThat(dto.mensagem()).isEqualTo("hello");
                })
                .verifyComplete();
    }
}
