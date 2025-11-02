package com.example.event_manager.service;

import com.example.event_manager.domain.Event;
import com.example.event_manager.domain.User; // Added import
import com.example.event_manager.dto.CreateEventRequest; // Added import
import com.example.event_manager.domain.Registration;
import com.example.event_manager.dto.EventDTO;
import com.example.event_manager.dto.AttendeeDTO;
import com.example.event_manager.dto.OrganizerDTO;
import org.springframework.security.access.AccessDeniedException;
import com.example.event_manager.repository.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Gets all events and converts them to DTOs.
     */
    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::convertToDto) // Convert each event
                .collect(Collectors.toList());
    }

    /**
     * Gets the list of attendees for a specific event.
     * Includes a critical ownership check.
     */
    @Transactional(readOnly = true)
    public List<AttendeeDTO> getEventAttendees(Long eventId, User loggedInOrganizer) {
        // 1. Find the event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        // 2. CRITICAL: Check if the logged-in user is the organizer
        if (!event.getOrganizer().getId().equals(loggedInOrganizer.getId())) {
            // Throw a 403 Forbidden error
            throw new AccessDeniedException("You are not the organizer of this event.");
        }

        // 3. If check passes, map registrations to AttendeeDTOs
        return event.getRegistrations().stream()
                .map(this::convertRegistrationToAttendeeDto)
                .collect(Collectors.toList());
    }

    /**
     * A private helper method to convert a Registration entity to an AttendeeDTO.
     */
    private AttendeeDTO convertRegistrationToAttendeeDto(Registration registration) {
        return AttendeeDTO.builder()
                .registrationId(registration.getId())
                .username(registration.getUser().getUsername())
                .email(registration.getUser().getEmail())
                .registrationDate(registration.getRegistrationDate())
                .build();
    }

    /**
     * Gets a single event by its ID and converts it to a DTO.
     */
    @Transactional(readOnly = true)
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id)); // Add a proper exception later
        return convertToDto(event);
    }

    /**
     * Creates a new event and links it to the logged-in organizer.
     */
    @Transactional
    public EventDTO createEvent(CreateEventRequest request, User organizer) {
        // 1. Create the new Event entity
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .availableSpots(request.getAvailableSpots())
                .organizer(organizer) // 2. Link to the organizer
                .build();

        // 3. Save it to the database
        Event savedEvent = eventRepository.save(event);

        // 4. Convert to DTO and return
        return convertToDto(savedEvent);
    }
    /**
     * Updates an existing event after verifying ownership.
     *
     * @param eventId           The ID of the event to update.
     * @param request           The DTO with the new event data.
     * @param loggedInOrganizer The currently authenticated user.
     * @return The updated EventDTO.
     */
    @Transactional
    public EventDTO updateEvent(Long eventId, CreateEventRequest request, User loggedInOrganizer) {
        // 1. Find the event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId)); // Use a custom exception here

        // 2. CRITICAL: Check if the logged-in user is the organizer
        if (!event.getOrganizer().getId().equals(loggedInOrganizer.getId())) {
            // In a real app, you'd throw a specific AccessDeniedException
            throw new RuntimeException("You are not authorized to update this event");
        }

        // 3. Update the event's fields
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setAvailableSpots(request.getAvailableSpots());

        // 4. Save the changes to the database
        Event updatedEvent = eventRepository.save(event);

        // 5. Convert to DTO and return
        return convertToDto(updatedEvent);
    }
    /**
     * Gets all events created by a specific organizer.
     *
     * @param organizer The authenticated User object.
     * @return A list of that organizer's events.
     */
    @Transactional(readOnly = true)
    public List<EventDTO> getEventsForOrganizer(User organizer) {
        // We already created the findByOrganizer method in our EventRepository!
        return eventRepository.findByOrganizer(organizer)
                .stream()
                .map(this::convertToDto) // Reuse our existing helper
                .collect(Collectors.toList());
    }

    /**
     * A helper method to convert an Event entity to an EventDTO.
     */
    private EventDTO convertToDto(Event event) {
        // Create the safe organizer DTO
        OrganizerDTO organizerDto = OrganizerDTO.builder()
                .id(event.getOrganizer().getId())
                .username(event.getOrganizer().getUsername())
                .build();

        // Create the Event DTO
        return EventDTO.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .availableSpots(event.getAvailableSpots())
                .organizer(organizerDto)
                .build();
    }
}