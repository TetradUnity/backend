package com.example.server.repositories;

import com.example.server.entities.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserEntityRepository extends CrudRepository<UserEntity, Long> { }
