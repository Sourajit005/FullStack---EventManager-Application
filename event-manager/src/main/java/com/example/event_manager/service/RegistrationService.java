package com.example.event_manager.service;

import com.example.event_manager.domain.Event;
import com.example.event_manager.domain.Registration;
import com.example.event_manager.domain.User;
import com.example.event_manager.repository.EventRepository;
import com.example.event_manager.repository.RegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.event_manager.dto.RegistrationDTO;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final QRCodeService qrCodeService;
    private final EmailService emailService;

    public RegistrationService(RegistrationRepository registrationRepository,
                               EventRepository eventRepository,
                               QRCodeService qrCodeService,
                               EmailService emailService) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.qrCodeService = qrCodeService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<RegistrationDTO> getMyRegistrations(User user) {
        List<Registration> registrations = registrationRepository.findByUser(user);
        return registrations.stream()
                .map(this::convertRegistrationToDto)
                .collect(Collectors.toList());
    }

    private RegistrationDTO convertRegistrationToDto(Registration reg) {
        return RegistrationDTO.builder()
                .registrationId(reg.getId())
                .qrCodeData(reg.getQrCodeData())
                .registrationDate(reg.getRegistrationDate())
                .eventTitle(reg.getEvent().getTitle())
                .eventLocation(reg.getEvent().getLocation())
                .eventDate(reg.getEvent().getEventDate())
                .build();
    }

    /**
     * Registers the logged-in user for a specific event.
     * This entire method is one transaction.
     */
    @Transactional
    public void registerForEvent(Long eventId, User user) {
        // 1. Find the event
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 2. Check if already registered
        if (registrationRepository.existsByUserIdAndEventId(user.getId(), eventId)) {
            throw new RuntimeException("You are already registered for this event.");
        }

        // 3. Check for available spots
        if (event.getAvailableSpots() <= 0) {
            throw new RuntimeException("This event is full. No available spots.");
        }

        // 4. Decrement spots and save the event
        event.setAvailableSpots(event.getAvailableSpots() - 1);
        eventRepository.save(event);

        // 5. Create and save the new registration (FIRST save, without QR data)
        Registration registration = Registration.builder()
                .user(user)
                .event(event)
                .registrationDate(LocalDateTime.now())
                .build();
        Registration savedRegistration = registrationRepository.save(registration);

        // 6. Generate QR Code text using the ID from the saved registration
        String qrText = "RegID:" + savedRegistration.getId() +
                ";EventID:" + event.getId() +
                ";User:" + user.getUsername();

        // 7. Set the QR data on the object and save AGAIN (this updates the row)
        savedRegistration.setQrCodeData(qrText);
        registrationRepository.save(savedRegistration);

        // 8. Generate the QR code image for the email
        byte[] qrCodeBytes = qrCodeService.generateQRCode(qrText, 250, 250);

        // 9. Send the confirmation email
        emailService.sendRegistrationConfirmation(user.getEmail(), event.getTitle(), qrCodeBytes);
    }
}