package com.tetradunity.server.repositories;

import com.amazonaws.services.iot.model.ConfirmTopicRuleDestinationRequest;
import com.tetradunity.server.entities.EducationMaterialEntity;
import com.tetradunity.server.projections.EventProjection;
import com.tetradunity.server.projections.InfoEducationMaterialProjection;
import com.tetradunity.server.projections.NoRatedTaskProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface EducationMaterialRepository extends JpaRepository<EducationMaterialEntity, Long> {
    @Query(value = """
            SELECT id, title, is_test, deadline, time_created FROM education_materials
            WHERE :subject_id = subject_id
            ORDER BY time_created DESC
            LIMIT 15 OFFSET :pos""", nativeQuery = true)
    List<InfoEducationMaterialProjection> findBySubjectId(long subject_id, int pos);

    @Query(value = "select * from education_materials where :subject_id = subject_id", nativeQuery = true)
    List<EducationMaterialEntity> findEntitiesBySubjectId(long subject_id);

    @Query(value = """
            SELECT id, title, time_created as date, (
            CASE
                WHEN is_test THEN 'test'
                ELSE 'education_material'
            END) as type FROM education_materials
            WHERE subject_id IN :subject_id
            AND (time_created > :from AND time_created < :till)
            UNION
            SELECT id, link as title, date, 'conference' as type FROM conferences
            WHERE subject_id IN :subject_id
            AND (date > :from AND date < :till)""", nativeQuery = true)
    List<EventProjection> findForMonth(long[] subject_id, long from, long till);

    @Query(value = """
            SELECT id, title FROM education_materials
            WHERE subject_id = :subject_id AND deadline > UNIX_TIMESTAMP() * 1000
            LIMIT 1
            """, nativeQuery = true)
    Optional<NoRatedTaskProjection> findNoEndedTask(long subject_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM education_materials WHERE subject_id = :subject_id", nativeQuery = true)
    void delete(long subject_id);
}