package dev.gdob.spring4rts.dto;

import dev.gdob.spring4rts.Entities.MensagemEntity;

public record MensagemDto (String mensagem) {

    public MensagemEntity toEntity() {
        MensagemEntity entity = new MensagemEntity(this.mensagem);
        return entity;
    }
}
