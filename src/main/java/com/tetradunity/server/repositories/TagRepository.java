package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.TagEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends CrudRepository<TagEntity, Long> {
    @Query(value = "select * from tags", nativeQuery = true)
    List<TagEntity> findAll();
    Optional<TagEntity> findByTag(String tag);
}
