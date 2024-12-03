import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('jwtToken') || '',
  }),
  actions: {
    setToken(token) {
      this.token = token;
      localStorage.setItem('jwtToken', token);
    },
    clearToken() {
      this.token = '';
      localStorage.removeItem('jwtToken');
    },
  },
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
});
