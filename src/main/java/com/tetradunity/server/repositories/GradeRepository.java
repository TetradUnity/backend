package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.GradeEntity;
import com.tetradunity.server.projections.GradeProjection;
import com.tetradunity.server.projections.ShortInfoHomeworkProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends CrudRepository<GradeEntity, Long> {
    @Query(value = "SELECT * FROM grades WHERE :student_id = student_id AND :parent_id = parent_id", nativeQuery = true)
    Optional<GradeEntity> findByStudentAndParent(long student_id, long parent_id);

    @Query(value = "SELECT g.id, u.first_name, u.last_name, u.avatar, g.value, g.time_edited_end as dispatch_time, g.attempt " +
            " FROM grades g " +
            "JOIN users u ON g.student_id = u.id " +
            "WHERE :parent_id = g.parent_id", nativeQuery = true)
    List<ShortInfoHomeworkProjection> findByParent(long parent_id);

    @Query(value = "SELECT g.id, u.first_name, u.last_name, u.avatar, g.value, g.time_edited_end as dispatch_time, g.attempt " +
            " FROM grades g " +
            "JOIN users u ON g.student_id = u.id " +
            "WHERE :id = g.id", nativeQuery = true)
    Optional<ShortInfoHomeworkProjection> findShortInfoById(long id);

    @Query(value = "SELECT g.id, g.value, g.date, " +
            "(CASE " +
            "   WHEN (SELECT COUNT(*) = 1 FROM conferences c WHERE c.id = g.parent_id) " +
            "   THEN 'conference' " +
            "   WHEN (SELECT COUNT(*) = 1 FROM education_materials e WHERE e.id = g.parent_id AND e.is_test) " +
            "   THEN 'test' " +
            "   ELSE 'education_material' END " +
            ") as reason FROM grades g " +
            "WHERE (g.date > :from AND g.date < :till) " +
            "AND (g.subject_id IN :subject_id) " +
            "AND (g.student_id = :student_id)", nativeQuery = true)
    List<GradeProjection> findForMonth(long student_id, long[] subject_id, long from, long till);
}
