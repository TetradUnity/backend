package com.tetradunity.server.repositories;

import com.amazonaws.services.iot.model.ConfirmTopicRuleDestinationRequest;
import com.tetradunity.server.entities.EducationMaterialEntity;
import com.tetradunity.server.projections.EventProjection;
import com.tetradunity.server.projections.InfoEducationMaterialProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EducationMaterialRepository extends JpaRepository<EducationMaterialEntity, Long> {
    @Query(value = "select id, title, is_test, deadline, time_created from education_materials where :subject_id = subject_id", nativeQuery = true)
    List<InfoEducationMaterialProjection> findBySubjectId(long subject_id);

    @Query(value = "select * from education_materials where :subject_id = subject_id", nativeQuery = true)
    List<EducationMaterialEntity> findEntitiesBySubjectId(long subject_id);

    @Query(value = "SELECT id, title, ( " +
            "CASE " +
            "    WHEN is_test THEN 'test' " +
            "    ELSE 'education_material' " +
            "END) as type FROM education_materials " +
            "WHERE subject_id IN :subject_id " +
            "AND (deadline > :from AND deadline < :till)", nativeQuery = true)
    List<EventProjection> findForMonth(long[] subject_id, long from, long till);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM education_materials WHERE subject_id = :subject_id", nativeQuery = true)
    void delete(long subject_id);
}