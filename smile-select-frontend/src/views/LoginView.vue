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
import { useAuthStore } from '@/stores/auth';

export default {
  name: 'DentistLoginView',
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
  max-width: 400px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
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
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
button:hover {
  background-color: #0056b3;
}
.error {
  color: red;
  margin-top: 10px;
}
</style>
