package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.UserEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmail(String email);
	@Query(value = "SELECT * FROM users WHERE email LIKE :emailPrefix% AND role = :role LIMIT 3", nativeQuery = true)
	List<UserEntity> findByEmailPrefixAndRole(@Param("emailPrefix") String emailPrefix, @Param("role") String role);
	@Query(value = "SELECT COUNT(*) FROM users WHERE role = 'CHIEF_TEACHER'", nativeQuery = true)
	Long countChiefTeachers();

	default boolean existsChiefTeacher() {
		Long count = countChiefTeachers();
		return count > 0;
	}
}
