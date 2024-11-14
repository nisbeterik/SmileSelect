<template>
<div>
  <h1>Patient Registration</h1>
  <form @submit.prevent="submitPatient">
    <label for="first_name_patient">First Name:</label>
    <input
      type="text"
      id="first_name_patient"
      name="first_name"
      v-model="formData.firstName"
      required
    /><br /><br />

    <label for="last_name_patient">Last Name:</label>
    <input
      type="text"
      id="last_name_patient"
      name="last_name"
      v-model="formData.lastName"
      required
    /><br /><br />

    <label for="email_patient">Email:</label>
    <input
      type="email"
      id="email_patient" 
      name="email" 
      v-model="formData.email"
      required /><br /><br />

    <label for="password_patient">Password:</label>
    <input
      type="password"
      id="password_patient"
      name="password"
      v-model="formData.password"
      minlength="6"
      maxlength="16"
      required
    /><br /><br />

    <label for="dob_patient">Date of Birth:</label>
    <input
      type="date"
      id="dob_patient"
      name="date_of_birth"
      v-model="formData.dateOfBirth"
      required
    /><br /><br />

    <button type="submit">Register as Patient</button>
  </form>
  <p> {{ text }} </p>
</div>
</template>

<script>

import axios from '../axios.js'

export default {
  data() {
    return {
      formData: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        dateOfBirth: '',
      },
      text: '',
    };
  },
  methods: {
    async submitPatient() {
      try {
        const response = await axios.post(
          '/accounts/patients',
          this.formData
        );
        this.text = response.data;
        console.log(response.data);
        this.tex = "Successfully created account!"
      } catch (error) {
        console.error('Error fetching data:', error);
        this.text = 'Error occurred while fetching data';
      }
    },
  },
};
</script>

<style scoped></style>
