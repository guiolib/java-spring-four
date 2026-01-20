package dev.gdob.spring4rts.dto;

import dev.gdob.spring4rts.Entities.MensagemEntity;

public record MensagemDto (String id, String mensagem) {

    public MensagemDto(String mensagem) {
        this(null, mensagem);
    }

    public MensagemEntity toEntity() {
        MensagemEntity entity = new MensagemEntity(this.id, this.mensagem);
        return entity;
    }
}
