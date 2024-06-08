package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.ConferenceEntity;
import org.springframework.data.repository.CrudRepository;

public interface ConferenceRepository extends CrudRepository<ConferenceEntity, Long> {

}