package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.GradeEntity;
import com.tetradunity.server.projections.GradeProjection;
import com.tetradunity.server.projections.NoRatedTaskProjection;
import com.tetradunity.server.projections.ShortInfoHomeworkProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<GradeEntity, Long> {
    @Query(value = "SELECT * FROM grades g " +
            "WHERE :student_id = student_id " +
            "AND :parent_id = parent_id " +
            "AND CASE " +
            "    WHEN :parent_type = 'conference' " +
            "    THEN (SELECT COUNT(*) = 1 FROM conferences c WHERE c.id = :parent_id) " +
            "    ELSE (SELECT COUNT(*) = 1 FROM education_materials e WHERE e.id = :parent_id) " +
            "END", nativeQuery = true)
    Optional<GradeEntity> findByStudentAndParent(long student_id, long parent_id, String parent_type);

    @Query(value = """
            SELECT * FROM grades
            WHERE subject_id = :subject_id
            """, nativeQuery = true)
    List<GradeEntity> findBySubject(long subject_id);

    @Query(value = """
            SELECT id, value, date, type as reason
            FROM grades
            WHERE subject_id = :subject_id
            AND content <> '' AND student_id = :student_id
            LIMIT 15 OFFSET :pos
            """, nativeQuery = true)
    List<GradeProjection> findByStudentAndSubject(long student_id, long subject_id, int pos);

    @Query(value = """
            SELECT * FROM grades
            WHERE subject_id = :subject_id AND student_id = :student_id
            """, nativeQuery = true)
    List<GradeEntity> findByStudentAndSubject(long student_id, long subject_id);

    @Query(value = """
                SELECT AVG(value) FROM grades
                WHERE parent_id = :parent_id AND type IN ('EDUCATION_MATERIAL', 'TEST')
                AND content <> ''
            """, nativeQuery = true)
    Double findAverageGradeByTask(long parent_id);

    @Query(value = """
                SELECT COUNT(value) FROM grades
                WHERE parent_id = :parent_id AND type IN ('EDUCATION_MATERIAL', 'TEST')
                AND content <> ''
            """, nativeQuery = true)
    Long findCountGradeByTask(long parent_id);

    @Query(value = "SELECT g.id, u.first_name, u.last_name, u.avatar, g.value, g.date as dispatch_time, g.attempt " +
            " FROM grades g " +
            "JOIN users u ON g.student_id = u.id " +
            "WHERE :id = g.id AND date <> 0", nativeQuery = true)
    Optional<ShortInfoHomeworkProjection> findShortInfoById(long id);

    @Query(value = "SELECT g.id, u.first_name, u.last_name, u.avatar, g.value, g.date as dispatch_time, g.attempt " +
            " FROM grades g " +
            "JOIN users u ON g.student_id = u.id " +
            "WHERE :parent_id = g.parent_id AND g.type IN ('EDUCATION_MATERIAL', 'TEST') AND g.content <> ''" +
            "LIMIT 20 OFFSET :pos", nativeQuery = true)
    List<ShortInfoHomeworkProjection> findHomeworkByParent(long parent_id, int pos);

    @Query(value = "SELECT g.id, g.value, g.date, g.type as reason FROM grades g " +
            "WHERE (g.date > :from AND g.date < :till) " +
            "AND (g.subject_id IN :subject_id) " +
            "AND (g.student_id = :student_id)", nativeQuery = true)
    List<GradeProjection> findForMonth(long student_id, long[] subject_id, long from, long till);

    @Query(value = """
                SELECT value FROM grades
                WHERE student_id = :student_id AND subject_id = :subject_id
            """, nativeQuery = true)
    List<Double> getGradesForStudentAndSubject(long student_id, long subject_id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM grades WHERE subject_id = :subject_id", nativeQuery = true)
    void delete(long subject_id);

    @Query(value = """
            SELECT e.id, e.title FROM grades g
            JOIN education_materials e ON e.id = g.parent_id
            WHERE g.subject_id = :subject_id AND g.content <> '' AND g.date = 0
            LIMIT 1
            """, nativeQuery = true)
    Optional<NoRatedTaskProjection> findNoRateTask(long subject_id);
}
