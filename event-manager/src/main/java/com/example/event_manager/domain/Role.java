package com.example.event_manager.domain;

/**
 * Defines the roles a user can have within the application.
 */
public enum Role {
    ROLE_USER,      // Standard user (can browse and register for events)
    ROLE_ORGANIZER  // Can do everything ROLE_USER can, plus create/manage their own events
}