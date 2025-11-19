import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  //baseURL: 'https://event-manager-backend-icw9.onrender.com/api',
});

// Use an interceptor to add the auth token
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

const registrationService = {
  /**
   * Gets all registrations for the current logged-in user.
   */
  getMyRegistrations: () => {
    return api.get('/my-registrations');
  },
};

export default registrationService;