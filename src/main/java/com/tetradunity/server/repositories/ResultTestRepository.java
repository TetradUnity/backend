package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.ResultTestEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ResultTestRepository extends CrudRepository<ResultTestEntity, Long> {
    @Query(value = "select * from results_test where parentId = :subject", nativeQuery = true)
    public List<ResultTestEntity> findRequests(long subjectId);
}
