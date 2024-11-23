<template>
  <div class="dashboard">
    <h1>Welcome to the Dentist Dashboard!</h1>
    <button @click="getAllDentists">GET all /dentists</button>
    <button @click="getDentistById">GET dentist by ID</button>
    <button @click="updateDentistById">PUT/Update dentist by ID</button>
    <button @click="deleteDentistById">DELETE dentist by ID</button>

    <p v-if="message">{{ message }}</p>
  </div>
</template>

<script>
import axios from '../axios'; // Import the configured Axios instance

export default {
  name: 'DentistDashboard',
  data() {
    return {
      message: '',
    };
  },
  methods: {
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
    },
  },
};
</script>

<style scoped>
/* Add your styles here */
</style>
