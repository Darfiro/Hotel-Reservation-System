package com.rsoi.repository;


import com.rsoi.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CrudRepository<User, Integer>
{
    Optional<User> findByLogin(String username);
    Optional<User> findByUserUid(UUID userUid);
}

