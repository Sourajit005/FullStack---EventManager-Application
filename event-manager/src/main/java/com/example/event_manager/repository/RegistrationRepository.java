package com.example.event_manager.repository;

import com.example.event_manager.domain.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.event_manager.domain.User;

import java.util.List;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // Custom method to check if a registration already exists for this user and event
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
    List<Registration> findByUser(User user);
}