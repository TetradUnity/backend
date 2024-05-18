package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.SubjectEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Long> {
    Optional<SubjectEntity> findById(long id);
    
    @Query(value = "select * from subjects where examEnd > UNIX_TIMESTAMP() * 1000 " +
            "limit 10 offset (:page - 1) * 10", nativeQuery = true)
    List<SubjectEntity> findAccessAnnounceSubject(int page);
}
