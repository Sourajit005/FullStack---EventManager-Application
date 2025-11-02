import axios from 'axios';

// This is your Spring Boot backend's public auth URL
const AUTH_API_URL = 'https://event-manager-backend-icw9.onrender.com/auth';

const authService = {
  /**
   * Logs a user in
   * @param {object} loginData - { username, password }
   */
  login: (loginData) => {
    return axios.post(`${AUTH_API_URL}/login`, loginData);
  },

  /**
   * Registers a new user
   * @param {object} registerData - { username, email, password, role }
   */
  register: (registerData) => {
    return axios.post(`${AUTH_API_URL}/register`, registerData);
  },
};

export default authService;