package com.rsoi.loyalty.repository;

import com.rsoi.loyalty.model.UserLoyalty;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLoyaltyRepository extends CrudRepository<UserLoyalty, Integer>
{
    Optional<UserLoyalty> findByUserUid(UUID userUid);
}
