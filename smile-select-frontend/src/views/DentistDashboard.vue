<template>
  <div class="dashboard">
    <h1>Welcome to the Dentist Dashboard!</h1>
    <button @click="getAllDentists"> GET all /dentists </button>
    <button @click="getDentistById">GET dentist by ID</button>
    <button @click="updateDentistById">PUT/Update dentist by ID </button>
    <button @click="deleteDentistById">DELETE dentist by ID </button>

    <p v-if="message"> {{ message }}</p>
  </div>
</template>

<script>
import axios from 'axios';

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
        const response = await axios.get('http://localhost:8080/api/accounts/dentists');
        console.log(response.data);
        this.message = `Fetched ${response.data.length} dentists successfully.`;
      } catch (error) {
        console.error('Error fetching all dentists:', error);
        this.message = 'Failed to fetch dentists.';
      }
    },
    async getDentistById() {
      const id = prompt('Enter Dentist ID:');
      if (!id) return;

      try {
        const response = await axios.get(`http://localhost:8080/api/accounts/dentists/${id}`);
        console.log(response.data);
        this.message = `Fetched dentist: ${response.data.firstName} ${response.data.lastName}`;
      } catch (error) {
        console.error(`Error fetching dentist with ID ${id}:`, error);
        if (error.response && error.response.status === 404) {
          this.message = `Dentist with ID ${id} not found.`;
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
        street: prompt('Enter Street:'),
        houseNumber: prompt('Enter House Number:'),
        city: prompt('Enter City:'),
        zip: prompt('Enter ZIP Code:'),
        latitude: parseFloat(prompt('Enter Latitude:')),
        longitude: parseFloat(prompt('Enter Longitude:')),
      };

      try {
        const response = await axios.put(`http://localhost:8080/api/accounts/dentists/${id}`, updatedData);
        console.log(response.data);
        this.message = response.data;
      } catch (error) {
        console.error(`Error updating dentist with ID ${id}:`, error);
        if (error.response && error.response.status === 404) {
          this.message = `Dentist with ID ${id} not found.`;
        } else {
          this.message = `Failed to update dentist with ID ${id}.`;
        }
      }
    },
    async deleteDentistById() {
      const id = prompt('Enter Dentist ID to delete:');
      if (!id) return;

      try {
        const response = await axios.delete(`http://localhost:8080/api/accounts/dentists/${id}`);
        console.log(response.data);
        this.message = response.data;
      } catch (error) {
        console.error(`Error deleting dentist with ID ${id}:`, error);
        if (error.response && error.response.status === 404) {
          this.message = `Dentist with ID ${id} not found.`;
        } else {
          this.message = `Failed to delete dentist with ID ${id}.`;
        }
      }
    },
  },
};
</script>



