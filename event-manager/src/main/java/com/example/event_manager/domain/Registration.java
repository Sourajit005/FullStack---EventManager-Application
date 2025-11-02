package com.example.event_manager.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode; // <-- ADD THIS IMPORT
import lombok.ToString; // <-- ADD THIS IMPORT

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registrations",
        uniqueConstraints = {
                // A user can only register for a specific event once
                @UniqueConstraint(columnNames = {"user_id", "event_id"})
        })
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corresponds to registration_id

    @Column(nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "qr_code_data") // For the bonus feature
    private String qrCodeData;

    // --- Relationships ---

    /**
     * Many Registrations can be made by one User.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude // <-- ADD THIS
    @EqualsAndHashCode.Exclude // <-- ADD THIS
    private User user;

    /**
     * Many Registrations can be for one Event.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    @ToString.Exclude // <-- ADD THIS
    @EqualsAndHashCode.Exclude // <-- ADD THIS
    private Event event;

}