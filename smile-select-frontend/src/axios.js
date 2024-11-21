// axios.js
import axios from 'axios';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// Request interceptor to include the JWT token
instance.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('jwtToken');
      if (token) {
        // If token exists, include it in the Authorization header
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
);

// TODO - Add Logout Feature that removes token after logout

export default instance;
