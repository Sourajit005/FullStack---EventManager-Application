# Full-Stack Event Manager 

A complete full-stack event management application built with a Java Spring Boot backend and a React frontend. Users can browse events, and organizers can log in to a dedicated dashboard to create, manage, and track their events.

## Features
* **User Authentication:** Secure user registration and login (USER & ORGANIZER roles) using Spring Security and JWT.
* **Event Browsing:** Public-facing pages for all users to browse and view event details.
* **Event Registration:** Logged-in users can register for events.
* **Ticketing & Email:** On successful registration, the user receives a confirmation email (via Spring Mail) with a unique QR code (via `zxing`).
* **Organizer Dashboard:** A protected route for `ROLE_ORGANIZER` users to create new events and view their existing events.
* **Attendee Tracking:** Organizers can view a detailed list of all registered attendees (with ticket numbers) for each of their events.

## Tech Stack
* **Backend:** Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA
* **Frontend:** React, React Router, Axios, `qrcode.react`
* **Database:** PostgreSQL

---

## 🚀 How to Run

### 1. Backend (event-manager)
1.  Open the `/event-manager` folder in your preferred Java IDE (like IntelliJ IDEA).
2.  Open `src/main/resources/application.properties`.
3.  Update the `spring.datasource.url`, `username`, and `password` to match your local PostgreSQL setup.
4.  Update the `spring.mail` properties with your own email credentials (a Gmail "App Password" is recommended).
5.  Run the `EventManagerApplication.java` file.
6.  The backend will be running on `http://localhost:8080`.

### 2. Frontend (event-manager-ui)
1.  Open the `/event-manager-ui` folder in VS Code.
2.  Open a terminal and run `npm install` to install all dependencies.
3.  After installation, run `npm run dev`.
4.  The app will be running on `http://localhost:5173`.
