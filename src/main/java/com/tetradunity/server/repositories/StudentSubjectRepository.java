package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.StudentSubjectEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudentSubjectRepository extends CrudRepository<StudentSubjectEntity, Long> {
    Optional<StudentSubjectEntity> findByStudentIdAndSubjectId(long studentId, long subjectId);
}
