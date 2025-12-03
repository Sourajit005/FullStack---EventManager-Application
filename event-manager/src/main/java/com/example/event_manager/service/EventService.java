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

    @Transactional(readOnly = true)
    public List<EventDTO> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .map(this::convertToDto) 
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AttendeeDTO> getEventAttendees(Long eventId, User loggedInOrganizer) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId));

        if (!event.getOrganizer().getId().equals(loggedInOrganizer.getId())) {
            throw new AccessDeniedException("You are not the organizer of this event.");
        }

        return event.getRegistrations().stream()
                .map(this::convertRegistrationToAttendeeDto)
                .collect(Collectors.toList());
    }

    private AttendeeDTO convertRegistrationToAttendeeDto(Registration registration) {
        return AttendeeDTO.builder()
                .registrationId(registration.getId())
                .username(registration.getUser().getUsername())
                .email(registration.getUser().getEmail())
                .registrationDate(registration.getRegistrationDate())
                .build();
    }

    @Transactional(readOnly = true)
    public EventDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id)); // Add a proper exception later
        return convertToDto(event);
    }

    @Transactional
    public EventDTO createEvent(CreateEventRequest request, User organizer) {
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .availableSpots(request.getAvailableSpots())
                .organizer(organizer)
                .build();

        Event savedEvent = eventRepository.save(event);

        return convertToDto(savedEvent);
    }

    @Transactional
    public EventDTO updateEvent(Long eventId, CreateEventRequest request, User loggedInOrganizer) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + eventId)); 

        if (!event.getOrganizer().getId().equals(loggedInOrganizer.getId())) {
            throw new RuntimeException("You are not authorized to update this event");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setAvailableSpots(request.getAvailableSpots());

        Event updatedEvent = eventRepository.save(event);

        return convertToDto(updatedEvent);
    }

    @Transactional(readOnly = true)
    public List<EventDTO> getEventsForOrganizer(User organizer) {
        return eventRepository.findByOrganizer(organizer)
                .stream()
                .map(this::convertToDto) 
                .collect(Collectors.toList());
    }

    private EventDTO convertToDto(Event event) {
        OrganizerDTO organizerDto = OrganizerDTO.builder()
                .id(event.getOrganizer().getId())
                .username(event.getOrganizer().getUsername())
                .build();

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