package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.TagEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends CrudRepository<TagEntity, Long> {
    Optional<TagEntity> findByTag(String tag);
    default boolean existsTag(String tag){
        return findByTag(tag).isPresent();
    }
    @Query(value = "select * from tags where tag like :prefix% limit 3",nativeQuery = true)
    List<TagEntity> findOptionByPrefixTag(String prefix);
}
