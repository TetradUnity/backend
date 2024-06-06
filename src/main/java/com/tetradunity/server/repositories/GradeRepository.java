package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.GradeEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface GradeRepository extends CrudRepository<GradeEntity, Long> {
    @Query(value = "SELECT * from grades WHERE :student_id = student_id AND :parent_id = parent_id", nativeQuery = true)
    Optional<GradeEntity> findByStudent_idAndParent_id(long student_id, long parent_id);
}
