import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('jwtToken') || '',
    role: localStorage.getItem('userRole') || '',
    id: localStorage.getItem('userId') || '',
  }),
  actions: {
    setToken(token) {
      this.token = token;
      localStorage.setItem('jwtToken', token);
    },
    setRole(role) {
      this.role = role;
      localStorage.setItem('userRole', role);
    },
    setId(id) {
      this.id = id;
      localStorage.setItem('userId', id);
    },
    clearAuth() {
      this.token = '';
      this.role = '';
      this.id = '';
      localStorage.removeItem('jwtToken');
      localStorage.removeItem('userRole');
      localStorage.removeItem('userId');
    },
  },
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
});
