package com.tetradunity.server.repositories;

import com.amazonaws.services.iot.model.ConfirmTopicRuleDestinationRequest;
import com.tetradunity.server.entities.EducationMaterialEntity;
import com.tetradunity.server.projections.InfoEducationMaterialProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EducationMaterialRepository extends CrudRepository<EducationMaterialEntity, Long> {
    @Query(value = "select id, title, is_test, deadline from education_materials where :subject_id = subject_id", nativeQuery = true)
    List<InfoEducationMaterialProjection> findBySubjectId(long subject_id);
}