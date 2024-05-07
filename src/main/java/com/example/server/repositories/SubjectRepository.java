package com.example.server.repositories;

import com.example.server.entities.SubjectEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Long> {
    Optional<SubjectEntity> findById(Long id);
}
