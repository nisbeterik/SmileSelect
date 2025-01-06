<template>
  <div class="dentist-dashboard container-fluid">
    <!-- Background Layer -->
    <div class="background-layer"></div>

    <!-- Header Section -->
    <header class="dashboard-header p-3 mb-4 shadow-sm">
      <div class="header-content d-flex justify-content-between align-items-center">
        <img src="/images/smileSelectIcon.png" alt="Smile Select Icon" class="header-image" />
        <h1 class="h3">Welcome {{ dentistName }}!</h1>
        <button class="button-primary" @click="logOutUser">Log Out</button>
      </div>
    </header>

    <!-- Conditional Content -->
    <div v-if="validUser">
      <!-- Top Row -->
      <div class="top-row row mb-4">
      </div>

      <!-- Bottom Row with Availability Page -->
      <div class="row justify-content-center">
        <div class="glass-card create-appointment col-12 col-md-8 col-lg-6 p-4 shadow rounded">
          <create-appointment-component />
        </div>
      </div>
    </div>

    <!-- Not Authenticated Section -->
    <div v-else class="row justify-content-center align-items-center vh-100">
      <div class="glass-card not-auth col-12 col-md-6 text-center p-4 shadow rounded bg-light">
        <h1>NOT LOGGED IN</h1>
        <button class="button-primary" @click="logOutUser">Home</button>
      </div>
    </div>

    <p v-if="message" class="alert alert-info mt-4">{{ message }}</p>
  </div>
</template>

<script>
import axios from '../axios';
import { useAuthStore } from '@/stores/auth';
import '/src/CSS/global.css';
import createAppointmentComponent from '@/components/CreateAppointmentComponent.vue';

export default {
  name: 'DentistDashboard',
  components: {
    createAppointmentComponent,
  },
  created() {
    this.checkUser();
    if (this.validUser) {
      this.fetchDentistDetails();
    }
  },
  data() {
    const authStore = useAuthStore();
    return {
      validUser: false,
      dentistId: authStore.id,
      role: authStore.role,
      message: '',
      dentistName: '',
    };
  },
  methods: {
    async checkUser() {
      this.validUser = this.role === 'DENTIST' && this.dentistId !== null;
    },
    async fetchDentistDetails() {
      try {

        const response = await axios.get(`/dentists/${this.dentistId}`);
        this.dentistName = `${response.data.firstName}`;
      } catch (error) {
        console.error('Error fetching dentist details:', error);
        this.dentistName = '';
        this.message = 'Failed to load dentist details.';
      }
    },
    async logOutUser() {
      const authStore = useAuthStore();
      try {
        authStore.clearAuth();
        this.$router.push({path: '/'});
      } catch (error) {
        console.error('LogOut Error:', error.response?.data || error.message);
      }
    },
  },
};
</script>

<style scoped>

.header-image {
  height: 100px;
  border: none;
}

.dentist-dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.dashboard-header {
  width: 100vw;
  position: relative;
  background-color: #2B6C5D;
  margin-left: calc(-50vw + 50%);
  color: #FFFFFF;
}

.glass-card {
  background-color: rgba(255, 255, 255, 0.9);
}

.button-primary {
  padding: 10px 20px;
  font-size: 16px;
  border-radius: 5px;
  width: auto;
}

.dentist-dashboard button:hover {
  background-color: #003020;
}

.dentist-dashboard button:focus {
  outline: none;
}
</style>
