package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.GradeEntity;
import com.tetradunity.server.models.ShortInfoHomework;
import com.tetradunity.server.projections.ShortInfoHomeworkProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends CrudRepository<GradeEntity, Long> {
    @Query(value = "SELECT * FROM grades WHERE :student_id = student_id AND :parent_id = parent_id", nativeQuery = true)
    Optional<GradeEntity> findByStudent_idAndParent_id(long student_id, long parent_id);

    @Query(value = "SELECT g.id, u.first_name, u.last_name, u.avatar, g.value, g.time_edited_end as dispatch_time, g.attempt" +
            " FROM grades g " +
            "JOIN users u ON g.student_id = u.id" +
            "WHERE :parent_id = g.parent_id", nativeQuery = true)
    List<ShortInfoHomeworkProjection> findByParent(long parent_id);

    @Query(value = "SELECT g.id, u.first_name, u.last_name, u.avatar, g.value, g.time_edited_end as dispatch_time, g.attempt" +
            " FROM grades g " +
            "JOIN users u ON g.student_id = u.id" +
            "WHERE :id = g.id", nativeQuery = true)
    Optional<ShortInfoHomeworkProjection> findShortInfoById(long id);
}
