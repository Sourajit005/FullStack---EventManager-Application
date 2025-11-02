package com.example.event_manager.config;

import com.example.event_manager.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // For method-level security (like @PreAuthorize)
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    // 1. Password Encoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. Authentication Provider Bean (tells Spring how to auth)
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // 3. Authentication Manager Bean (needed for login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 4. The Main Security Filter Chain (The Rules)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Disable CSRF (Cross-Site Request Forgery) since we are stateless
                .csrf(AbstractHttpConfigurer::disable)


                // Define endpoint authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public Endpoints
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/events", "/api/events/{id}").permitAll()

                        // User-Specific Endpoints
                        .requestMatchers("/api/events/{id}/register").hasAnyRole("USER", "ORGANIZER")
                        // --- ADD THIS NEW RULE ---
                        .requestMatchers("/api/my-registrations").authenticated() // Any logged-in user
                        // --- END OF NEW RULE ---
                        // Organizer-Specific Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/events").hasRole("ORGANIZER")
                        .requestMatchers(HttpMethod.PUT, "/api/events/{id}").hasRole("ORGANIZER")
                        .requestMatchers("/api/organizer/**").hasRole("ORGANIZER")

                        // All other requests must be authenticated
                        .anyRequest().authenticated()
                )


                // Set session management to STATELESS
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Tell Spring to use our custom AuthProvider
                .authenticationProvider(authenticationProvider())

                // Add our custom JWT filter *before* the standard auth filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // --- ADD THIS NEW BEAN ---
    /**
     * Configures CORS (Cross-Origin Resource Sharing) for the application.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. Allow your React app's origin
        // (Vite's default is 5173, Create React App is 3000)
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));

        // 2. Allow all common HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 3. Allow all headers
        configuration.setAllowedHeaders(List.of("*")); // Or be more specific

        // 4. Allow credentials (like cookies, if you use them)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this config to all paths
        return source;
    }
}