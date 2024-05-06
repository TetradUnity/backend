package com.example.server.repositories;

import com.example.server.entities.StudentSubjectEntity;
import org.springframework.data.repository.CrudRepository;

public interface StudentSubjectRepository extends CrudRepository<StudentSubjectEntity, Long> {

}
