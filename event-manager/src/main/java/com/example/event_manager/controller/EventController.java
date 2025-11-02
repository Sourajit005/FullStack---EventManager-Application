package com.example.event_manager.controller;

import com.example.event_manager.domain.User;
import com.example.event_manager.dto.CreateEventRequest;
import com.example.event_manager.dto.EventDTO;
import com.example.event_manager.service.EventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import com.example.event_manager.dto.MessageResponse;
import com.example.event_manager.service.RegistrationService;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/events") // Base path for all event-related APIs
public class EventController {

    private final EventService eventService;
    private final RegistrationService registrationService;

    // UPDATE THE CONSTRUCTOR
    public EventController(EventService eventService, RegistrationService registrationService) {
        this.eventService = eventService;
        this.registrationService = registrationService;
    }

    /**
     * GET /api/events
     * Gets a list of all available events.
     * This endpoint is public (as defined in SecurityConfig).
     */
    @GetMapping
    public ResponseEntity<List<EventDTO>> getAllEvents() {
        List<EventDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    /**
     * GET /api/events/{id}
     * Gets the details for a single event.
     * This endpoint is also public.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEventById(@PathVariable Long id) {
        EventDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    /**
     * POST /api/events
     * Creates a new event.
     * This endpoint is secured by SecurityConfig to only allow ROLE_ORGANIZER.
     *
     * @param request        The event data from the request body.
     * @param authentication The security context, injected by Spring Security.
     * @return The newly created event DTO.
     */
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            Authentication authentication
    ) {
        // Get the logged-in user from the security principal
        User loggedInOrganizer = (User) authentication.getPrincipal();

        // Call the service to create the event
        EventDTO createdEvent = eventService.createEvent(request, loggedInOrganizer);

        // Return a 201 Created status
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }
    @PostMapping("/{id}/register")
    public ResponseEntity<MessageResponse> registerForEvent(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User loggedInUser = (User) authentication.getPrincipal();

        // The service handles all the complex logic
        registrationService.registerForEvent(id, loggedInUser);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new MessageResponse("Registration successful! Check your email for confirmation."));
    }
    /**
     * PUT /api/events/{id}
     * Updates an existing event.
     * Secured by SecurityConfig to only allow ROLE_ORGANIZER.
     * The service layer will verify that the organizer owns the event.
     *
     * @param id             The event ID from the URL path.
     * @param request        The event data from the request body.
     * @param authentication The security context.
     * @return The updated event DTO.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody CreateEventRequest request,
            Authentication authentication
    ) {
        // Get the logged-in user
        User loggedInOrganizer = (User) authentication.getPrincipal();

        // Call the service to update
        EventDTO updatedEvent = eventService.updateEvent(id, request, loggedInOrganizer);

        // Return 200 OK with the updated event
        return ResponseEntity.ok(updatedEvent);
    }
}