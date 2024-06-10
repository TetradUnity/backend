package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.ConferenceEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface ConferenceRepository extends JpaRepository<ConferenceEntity, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM conferences WHERE subject_id = :subject_id", nativeQuery = true)
    void delete(long subject_id);
}