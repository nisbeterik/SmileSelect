<template>
  <div class="dentist-dashboard container-fluid">
    <!-- Background Layer -->
    <div class="background-layer"></div>

    <!-- Header Section -->
    <header class="dashboard-header p-3 mb-4 shadow-sm">
      <div class="header-content d-flex justify-content-between align-items-center">
        <img src="/images/smileSelectIcon.png" alt="Smile Select Icon" class="header-image" />
        <h1 class="h3">Welcome {{ role }} {{ dentistId }}</h1>
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
        <button class="btn btn-primary mt-3" @click="logOutUser">Home</button>
      </div>
    </div>


    <!--
    <div class="d-flex gap-2">
      <button class="btn btn-primary" @click="getAllDentists">GET all /dentists</button>
      <button class="btn btn-primary" @click="getDentistById">GET dentist by ID</button>
      <button class="btn btn-warning" @click="updateDentistById">PUT/Update dentist by ID</button>
      <button class="btn btn-danger" @click="deleteDentistById">DELETE dentist by ID</button>
    </div>
    -->
    <p v-if="message" class="alert alert-info mt-4">{{ message }}</p>
  </div>
</template>

<script>
//import axios from '../axios'; // Import the configured Axios instance
import { useAuthStore } from '@/stores/auth';
import '/src/CSS/global.css';
import createAppointmentComponent from '@/components/CreateAppointmentComponent.vue';

export default {
  name: 'DentistDashboard',
  components: {
    createAppointmentComponent,
  },
  created() {
    this.checkUser()
  },
  data() {
    const authStore = useAuthStore();
    return {
      validUser: false,
      dentistId: authStore.id,
      role: authStore.role,
      message: '',
    };
  },
  methods: {
    async checkUser(){
      this.validUser = this.role === 'DENTIST' && this.dentistId !== null;
    },
    async logOutUser(){
      const authStore = useAuthStore();

      try{
        authStore.clearAuth();
        this.$router.push({ path: '/'});

      } catch(error){
        console.error('LogOut Error:', error.response?.data || error.message);
      }

    }
    /*
    async getAllDentists() {
      try {
        const response = await axios.get('/dentists');
        console.log(response.data);

        const dentists = response.data
          .map(
            (dentist) =>
              `ID: ${dentist.id}, Name: ${dentist.firstName} ${dentist.lastName}`
          )
          .join('\n');

        this.message = `Fetched ${response.data.length} dentists:\n${dentists}`;
      } catch (error) {
        console.error('Error fetching all dentists:', error);
        this.message = 'Failed to fetch dentists.';
      }
    },
    async getDentistById() {
      const id = prompt('Enter Dentist ID:');
      if (!id) return;

      try {
        const response = await axios.get(`/dentists/${id}`);
        console.log(response.data);
        this.message = `Fetched dentist: ${response.data.firstName} ${response.data.lastName},
        Clinic: ${response.data.clinicName},
        Address: ${response.data.street}, ${response.data.city}`;
      } catch (error) {
        console.error(`Error fetching dentist with ID ${id}:`, error);
        if (error.response && error.response.status === 404) {
          this.message = `Dentist with ID ${id} not found.`;
        } else if (error.response && error.response.status === 403) {
          this.message = `Access denied. Please log in.`;
        } else {
          this.message = `Failed to fetch dentist with ID ${id}.`;
        }
      }
    },
    async updateDentistById() {
      const id = prompt('Enter Dentist ID to update:');
      if (!id) return;

      const updatedData = {
        firstName: prompt('Enter First Name:'),
        lastName: prompt('Enter Last Name:'),
        email: prompt('Enter Email:'),
        password: prompt('Enter Password:'),
        clinicId: parseInt(prompt('Enter Clinic ID:')), // Update associated clinic
      };

      try {
        const response = await axios.put(
          `/dentists/${id}`,
          updatedData
        );
        console.log(response.data);
        this.message = response.data;
      } catch (error) {
        console.error(`Error updating dentist with ID ${id}:`, error);
        if (error.response && error.response.status === 404) {
          this.message = `Dentist with ID ${id} not found.`;
        } else if (error.response && error.response.status === 403) {
          this.message = `Access denied. Please log in.`;
        } else {
          this.message = `Failed to update dentist with ID ${id}.`;
        }
      }
    },
    async deleteDentistById() {
      const id = prompt('Enter Dentist ID to delete:');
      if (!id) return;

      try {
        const response = await axios.delete(`/dentists/${id}`);
        console.log(response.data);
        this.message = `Dentist with ID ${id} deleted successfully.`;
      } catch (error) {
        console.error(`Error deleting dentist with ID ${id}:`, error);
        if (error.response && error.response.status === 404) {
          this.message = `Dentist with ID ${id} not found.`;
        } else if (error.response && error.response.status === 403) {
          this.message = `Access denied. Please log in.`;
        } else {
          this.message = `Failed to delete dentist with ID ${id}.`;
        }
      }
    },*/
  },
};
</script>

<style scoped>

.header-image {
  height: 100px;
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
