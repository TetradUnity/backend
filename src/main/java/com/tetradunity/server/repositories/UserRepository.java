package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.models.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmail(String email);
	@Query(value = "select * from users where email like :emailPrefix% and role = '%role' limit 3", nativeQuery = true)
	List<UserEntity> findByEmailPrefixAndRole(String emailPrefix, Role role);
}
