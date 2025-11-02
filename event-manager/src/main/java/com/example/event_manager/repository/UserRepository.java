package com.example.event_manager.repository;

import com.example.event_manager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Security will use this method to find a user by their username
    Optional<User> findByUsername(String username);

    // We'll also add these for registration validation
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}