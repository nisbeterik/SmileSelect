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

      <button type="submit">Login</button>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </form>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';

export default {
  name: 'LoginView',
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

        const { token, role, id } = response.data;

        if (token) {
          authStore.setToken(token);
          authStore.setRole(role);
          authStore.setId(id); // Save the ID
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
}
.form-group {
  margin-bottom: 15px;
}
.form-group label {
  display: block;
  margin-bottom: 5px;
}
.form-group input,
.form-group select {
  width: 100%;
  padding: 8px;
  box-sizing: border-box;
}
button {
  width: 100%;
  padding: 10px;
  background-color: #206050;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #003020;
}
.error {
  color: red;
  margin-top: 10px;
}
</style>
