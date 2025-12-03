import axios from 'axios';

const AUTH_API_URL = 'http://localhost:8080/auth';

const authService = {
  login: (loginData) => {
    return axios.post(`${AUTH_API_URL}/login`, loginData);
  },

  register: (registerData) => {
    return axios.post(`${AUTH_API_URL}/register`, registerData);
  },
};

export default authService;