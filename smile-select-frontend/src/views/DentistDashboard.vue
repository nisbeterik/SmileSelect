<template>
  <div class="dentist-dashboard">
    <div class="background-layer"></div>
    <header class="dashboard-header">
      <div class="header-content">
        <h1>Welcome {{ role }} {{ dentistId }}</h1>
        <button class="button-secondary" @click="logOutUser">Log Out</button>
      </div>
    </header>
    <div v-if="validUser" class="top-row"></div>

    <!-- Bottom row with AvailabilityPage -->
    <div v-if="validUser" class="glass-card create-appointment">
      <create-appointment-component />
    </div>
    <div v-if ="!validUser" class="glass-card not-auth">
      <h1 style="text-align: center">NOT LOGGED IN</h1>
      <button class="button-primary" @click="logOutUser">Home</button>
    </div>

    <!--
    <button @click="getAllDentists">GET all /dentists</button>
    <button @click="getDentistById">GET dentist by ID</button>
    <button @click="updateDentistById">PUT/Update dentist by ID</button>
    <button @click="deleteDentistById">DELETE dentist by ID</button>
    -->
    <p v-if="message">{{ message }}</p>
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
.dentist-dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.header-content {
  justify-content: space-between;
  padding-top: 15px;
}
.button-secondary {
  max-width: 100px;
  margin-top: 0px;
}
.create-appointment {
  max-width: 50%;
  margin: 20px;
  background-color: rgba(255, 255, 255, 0.9);
}
.glass-card {

}
.not-auth {
  margin: auto;
}
</style>
