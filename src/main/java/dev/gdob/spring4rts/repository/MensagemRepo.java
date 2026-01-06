package dev.gdob.spring4rts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import dev.gdob.spring4rts.Entities.MensagemEntity;

@Repository
public interface MensagemRepo extends CrudRepository<MensagemEntity, String> {}
