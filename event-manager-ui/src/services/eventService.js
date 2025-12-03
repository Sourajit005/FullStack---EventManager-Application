import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

const eventService = {
  getAllEvents: () => {
    return api.get('/events'); 
  },

  getEventById: (id) => {
    return api.get(`/events/${id}`);
  },

  createEvent: (eventData) => {
    return api.post('/events', eventData);
  },

  updateEvent: (id, eventData) => {
    return api.put(`/events/${id}`, eventData);
  },
  
  registerForEvent: (id) => {
    return api.post(`/events/${id}/register`);
  },

  getMyEvents: () => {
    return api.get('/organizer/my-events');
  },
  getEventAttendees: (id) => {
    return api.get(`/organizer/my-events/${id}/attendees`);
  },
};

export default eventService;