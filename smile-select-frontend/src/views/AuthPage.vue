<template>
  <div class="login-div" >
    <!-- Back Arrow -->
    <button class="back-arrow" @click="goBack">
      ←
    </button>
    <!-- Toggle between Login and Registration forms -->
    <div v-if="!isLoggedIn" class="card">
      <!-- Show Login Form -->
      <LoginComponent v-if="showLogin" @loginSuccess="handleLoginSuccess" ></LoginComponent>

      <DentistRegistrationComponent v-if="!showLogin && (selectedRole === 'DENTIST')" ></DentistRegistrationComponent>
      <PatientRegistrationComponent v-if="!showLogin && (selectedRole === 'PATIENT')" ></PatientRegistrationComponent>
      <!-- Toggle button -->
      <button @click="toggleForm" class="button-secondary">
        {{
          showLogin
            ? 'Not registered yet? Sign up here'
            : 'Already registered? Log in here'
        }}
      </button>

    </div>
  </div>
</template>

<script>
import LoginComponent from '@/components/LoginComponent.vue';
import DentistRegistrationComponent from '@/components/DentistRegistrationComponent.vue';
import PatientRegistrationComponent from '@/components/PatientRegistrationComponent.vue';
import '/src/CSS/global.css';

export default {
  data() {
    return {
      isLoggedIn: false, // Tracks if the user is logged in
      showLogin: true, // Controls which form is shown by default
      selectedRole: '', // Stores the user's role
    };
  },
  components: {
    LoginComponent,
    DentistRegistrationComponent,
    PatientRegistrationComponent
  },
  created() {
    this.selectedRole = this.$route.query.role || 'PATIENT';
  },
  methods: {
    goBack() {
      this.$router.push('/'); // Navigate to the home screen
    },
    handleLoginSuccess() {
      this.isLoggedIn = true;
      this.navigateToDashboard();// Hide forms on login success// Store the role received from LoginView
    },
    toggleForm() {
      this.showLogin = !this.showLogin; // Toggle between login and registration forms
    },
    navigateToDashboard() {
      if (this.selectedRole === 'DENTIST') {
        this.$router.push('/dentist-dashboard');
      } else if (this.selectedRole === 'PATIENT') {
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
.button-secondary {
  margin-top: 20px;
}
</style>
