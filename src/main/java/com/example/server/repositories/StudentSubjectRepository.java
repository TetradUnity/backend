package com.example.server.repositories;

import com.example.server.entities.StudentSubjectEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StudentSubjectRepository extends CrudRepository<StudentSubjectEntity, Long> {
    Optional<StudentSubjectEntity> findByStudentIdAndSubjectId(long studentId, long subjectId);
}
