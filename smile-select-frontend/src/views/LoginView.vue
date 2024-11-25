<template>
  <div class="login-container">
    <h2>Login</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="role">Role</label>
        <select id="role" v-model="role" required>
          <option value="PATIENT">Patient</option>
          <option value="DENTIST">Dentist</option>
        </select>
      </div>

      <div class="form-group">
        <label for="email">Email</label>
        <input
            type="email"
            id="email"
            v-model="email"
            placeholder="Enter your email"
            required
        />
      </div>

      <div class="form-group">
        <label for="password">Password</label>
        <input
            type="password"
            id="password"
            v-model="password"
            placeholder="Enter your password"
            required
        />
      </div>

      <button type="submit">Login</button>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script>
import axios from '@/axios';

export default {
  name: 'LoginView',
  data() {
    return {
      email: '',
      password: '',
      role: 'PATIENT',
      errorMessage: '',
    };
  },
  methods: {
    async handleLogin() {
      try {
        const endpoint = `/auth/login`;
        const response = await axios.post(endpoint, {
          email: this.email,
          password: this.password,
          role: this.role,
        });

        const token = response.data.token;

        if (token) {
          localStorage.setItem('jwtToken', token);
          this.$emit('loginSuccess', this.role);
        } else {
          throw new Error('Token not found');
        }
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Login failed. Please try again.';
      }
    },
  },
};
</script>
