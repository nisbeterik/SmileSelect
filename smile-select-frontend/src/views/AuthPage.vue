<template>
  <div>
    <!-- Toggle between Login and Registration forms -->
    <div v-if="!isLoggedIn">
      <!-- Show Login Form -->
      <LoginView v-if="showLogin" @loginSuccess="handleLoginSuccess" />

      <!-- Toggle button -->
      <button @click="toggleForm" class="toggle-btn">
        {{
          showLogin
              ? 'Not registered yet? Sign up here'
              : 'Already registered? Log in here'
        }}
      </button>
    </div>

    <!-- Show dashboard or success message after login -->
    <div v-else>
      <h2>Welcome!</h2>
      <p>You are successfully logged in.</p>
      <button @click="navigateToDashboard">Go to Dashboard</button>
    </div>
  </div>
</template>

<script>
import LoginView from '@/views/LoginView.vue';

export default {
  data() {
    return {
      isLoggedIn: false, // Tracks if the user is logged in
      showLogin: true, // Controls which form is shown by default
      userRole: '', // Stores the user's role
    };
  },
  components: {
    LoginView,
  },
  methods: {
    handleLoginSuccess(role) {
      this.isLoggedIn = true; // Hide forms on login success
      this.userRole = role; // Store the role received from LoginView
    },
    toggleForm() {
      this.showLogin = !this.showLogin; // Toggle between login and registration forms
    },
    navigateToDashboard() {
      if (this.userRole === 'dentist') {
        this.$router.push('/dentist-dashboard');
      } else if (this.userRole === 'patient') {
        this.$router.push('/patient-dashboard');
      } else {
        // Default action, redirect to home or show an error
        this.$router.push('/');
      }
    },
  },
};
</script>

<style scoped>
.toggle-btn {
  margin-top: 20px;
  background-color: #007bff;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.toggle-btn:hover {
  background-color: #0056b3;
}
</style>
