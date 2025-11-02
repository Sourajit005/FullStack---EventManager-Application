package com.example.event_manager.controller;

import com.example.event_manager.domain.User;
import com.example.event_manager.dto.EventDTO;
import com.example.event_manager.dto.AttendeeDTO;
import com.example.event_manager.service.EventService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/organizer")
public class OrganizerController {

    private final EventService eventService;
    public OrganizerController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * GET /api/organizer/my-events
     * Gets all events created by the currently logged-in organizer.
     * Secured by SecurityConfig to only allow ROLE_ORGANIZER.
     */
    @GetMapping("/my-events")
    public ResponseEntity<List<EventDTO>> getMyEvents(Authentication authentication) {
        // 1. Get the logged-in user
        User loggedInOrganizer = (User) authentication.getPrincipal();

        // 2. Call the service to find their events
        List<EventDTO> events = eventService.getEventsForOrganizer(loggedInOrganizer);

        // 3. Return the list
        return ResponseEntity.ok(events);
    }
    /**
     * GET /api/organizer/my-events/{id}/attendees
     * Gets the list of registered attendees for one of the organizer's events.
     * Secured by SecurityConfig (ROLE_ORGANIZER) and service layer (ownership).
     */
    @GetMapping("/my-events/{id}/attendees")
    public ResponseEntity<List<AttendeeDTO>> getEventAttendees(
            @PathVariable Long id,
            Authentication authentication) {

        // 1. Get the logged-in user
        User loggedInOrganizer = (User) authentication.getPrincipal();

        // 2. Call the service (which includes the ownership check)
        List<AttendeeDTO> attendees = eventService.getEventAttendees(id, loggedInOrganizer);

        // 3. Return the list
        return ResponseEntity.ok(attendees);
    }
}