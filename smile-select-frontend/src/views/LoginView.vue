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
import axios from '@/axios';

export default {
  name: 'LoginView',
  data() {
    return {
      email: '',
      password: '',
      role: 'patient', // Default to patient
      errorMessage: '',
    };
  },
  methods: {
    async handleLogin() {
      // Validate email and password on the client side
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!this.email) {
        this.emailError = 'Email is required';
        return;
      } else if (!emailPattern.test(this.email)) {
        this.emailError = 'Please enter a valid email';
        return;
      }

      if (!this.password) {
        this.passwordError = 'Password is required';
        return;
      }

      try {
        const endpoint = `/api/login/${this.role}`; // Dynamically choose endpoint based on role
        const response = await axios.post(endpoint, {
          email: this.email,
          password: this.password,
        });

        console.log(`${this.role} Login Successful`, response.data);

        // Redirect to the dashboard or specific page based on role
        if (this.role === 'patient') {
          this.$router.push('/patient-dashboard');
        } else {
          this.$router.push('/dentist-dashboard');
        }
      } catch (error) {
        this.errorMessage = error.response?.data?.message || 'Login failed';
        console.error(`Error during ${this.role} login:`, error);
      }
    },
  },
};
</script>
