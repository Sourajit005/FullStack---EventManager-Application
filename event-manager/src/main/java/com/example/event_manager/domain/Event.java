package com.example.event_manager.domain;

import jakarta.persistence.*;
import com.example.event_manager.domain.Registration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode; // <-- ADD THIS IMPORT
import lombok.ToString; // <-- ADD THIS IMPORT

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corresponds to event_id

    @Column(nullable = false)
    private String title;

    @Lob // Specifies a large object, good for long descriptions
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP) // Stores both date and time
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private int availableSpots;

    // --- Relationships ---

    /**
     * Many Events can be created by one User (Organizer).
     * This is the "owning" side of the relationship.
     */
    @ManyToOne(fetch = FetchType.EAGER) // Lazy fetch for performance
    @JoinColumn(name = "organizer_id", nullable = false)
    @ToString.Exclude // <-- ADD THIS
    @EqualsAndHashCode.Exclude // <-- ADD THIS
    private User organizer;

    /**
     * One Event can have many Registrations.
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude // <-- ADD THIS
    @EqualsAndHashCode.Exclude // <-- ADD THIS
    private Set<Registration> registrations = new HashSet<>(); // <-- THIS IS THE FIX

}