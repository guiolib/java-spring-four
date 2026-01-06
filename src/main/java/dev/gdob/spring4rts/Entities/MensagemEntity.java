package dev.gdob.spring4rts.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import dev.gdob.spring4rts.dto.MensagemDto;

@RedisHash("mensagem")
public class MensagemEntity {

    @Id
    private String mensagem;

    public MensagemEntity() {
    }

    public MensagemEntity(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public MensagemDto toDto() {
        return new MensagemDto(this.mensagem);
    }

}
