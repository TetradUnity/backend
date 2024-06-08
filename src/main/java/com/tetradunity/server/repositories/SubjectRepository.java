package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.models.CalendarFilter;
import com.tetradunity.server.models.SubjectFilter;
import com.tetradunity.server.projections.AnnounceSubjectProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Long> {
    Optional<SubjectEntity> findById(long id);

    @Query(value = "SELECT s.id, s.title, s.teacher_id, s.short_description, s.time_exam_end, s.time_start, s.banner " +
            "FROM subjects s " +
            "LEFT JOIN ( " +
            "    SELECT subject_id " +
            "    FROM tags_subject " +
            "    WHERE :count_tags = 0 OR tag IN :tags " +
            "    GROUP BY subject_id " +
            "    HAVING :count_tags = 0 OR COUNT(DISTINCT tag) >= :count_tags" +
            ") t ON s.id = t.subject_id " +
            "JOIN users u ON u.id = s.teacher_id " +
            "WHERE (s.time_exam_end > UNIX_TIMESTAMP() * 1000) " +
            "AND (:title IS NULL OR s.title LIKE %:title%) " +
            "AND ((:has_exam IS NULL) OR (:has_exam = true AND s.exam <> '') OR (:has_exam = false AND s.exam = '')) " +
            "AND (:first_name_teacher IS NULL OR u.first_name LIKE :first_name_teacher%) " +
            "AND (:last_name_teacher IS NULL OR u.last_name LIKE :last_name_teacher%) " +
            "LIMIT 10 OFFSET :pos", nativeQuery = true)
    List<AnnounceSubjectProjection> findAccessAnnounceSubject(int pos, String title, List<String> tags, int count_tags, Boolean has_exam,
                                                              String first_name_teacher, String last_name_teacher);

    @Query(value = "SELECT COUNT(*) " +
            "FROM subjects s " +
            "LEFT JOIN ( " +
            "    SELECT subject_id " +
            "    FROM tags_subject " +
            "    WHERE :count_tags = 0 OR tag IN :tags " +
            "    GROUP BY subject_id " +
            "    HAVING :count_tags = 0 OR COUNT(DISTINCT tag) >= :count_tags" +
            ") t ON s.id = t.subject_id " +
            "JOIN users u ON u.id = s.teacher_id " +
            "WHERE (s.time_exam_end > UNIX_TIMESTAMP() * 1000) " +
            "AND (:title IS NULL OR s.title LIKE %:title%) " +
            "AND ((:has_exam IS NULL) OR (:has_exam = true AND s.exam <> '') OR (:has_exam = false AND s.exam = '')) " +
            "AND (:first_name_teacher IS NULL OR u.first_name LIKE :first_name_teacher%) " +
            "AND (:last_name_teacher IS NULL OR u.last_name LIKE :last_name_teacher%) ", nativeQuery = true)
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
            res = countAnnounceSubject(null, new ArrayList<String>(), 0, null, null, null) / 10d;
        } else {
            List<String> tags = filter.getTags();
            res = countAnnounceSubject(filter.getTitle(), tags == null ? new ArrayList<String>() : tags, tags == null ? 0 : tags.size(),
                    filter.getHas_exam(), filter.getFirst_name_teacher(), filter.getLast_name_teacher()) / 10d;
        }
        return res > (int) res ? (int) res + 1 : (int) res;
    }
}