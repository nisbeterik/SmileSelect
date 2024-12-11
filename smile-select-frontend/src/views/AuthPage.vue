<template>
  <div class="login-div" >
    <!-- Toggle between Login and Registration forms -->
    <div v-if="!isLoggedIn" class="card">
      <!-- Show Login Form -->
      <LoginComponent v-if="showLogin" @loginSuccess="handleLoginSuccess" />

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
import LoginComponent from '@/components/LoginComponent.vue';

export default {
  data() {
    return {
      isLoggedIn: false, // Tracks if the user is logged in
      showLogin: true, // Controls which form is shown by default
      userRole: '', // Stores the user's role
    };
  },
  components: {
    LoginComponent,
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
      if (this.userRole === 'DENTIST') {
        this.$router.push('/dentist-dashboard');
      } else if (this.userRole === 'PATIENT') {
        this.$router.push('/patient-dashboard');
      } else {
        // Default action, redirect to home or show an error
        this.$router.push('/');
      }
    },
  },
};
</script>

<style>
.login-div {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  width: 100%;
  margin: 0;
  box-sizing: border-box;
  background: url('../../public/images/wavyBackground.png') no-repeat center center;
  background-size: cover;
  backdrop-filter: blur(20px);
}
.login-div .card {
  max-width: 500px;
  width: 100%;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.4);
  backdrop-filter: blur(20px);
}

body {
  margin: 0;
}
.toggle-btn {
  margin-top: 20px;
  background-color: #206050;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.toggle-btn:hover {
  background-color: #003020;
}
</style>
