// src/axios.js
import axios from 'axios';
import { useAuthStore } from './stores/auth';

const instance = axios.create({
  baseURL: 'http://localhost:8080/api', 
});

instance.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore();
    const token = authStore.token;
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export default instance;
