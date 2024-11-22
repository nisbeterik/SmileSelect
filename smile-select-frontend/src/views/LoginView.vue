<template>
  <div class="login-container">
    <h2>Login</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label for="role">Role</label>
        <select id="role" v-model="role" required>
          <option value="patient">Patient</option>
          <option value="dentist">Dentist</option>
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
import axios from '@/axios'; // Import the configured Axios instance

export default {
  name: 'LoginView',
  data() {
    return {
      email: '',
      password: '',
      role: 'patient', // Default to patient role
      errorMessage: '',
    };
  },
  methods: {
    async handleLogin() {
      try {
        const endpoint = `/accounts/login/${this.role}`;
        const response = await axios.post(endpoint, {
          email: this.email,
          password: this.password,
        });
        console.log('Login Successful:', response.data);

        const token = response.data.token;

        if (token) {
          // Store the token in localStorage
          localStorage.setItem('jwtToken', token);
        } else {
          throw new Error('Token not found in response');
        }

        // Emit success event to parent component with role
        this.$emit('loginSuccess', this.role);
      } catch (error) {
        this.errorMessage = 'Login failed. Please try again.';
        console.error('Login Error:', error);
      }
    },
  },
};
</script>

<style scoped>
</style>
