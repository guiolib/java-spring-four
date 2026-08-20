---
name: spring-dev
description: Runbook operacional para build, execução, testes e containerização do projeto Spring Boot 4 com Java 25.
---

# Skill: Desenvolvimento Spring Boot 4 Reativo

Esta skill fornece procedimentos padrão para compilar, testar e executar a aplicação Spring Boot 4 com suporte a gRPC, Redis Reativo e Spring AI.

---

## 1. Compilação e Verificação de Projeto

Para verificar a integridade da compilação Maven:

```bash
./mvnw clean test-compile
```

Para executar o conjunto completo de testes (unitários e de integração com Testcontainers):

```bash
./mvnw test
```

---

## 2. Executando a Aplicação Localmente

Para iniciar a aplicação Spring Boot em modo dev:

```bash
./mvnw spring-boot:run
```

Ou usando a imagem do Redis com Podman/Docker:

```bash
podman build -t redis-rts -f containers/redis/ContainerFile containers/redis/
podman run -d -p 6379:6379 redis-rts
```

---

## 3. Endpoints Reativos e Verificação da API

- **Health Check Actuator**: `GET http://localhost:8080/actuator/health`
- **Endpoint de IA (Prompt)**: `POST http://localhost:8080/api/ai/generate`
- **Endpoint de IA Stream**: `GET http://localhost:8080/api/ai/stream?message=...`
