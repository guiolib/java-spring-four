# AGENTS.md - Diretrizes para Assistentes de IA

Este repositório é um projeto Java de tempo real construído com **Spring Boot 4**, **Java 25**, **Spring WebFlux (Reativo)**, **gRPC** e **Spring Data Redis Reactive**.

---

## 🚀 Arquitetura e Tecnologias Core

- **Linguagem**: Java 25 (utilize recursos modernos como Records, Pattern Matching, Sealed Interfaces e Virtual Threads/Reactive).
- **Framework**: Spring Boot 4.0.7 (WebFlux reativo com servidor Jetty; Tomcat desativado).
- **Tempo Real & Comunicação**:
  - WebFlux (mono/flux em rotas HTTP/REST)
  - gRPC (`spring-grpc-spring-boot-starter`)
  - Reactive Redis (`spring-boot-starter-data-redis-reactive`)
- **Containers & Testes**: Podman/Docker (`containers/redis/ContainerFile`), Testcontainers com Redis (`testcontainers-redis`).
- **Gerenciador de Build**: Maven Wrapper (`./mvnw`).

---

## 📁 Estrutura de Pacotes

O pacote base é `dev.gdob.spring4rts`.
- `dev.gdob.spring4rts.config` - Configurações de Beans (Redis, gRPC, WebFlux, Spring AI).
- `dev.gdob.spring4rts.controllers` - Controladores REST reativos (retornando `Mono<T>` ou `Flux<T>`).
- `dev.gdob.spring4rts.sevice` - Serviços de negócio reativos.
- `dev.gdob.spring4rts.repository` - Repositórios de dados (Redis Reactive).
- `dev.gdob.spring4rts.Entities` / `dto` - Modelos de domínio e DTOs.

---

## 🛠️ Comandos Principais

```bash
# Compilação e testes unitários/integração
./mvnw clean test

# Executar a aplicação Spring Boot em desenvolvimento
./mvnw spring-boot:run

# Compilar sem executar testes
./mvnw clean package -DskipTests
```

---

## ⚠️ Regras e Boas Práticas de Código

1. **Programação Reativa Não-Bloqueante**:
   - NUNCA utilize chamadas bloqueantes (ex: `block()`, `Future.get()`, `Thread.sleep()`) em threads da Event Loop do WebFlux.
   - Retorne sempre tipos reativos (`Mono<T>` ou `Flux<T>`) nos serviços e controladores.

2. **Imutabilidade e DTOs**:
   - Prefira utilizar `record` do Java 25 para DTOs e payloads de requisição/resposta.

3. **Verificação de Mudanças**:
   - Sempre execute `./mvnw clean test-compile` após modificar o `pom.xml` ou classes principais.
