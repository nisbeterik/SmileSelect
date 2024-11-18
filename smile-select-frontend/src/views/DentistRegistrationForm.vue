<template>
  <div>
    <div v-if="!isRegistered">
      <h1>Dentist Registration</h1>
      <form @submit.prevent="submitDentist">
        <label for="first_name_dentist">First Name:</label>
        <input
            type="text"
            id="first_name_dentist"
            v-model="formData.firstName"
            required
        /><br /><br />

        <label for="last_name_dentist">Last Name:</label>
        <input
            type="text"
            id="last_name_dentist"
            v-model="formData.lastName"
            required
        /><br /><br />

        <label for="email_dentist">Email:</label>
        <input
            type="email"
            id="email_dentist"
            v-model="formData.email"
            required
        /><br /><br />

        <label for="password_dentist">Password:</label>
        <input
            type="password"
            id="password_dentist"
            v-model="formData.password"
            required
        /><br /><br />

        <label for="longitude_dentist">Longitude:</label>
        <input
            type="number"
            id="longitude_dentist"
            v-model="formData.longitude"
            step="any"
            required
        /><br /><br />

        <label for="latitude_dentist">Latitude:</label>
        <input
            type="number"
            id="latitude_dentist"
            v-model="formData.latitude"
            step="any"
            required
        /><br /><br />

        <label for="street_dentist">Street:</label>
        <input
            type="text"
            id="street_dentist"
            v-model="formData.street"
            required
        /><br /><br />

        <label for="house_number_dentist">House Number:</label>
        <input
            type="text"
            id="house_number_dentist"
            v-model="formData.houseNumber"
            required
        /><br /><br />

        <label for="zip_dentist">ZIP Code:</label>
        <input
            type="number"
            id="zip_dentist"
            v-model="formData.zip"
            required
        /><br /><br />

        <label for="city_dentist">City:</label>
        <input
            type="text"
            id="city_dentist"
            v-model="formData.city"
            required
        /><br /><br />

        <button type="submit">Register as Dentist</button>
      </form>
    </div>
    <div v-else>
      <h2>Registration Successful!</h2>
      <p>Welcome! Redirecting to login...</p>
    </div>
  </div>
</template>

<script>
import axios from '@/axios';

export default {
  data() {
    return {
      formData: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        longitude: '',
        latitude: '',
        street: '',
        houseNumber: '',
        zip: '',
        city: '',
      },
      isRegistered: false,
    };
  },
  methods: {
    async submitDentist() {
      try {
        const response = await axios.post('/accounts/dentists', this.formData);
        console.log('Registration Successful:', response.data);

        // Emit success event to parent component
        this.isRegistered = true;
        this.$emit('registrationSuccess');
      } catch (error) {
        console.error('Registration Error:', error);
      }
    },
  },
};
</script>

<style scoped></style>
