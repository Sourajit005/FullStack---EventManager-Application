import axios from 'axios';

// Create an 'api' instance of axios
// All requests will go to this base URL
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  //baseURL: 'https://event-manager-backend-icw9.onrender.com/api', // Your Spring Boot backend URL
});

// --- THIS IS THE NEW PART ---
// Use an "interceptor" to add the token to every request
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      // If the token exists, add it to the Authorization header
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);
// --- END OF NEW PART ---

// Create our service object
const eventService = {
  // Method to get all events
  getAllEvents: () => {
    return api.get('/events'); // This calls http://localhost:8080/api/events
  },

  // Method to get a single event by ID (we'll use this later)
  getEventById: (id) => {
    return api.get(`/events/${id}`);
  },

  // We will add createEvent, updateEvent, etc. here later
  // --- THESE ARE THE SECURED METHODS ---
  // Only organizers can do this
  createEvent: (eventData) => {
    return api.post('/events', eventData);
  },

  // Only the event owner can do this
  updateEvent: (id, eventData) => {
    return api.put(`/events/${id}`, eventData);
  },
  
  // Any logged-in user can do this
  registerForEvent: (id) => {
    return api.post(`/events/${id}/register`);
  },

  // Only organizers can do this
  getMyEvents: () => {
    return api.get('/organizer/my-events');
  },
  /**
   * Gets the list of attendees for one of the organizer's events.
   * This is a secured endpoint.
   * @param {string} id - The event ID.
   */
  getEventAttendees: (id) => {
    return api.get(`/organizer/my-events/${id}/attendees`);
  },
};

export default eventService;