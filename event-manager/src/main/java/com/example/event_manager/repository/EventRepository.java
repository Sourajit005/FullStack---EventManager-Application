package com.example.event_manager.repository;

import com.example.event_manager.domain.Event;
import com.example.event_manager.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByOrganizer(User organizer);

    List<Event> findByOrganizerId(Long organizerId);
}