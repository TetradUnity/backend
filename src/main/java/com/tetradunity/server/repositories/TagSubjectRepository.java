package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.TagSubjectEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TagSubjectRepository extends CrudRepository<TagSubjectEntity, Long> {
    @Query(value = "select tag from tags_subject where :id = subject")
    List<String> findBySubject(long id);
}