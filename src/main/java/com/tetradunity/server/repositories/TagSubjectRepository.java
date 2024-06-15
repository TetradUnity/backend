package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.TagSubjectEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TagSubjectRepository extends JpaRepository<TagSubjectEntity, Long> {
    @Query(value = "select * from tags_subject where :subject_id = subject_id", nativeQuery = true)
    List<TagSubjectEntity> findBySubject(long subject_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tags_subject WHERE subject_id = :subject_id", nativeQuery = true)
    void delete(long subject_id);
}