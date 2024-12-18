<template>
  <div class="login-container">
    <h2>{{ headerText }}</h2>
    <form @submit.prevent="handleLogin">
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

      <button type="submit" class="button-primary">Login</button>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';
import '/src/CSS/global.css';

export default {
  name: 'LoginComponent',
  data() {
    return {
      email: '',
      password: '',
      role: '',
      errorMessage: '',
    };
  },
  created() {
    this.role = this.$route.query.role || 'PATIENT';
  },
  computed: {
    headerText() {
      return this.role === 'DENTIST' ? 'Dentist Login' : 'Patient Login';
    },
  },
  methods: {
    async handleLogin() {
      const authStore = useAuthStore();
      try {
        authStore.clearAuth();
        
        const endpoint = `/auth/login`;
        const response = await this.$axios.post(endpoint, {
          email: this.email,
          password: this.password,
          role: this.role,
        });

        const { token, role, id, firstName, lastName } = response.data;

        if (token) {
          authStore.setFirstName(firstName)
          authStore.setLastName(lastName)
          authStore.setToken(token);
          authStore.setRole(role);
          authStore.setId(id);
          // Save the ID
          this.$emit('loginSuccess', this.role);
        } else {
          throw new Error('Token not found');
        }
      } catch (error) {
        console.error('Login Error:', error.response?.data || error.message);
        this.errorMessage =
          error.response?.data?.message || 'Login failed. Please try again.';
      }
    },
  },
};
</script>

<style scoped>
.login-container {
  max-width: 500px;
  margin: 0;
  padding: 20px;
  border-radius: 8px;
  color: #003020;
}
.error {
  color: red;
  margin-top: 10px;
}
</style>
