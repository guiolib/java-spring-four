# Diretrizes de Desenvolvimento: Java 25 & Spring Boot 4 Reativo

---

## 1. Padrões de Código Reativo (WebFlux)
- Todos os endpoints e métodos de serviço devem ser não-bloqueantes.
- Utilize operadores reativos adequados (`map`, `flatMap`, `filter`, `onErrorResume`).
- Para fluxos de streaming de dados em tempo real ou IA, utilize `Flux<ServerSentEvent<T>>` ou `Flux<String>` com `MediaType.TEXT_EVENT_STREAM_VALUE`.

---

## 2. Spring AI & Integração de Modelos
- Utilize `ChatModel` ou `StreamingChatModel` para chamadas de IA.
- Injete o `ChatModel` via construtor (injeção de dependência Spring).
- Em cenários reativos, prefira o método `stream(Prompt prompt)` do `ChatModel` que retorna `Flux<ChatResponse>`.

---

## 3. Gestão de Estado com Redis
- Utilize `ReactiveRedisTemplate` ou `ReactiveStringRedisTemplate`.
- Respeite as conexões assíncronas do Lettuce/Redis Reativo.

---

## 4. Estilo Java 25
- Utilize sintaxe moderna (Record Patterns, Switch Expressions, Text Blocks `"""..."""`).
- Utilize construtores explícitos e evite acoplamento direto de código legado.
