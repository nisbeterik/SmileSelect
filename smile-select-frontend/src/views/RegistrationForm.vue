<template>
  <div>
    <div v-if="!isRegistered">
      <h1>Patient Registration</h1>
      <form @submit.prevent="submitPatient">
        <label for="first_name_patient">First Name:</label>
        <input
          type="text"
          id="first_name_patient"
          v-model="formData.firstName"
          required
        /><br /><br />

        <label for="last_name_patient">Last Name:</label>
        <input
          type="text"
          id="last_name_patient"
          v-model="formData.lastName"
          required
        /><br /><br />

        <label for="email_patient">Email:</label>
        <input
          type="email"
          id="email_patient"
          v-model="formData.email"
          required
        /><br /><br />

        <label for="password_patient">Password:</label>
        <input
          type="password"
          id="password_patient"
          v-model="formData.password"
          required
        /><br /><br />

        <label for="dob_patient">Date of Birth:</label>
        <input
          type="date"
          id="dob_patient"
          v-model="formData.dateOfBirth"
          required
        /><br /><br />

        <button type="submit">Register as Patient</button>
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
  name: 'RegistrationForm',
  data() {
    return {
      formData: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        dateOfBirth: '',
      },
      isRegistered: false,
    };
  },
  methods: {
    async submitPatient() {
      try {
        const response = await axios.post('/accounts/patients', this.formData);
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
