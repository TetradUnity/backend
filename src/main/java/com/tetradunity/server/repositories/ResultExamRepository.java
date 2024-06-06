package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.ResultExamEntity;
import com.tetradunity.server.models.Candidate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ResultExamRepository extends CrudRepository<ResultExamEntity, Long> {
    Optional<ResultExamEntity> findById(long id);

    @Query(value = "select * from results_test where parentId = :subjectId", nativeQuery = true)
    List<ResultExamEntity> findRequests(long subjectId);

    @Query(value = "select count(*) from results_test where email = :email and parent_id = :id", nativeQuery = true)
    Long countByEmailAndSubjectId(String email, long id);

    @Query(value = "select * from results_test where :uid = uid", nativeQuery = true)
    Optional<ResultExamEntity> findByUID(String uid);

    @Query(value = "select id, email, first_name, last_name, result, duration from results_test where :id = parent_id", nativeQuery = true)
    List<Candidate> findCandidatesByParent_id(long id);

    @Query(value = "select * from subject", nativeQuery = true)

    default boolean existsByEmailAndSubjectId(String email, long id) {
        Long count = countByEmailAndSubjectId(email, id);
        return count > 0;
    }
}