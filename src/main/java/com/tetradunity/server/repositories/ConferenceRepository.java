package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.ConferenceEntity;
import com.tetradunity.server.projections.ConferenceRemindProjection;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ConferenceRepository extends JpaRepository<ConferenceEntity, Long> {

    @Query(value = """
                SELECT s.title, u.email as student_emails FROM conferences c
                JOIN subjects s ON s.id = c.subject_id
                LEFT JOIN student_subjects st ON st.subject_id = c.subject_id
                LEFT JOIN users u ON u.id = st.student_id
                WHERE c.date < UNIX_TIMESTAMP() * 1000 + 72000
                AND c.date > UNIX_TIMESTAMP() * 1000 + 60000
                GROUP BY s.title
            """, nativeQuery = true)
    List<ConferenceRemindProjection> conferencesRemind();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM conferences WHERE subject_id = :subject_id", nativeQuery = true)
    void delete(long subject_id);
}