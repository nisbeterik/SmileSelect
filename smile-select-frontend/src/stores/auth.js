import { defineStore } from 'pinia';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('jwtToken') || '',
    role: localStorage.getItem('userRole') || '',
    id: localStorage.getItem('userId') || '',
    firstName: localStorage.getItem('firstName') || '',
    lastName: localStorage.getItem('lastName') || '',
    clinicId: localStorage.getItem('clinicId') || '',
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
    setFirstName(firstName) {
      this.firstName = firstName;
      localStorage.setItem('firstName', firstName);
    },
    setLastName(lastName) {
      this.lastName = lastName;
      localStorage.setItem('lastName', lastName);
    },
    setClinicId(clinicId) {
      this.clinicId = clinicId;
      localStorage.setItem('clinicId', clinicId);
    },

    clearAuth() {
      this.token = '';
      this.role = '';
      this.id = '';
      this.firstName = '';
      this.lastName = '';
      this.clinicId = '';
      localStorage.removeItem('jwtToken');
      localStorage.removeItem('userRole');
      localStorage.removeItem('userId');
      localStorage.removeItem('firstName');
      localStorage.removeItem('lastName')
      localStorage.removeItem('clinicId')
    },
  },
  getters: {
    isAuthenticated: (state) => !!state.token,
  },
});
