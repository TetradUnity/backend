package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.SubjectEntity;
import com.tetradunity.server.models.SubjectAnnounceDB;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Long> {
    Optional<SubjectEntity> findById(long id);

    @Query(value = "select id, title, teacher_id, short_description, time_exam_end, time_start from subjects where time_exam_end > UNIX_TIMESTAMP() * 1000 " +
            "limit 10 offset :pos", nativeQuery = true)
    List<SubjectAnnounceDB> findAccessAnnounceSubject(int pos);

    @Query(value = "SELECT s.id, s.title, s.teacher_id, s.short_description, s.time_exam_end, s.time_start FROM subjects s " +
            "JOIN tags_subject t ON s.id = t.subject_id " +
            "WHERE (s.time_exam_end > UNIX_TIMESTAMP() * 1000) " +
            "AND t.tag IN :tags " +
            "GROUP BY t.tag, s.id, s.title, s.teacher_id, s.short_description, s.time_exam_end, s.time_start " +
            "HAVING COUNT(t.tag) = :tag_count " +
            "limit 10 offset :pos ", nativeQuery = true)
    List<SubjectAnnounceDB> findAccessAnnounceSubject(int pos, String[] tags, int tag_count);

    @Query(value = "select id, title, teacher_id, short_description, time_exam_end, time_start from subjects where time_exam_end > UNIX_TIMESTAMP() * 1000 " +
            "AND ((:has_exam = true AND exam <> '') OR (:has_exam = false AND exam = '')) " +
            "limit 10 offset :pos", nativeQuery = true)
    List<SubjectAnnounceDB> findAccessAnnounceSubject(int pos, boolean has_exam);

    @Query(value = "SELECT s.id, s.title, s.teacher_id, s.short_description, s.time_exam_end, s.time_start FROM subjects s " +
            "JOIN tags_subject t ON s.id = t.subject_id " +
            "WHERE (s.time_exam_end > UNIX_TIMESTAMP() * 1000) " +
            "AND t.tag IN :tags " +
            "AND ((:has_exam = true AND s.exam <> '') OR (:has_exam = false AND s.exam = '')) " +
            "GROUP BY t.tag, s.id, s.title, s.teacher_id, s.short_description, s.time_exam_end, s.time_start " +
            "HAVING COUNT(t.tag) = :tag_count " +
            "limit 10 offset :pos ", nativeQuery = true)
    List<SubjectAnnounceDB> findAccessAnnounceSubject(int pos, String[] tags, boolean has_exam, int tag_count);

    default List<SubjectAnnounceDB> findAccessAnnounceSubject(int pos, String[] tags) {
        return findAccessAnnounceSubject(pos, tags, tags.length);
    }

    default List<SubjectAnnounceDB> findAccessAnnounceSubject(int pos, String[] tags, boolean has_exam) {
        return findAccessAnnounceSubject(pos, tags, has_exam, tags.length);
    }
}
