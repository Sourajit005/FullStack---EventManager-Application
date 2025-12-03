package com.example.event_manager.domain;

import jakarta.persistence.*;
import com.example.event_manager.domain.Registration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP) 
    private LocalDateTime eventDate;

    @Column(nullable = false)
    private int availableSpots;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "organizer_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User organizer;

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Registration> registrations = new HashSet<>();

}