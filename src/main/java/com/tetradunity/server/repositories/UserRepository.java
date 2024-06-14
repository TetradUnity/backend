package com.tetradunity.server.repositories;

import com.tetradunity.server.entities.UserEntity;
import com.tetradunity.server.projections.StartSubjectRemind;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmail(String email);
	@Query(value = "SELECT * FROM users WHERE email LIKE :emailPrefix% AND role = :role LIMIT :limit OFFSET :pos", nativeQuery = true)
	List<UserEntity> findByEmailPrefixAndRole(String emailPrefix, String role, int limit, int pos);

	@Query(value = """
			SELECT * FROM users
			WHERE (:first_namePrefix = '' OR first_name LIKE :first_namePrefix)
			AND (:last_namePrefix = '' OR last_name LIKE :last_namePrefix)
			AND role = :role
			ORDER BY last_name, first_name
			LIMIT :limit OFFSET :pos""", nativeQuery = true)
	List<UserEntity> findByNameAndRole(String first_namePrefix, String last_namePrefix, String role, int limit, int pos);
	@Query(value = "SELECT COUNT(*) FROM users WHERE role = 'CHIEF_TEACHER'", nativeQuery = true)
	Long countChiefTeachers();

	@Query(value = """
			SELECT COUNT(*) FROM users
			WHERE (:first_namePrefix = '' OR first_name LIKE :first_namePrefix)
			AND (:last_namePrefix = '' OR last_name LIKE :last_namePrefix)
			AND role = :role
			ORDER BY last_name, first_name
			""", nativeQuery = true)
	Long countByNameAndRole(String first_namePrefix, String last_namePrefix, String role);

	default boolean existsChiefTeacher() {
		Long count = countChiefTeachers();
		return count > 0;
	}

	@Query(value = """
				SELECT u.email, u.first_name, s.title FROM users u
				JOIN student_subjects st ON st.student_id = u.id
				JOIN subjects s ON s.id = st.subject_id
				WHERE (s.time_start BETWEEN UNIX_TIMESTAMP() * 1000 - 900000 AND UNIX_TIMESTAMP() * 1000)
				AND s.is_start
			""", nativeQuery = true)
	List<StartSubjectRemind> findUserRemind();

	@Query(value = """
			SELECT u.* FROM users u
			JOIN student_subjects st ON u.id = st.student_id
			WHERE st.subject_id = :subject_id
			""", nativeQuery = true)
	List<UserEntity> findBySubjectId(long subject_id);
}
