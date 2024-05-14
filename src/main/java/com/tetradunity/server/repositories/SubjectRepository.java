package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.SubjectEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SubjectRepository extends CrudRepository<SubjectEntity, Long> {
    Optional<SubjectEntity> findById(long id);
    
    @Query("select * from subjects where examEnd > UNIX_TIMESTAMP() * 1000")
    List<SubjectEntity> findAccessAnnounceSubject();
}
