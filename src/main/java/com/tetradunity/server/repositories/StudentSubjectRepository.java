package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.StudentSubjectEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSubjectRepository extends CrudRepository<StudentSubjectEntity, Long> {
    @Query(value = "select * from student_subjects where student_id = :student_id and subject_id = :subject_id", nativeQuery = true)
    Optional<StudentSubjectEntity> findByStudent_idAndSubject_id(long student_id, long subject_id);
    @Query(value = "select * from student_subjects where subject_id = :subject_id", nativeQuery = true)
    List<StudentSubjectEntity> findBySubject_id(long subject_id);
}
