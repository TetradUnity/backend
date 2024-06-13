package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.models.subjects.SubjectFilter;
import com.tetradunity.server.projections.AnnounceSubjectProjection;
import com.tetradunity.server.projections.ShortInfoStudentSubjectProjection;
import com.tetradunity.server.projections.ShortInfoTeacherSubjectProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Long> {

    Optional<SubjectEntity> findById(long id);

    @Query(value = """
            SELECT s.id, s.title, s.teacher_id, s.short_description, s.time_exam_end, s.time_start, s.banner
            FROM subjects s
            LEFT JOIN (
                SELECT subject_id
                FROM tags_subject
                WHERE :count_tags = 0 OR tag IN :tags
                GROUP BY subject_id
                HAVING :count_tags = 0 OR COUNT(DISTINCT tag) >= :count_tags
            ) t ON s.id = t.subject_id AND :count_tags > 0
            JOIN users u ON u.id = s.teacher_id
            WHERE (s.time_exam_end > UNIX_TIMESTAMP() * 1000)
            AND (:title IS NULL OR s.title LIKE %:title%)
            AND ((:has_exam IS NULL) OR (:has_exam = true AND s.exam <> '') OR (:has_exam = false AND s.exam = ''))
            AND (:first_name_teacher IS NULL OR u.first_name LIKE :first_name_teacher%)
            AND (:last_name_teacher IS NULL OR u.last_name LIKE :last_name_teacher%)
            AND (:count_tags = 0 OR t.subject_id IS NOT NULL)
            LIMIT 12 OFFSET :pos""", nativeQuery = true)
    List<AnnounceSubjectProjection> findAccessAnnounceSubject(int pos, String title, List<String> tags, int count_tags, Boolean has_exam,
                                                              String first_name_teacher, String last_name_teacher);

    @Query(value = """
            SELECT COUNT(*)
            FROM subjects s
            LEFT JOIN (
                SELECT subject_id
                FROM tags_subject
                WHERE :count_tags = 0 OR tag IN :tags
                GROUP BY subject_id
                HAVING :count_tags = 0 OR COUNT(DISTINCT tag) >= :count_tags
            ) t ON s.id = t.subject_id
            JOIN users u ON u.id = s.teacher_id
            WHERE (s.time_exam_end > UNIX_TIMESTAMP() * 1000)
            AND (:title IS NULL OR s.title LIKE %:title%)
            AND ((:has_exam IS NULL) OR (:has_exam = true AND s.exam <> '') OR (:has_exam = false AND s.exam = ''))
            AND (:first_name_teacher IS NULL OR u.first_name LIKE :first_name_teacher%)
            AND (:last_name_teacher IS NULL OR u.last_name LIKE :last_name_teacher%)""", nativeQuery = true)
    int countAnnounceSubject(String title, List<String> tags, int count_tags, Boolean has_exam, String first_name_teacher, String last_name_teacher);

    default List<AnnounceSubjectProjection> findAccessAnnounceSubject(int pos, SubjectFilter filter) {
        if (filter == null) {
            return findAccessAnnounceSubject(pos, null, new ArrayList<String>(), 0, null, null, null);
        }

        List<String> tags = filter.getTags();
        return findAccessAnnounceSubject(pos, filter.getTitle(), tags == null ? new ArrayList<String>() : tags,
                tags == null ? 0 : tags.size(), filter.getHas_exam(), filter.getFirst_name_teacher(), filter.getLast_name_teacher());
    }

    default int countAnnounceSubject(SubjectFilter filter) {
        double res;
        if (filter == null) {
            res = countAnnounceSubject(null, new ArrayList<String>(), 0, null, null, null) / 12d;
        } else {
            List<String> tags = filter.getTags();
            res = countAnnounceSubject(filter.getTitle(), tags == null ? new ArrayList<String>() : tags, tags == null ? 0 : tags.size(),
                    filter.getHas_exam(), filter.getFirst_name_teacher(), filter.getLast_name_teacher()) / 10d;
        }
        return res > (int) res ? (int) res + 1 : (int) res;
    }

    @Query(value = """
            SELECT s.id, s.banner, s.title,
            (CASE
                WHEN s.time_exam_end > UNIX_TIMESTAMP() * 1000 THEN s.time_exam_end
                WHEN s.is_start AND s.time_start > UNIX_TIMESTAMP() * 1000 THEN s.time_start
                WHEN s.time_start > UNIX_TIMESTAMP() * 1000 THEN 0
                ELSE (SELECT COUNT(*) FROM student_subjects st WHERE st.subject_id = s.id)
            END) AS info,
            (CASE
                WHEN s.time_exam_end > UNIX_TIMESTAMP() * 1000 THEN 0
                WHEN s.is_start AND s.time_start > UNIX_TIMESTAMP() * 1000 THEN 2
                WHEN s.time_start > UNIX_TIMESTAMP() * 1000 THEN 1
                ELSE 3
            END) AS type FROM subjects s WHERE s.teacher_id = :teacher_id
            """, nativeQuery = true)
    List<ShortInfoTeacherSubjectProjection> findTeacherSubjects(long teacher_id);

    @Query(value = """
            SELECT s.id, s.banner, u.first_name, u.last_name, s.title,
            (CASE
                WHEN s.time_start > UNIX_TIMESTAMP() * 1000 THEN s.time_start
                ELSE (SELECT COUNT(*) FROM student_subjects st WHERE st.subject_id = s.id)
            END) AS info,
            (CASE
                WHEN s.time_start > UNIX_TIMESTAMP() * 1000 THEN 2
                ELSE 3
            END) AS type FROM student_subjects st
            JOIN subjects s ON st.subject_id = s.id
            JOIN users u ON u.id = s.teacher_id
            WHERE st.student_id = :student_id
            AND s.is_start
            """, nativeQuery = true)
    List<ShortInfoStudentSubjectProjection> findStudentSubjects(long student_id);

    @Query(value = """
            SELECT COUNT(*) FROM subjects
            WHERE id = :subject_id AND teacher_id = :teacher_id
            """, nativeQuery = true)
    long countByTeacherAndSubject(long teacher_id, long subject_id);

    default boolean existsSubjectByTeacherAndSubject(long teacher_id, long subject_id){
        return countByTeacherAndSubject(teacher_id, subject_id) == 1;
    }
}