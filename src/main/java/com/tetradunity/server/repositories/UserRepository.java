package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmail(String email);
	@Query("select * from users where email like :emailPrefix% and role = '%role' limit 3", nativeQuery = true)
	List<UserEntity> findByEmailPrefixAndRole(String emailPrefix, Role role);
}
