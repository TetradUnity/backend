package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.StudentSubjectEntity;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface StudentSubjectRepository extends JpaRepository<StudentSubjectEntity, Long> {
    @Query(value = "select * from student_subjects where student_id = :student_id and subject_id = :subject_id", nativeQuery = true)
    Optional<StudentSubjectEntity> findByStudent_idAndSubject_id(long student_id, long subject_id);
    @Query(value = "select * from student_subjects where subject_id = :subject_id", nativeQuery = true)
    List<StudentSubjectEntity> findBySubject_id(long subject_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM student_subjects WHERE subject_id = :subject_id", nativeQuery = true)
    void delete(long subject_id);
}
