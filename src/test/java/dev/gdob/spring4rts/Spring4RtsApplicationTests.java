package dev.gdob.spring4rts;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.utility.DockerImageName;

import com.redis.testcontainers.RedisContainer;

@SpringBootTest()
class Spring4RtsApplicationTests {

	// @TestConfiguration
	// static class TestConfig {
	// // Beans de teste podem ser definidos aqui, se necessário
	// @Bean
	// public RedisConnectionFactory lettuceConnectionFactory() {
	// return mock(RedisConnectionFactory.class);
	// }

	// @Bean
	// public StringRedisTemplate redisTemplate() {
	// return mock(StringRedisTemplate.class);
	// }

	// }

	private static RedisContainer redis = new RedisContainer(DockerImageName.parse("redis:8.4"));

	Spring4RtsApplicationTests() {
	}

	@BeforeAll
	static void setUp() {
		// Iniciar o container Redis antes dos testes
		redis.withExposedPorts(6379);
		redis.start();
		System.setProperty("spring.redis.host", redis.getHost());
		System.setProperty("spring.redis.port", redis.getMappedPort(6379).toString());
	}

	@AfterAll
	static void tearDown() {
		// Parar o container Redis após os testes
		redis.stop();
	}

	@Test
	void contextLoads() {
	}

}
