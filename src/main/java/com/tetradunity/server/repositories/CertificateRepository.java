package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.CertificateEntity;
import com.tetradunity.server.projections.CertificateProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CertificateRepository extends CrudRepository<CertificateEntity, Long> {
    @Query(value = """
                SELECT s.title, c.type, s.uid FROM certificates c
                JOIN subjects s ON s.id = c.subject_id
                WHERE c.student_id = :student_id
            """, nativeQuery = true)
    List<CertificateProjection> findAllCertificates(long student_id);

    @Query(value = """
                SELECT * FROM certificates
                WHERE uid = :uid
            """, nativeQuery = true)
    Optional<CertificateEntity> findByUid(UUID uid);

    default boolean certificateExists(UUID uid){
        return findByUid(uid).isPresent();
    }
}
