<template>
    <div>
      <!-- Toggle between Login and Registration forms -->
      <div v-if="!isLoggedIn">
        <!-- Show Login Form -->
        <LoginView v-if="showLogin" @loginSuccess="handleLoginSuccess" />
  
        <!-- Show Registration Form -->
        <RegistrationForm
          v-else
          @registrationSuccess="handleRegistrationSuccess"
        />
  
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
  import RegistrationForm from '@/views/RegistrationForm.vue';
  
  export default {
    data() {
      return {
        isLoggedIn: false, // Tracks if the user is logged in
        showLogin: true, // Controls which form is shown by default
      };
    },
    components: {
      LoginView,
      RegistrationForm,
    },
    methods: {
      handleLoginSuccess() {
        this.isLoggedIn = true; // Hide forms on login success
      },
      handleRegistrationSuccess() {
        this.showLogin = true; // Switch to login form after successful registration
      },
      toggleForm() {
        this.showLogin = !this.showLogin; // Toggle between login and registration forms
      },
      navigateToDashboard() {
        this.$router.push('/patient-dashboard'); // Redirect to dashboard
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
  