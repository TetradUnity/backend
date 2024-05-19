package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.ResultTestEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ResultTestRepository extends CrudRepository<ResultTestEntity, Long> {
    @Query(value = "select * from results_test where parentId = :subjectId", nativeQuery = true)
    List<ResultTestEntity> findRequests(long subjectId);

    @Query(value = "select count(*) from results_test where email = :email and parent_id = :id", nativeQuery = true)
    Long countByEmailAndSubjectId(String email, long id);

    default boolean existsByEmailAndSubjectId(String email, long id) {
        Long count = countByEmailAndSubjectId(email, id);
        return count > 0;
    }
}
