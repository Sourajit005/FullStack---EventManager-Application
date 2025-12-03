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

    @Transactional
    public void registerForEvent(Long eventId, User user) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (registrationRepository.existsByUserIdAndEventId(user.getId(), eventId)) {
            throw new RuntimeException("You are already registered for this event.");
        }

        if (event.getAvailableSpots() <= 0) {
            throw new RuntimeException("This event is full. No available spots.");
        }

        event.setAvailableSpots(event.getAvailableSpots() - 1);
        eventRepository.save(event);

        Registration registration = Registration.builder()
                .user(user)
                .event(event)
                .registrationDate(LocalDateTime.now())
                .build();
        Registration savedRegistration = registrationRepository.save(registration);

        String qrText = "RegID:" + savedRegistration.getId() +
                ";EventID:" + event.getId() +
                ";User:" + user.getUsername();

        savedRegistration.setQrCodeData(qrText);
        registrationRepository.save(savedRegistration);

        byte[] qrCodeBytes = qrCodeService.generateQRCode(qrText, 250, 250);

        emailService.sendRegistrationConfirmation(user.getEmail(), event.getTitle(), qrCodeBytes);
    }
}