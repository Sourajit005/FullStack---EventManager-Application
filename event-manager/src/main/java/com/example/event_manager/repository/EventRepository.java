package com.example.event_manager.repository;

import com.example.event_manager.domain.Event;
import com.example.event_manager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // Custom method to find all events for a specific organizer
    List<Event> findByOrganizer(User organizer);

    // You could also find by organizer ID
    List<Event> findByOrganizerId(Long organizerId);
}