package com.example.event_manager.domain;

import jakarta.persistence.*;
import com.example.event_manager.domain.Registration;
import com.example.event_manager.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.EqualsAndHashCode; // <-- ADD THIS IMPORT
import lombok.ToString; // <-- ADD THIS IMPORT

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.ArrayList; // <-- ADD THIS IMPORT
import java.util.HashSet; // <-- ADD THIS IMPORT

@Data // Lombok: Generates getters, setters, toString, equals, and hashCode
@Builder // Lombok: Implements the builder pattern
@NoArgsConstructor // Lombok: Generates a no-argument constructor
@AllArgsConstructor // Lombok: Generates an all-argument constructor
@Entity // Specifies that this class is a JPA entity
@Table(name = "users") // Maps this class to the "users" table in the database
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corresponds to user_id

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password; // We'll store the hashed password

    @Enumerated(EnumType.STRING) // Stores the enum as a String (e.g., "ROLE_USER")
    @Column(nullable = false)
    private Role role;

    // --- Relationships ---

    // One User (as an Organizer) can create many Events
    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // <-- ADD THIS
    @EqualsAndHashCode.Exclude // <-- ADD THIS
    private List<Event> organizedEvents = new ArrayList<>();

    // One User (as an Attendee) can have many Registrations
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // <-- ADD THIS
    @EqualsAndHashCode.Exclude // <-- ADD THIS
    private Set<Registration> registrations = new HashSet<>();


    // --- Spring Security (UserDetails interface methods) ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Returns the user's role as a security-usable authority
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password; // Returns the hashed password
    }

    @Override
    public String getUsername() {
        return username; // Spring Security will use this for authentication
    }

    // You can set these to true for simplicity, or add logic later
    // (e.g., for email verification or account banning)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}