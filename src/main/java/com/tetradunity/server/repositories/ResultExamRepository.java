package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.ResultExamEntity;
import com.tetradunity.server.models.users.Candidate;
import com.tetradunity.server.projections.CandidateProjection;
import com.tetradunity.server.services.JSONService;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ResultExamRepository extends JpaRepository<ResultExamEntity, Long> {
    Optional<ResultExamEntity> findById(long id);

    @Query(value = "select * from results_test where parentId = :subjectId", nativeQuery = true)
    List<ResultExamEntity> findRequests(long subjectId);

    @Query(value = "select count(*) from results_test where email = :email and parent_id = :id", nativeQuery = true)
    Long countByEmailAndSubjectId(String email, long id);

    @Query(value = "select * from results_test where :uid = uid", nativeQuery = true)
    Optional<ResultExamEntity> findByUID(String uid);

    @Query(value = """
            SELECT id, email, first_name, last_name, result, duration FROM results_test
            WHERE :parent_id = parent_id
            AND (:all OR (result > :passing_grade))""", nativeQuery = true)
    List<CandidateProjection> findCandidatesByParent_id(long parent_id, boolean all, int passing_grade);

    @Query(value = "select * from subject", nativeQuery = true)

    default boolean existsByEmailAndSubjectId(String email, long id) {
        Long count = countByEmailAndSubjectId(email, id);
        return count > 0;
    }

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM results_test WHERE parent_id = :subject_id", nativeQuery = true)
    void deleteBySubjectId(long subject_id);
}