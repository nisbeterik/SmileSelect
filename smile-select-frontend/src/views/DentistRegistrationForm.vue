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

        <label for="clinic_dentist">Select Clinic:</label>
        <select
          id="clinic_dentist"
          v-model="formData.clinicId"
          required
        >
          <option v-for="clinic in clinics" :key="clinic.id" :value="clinic.id">
            {{ clinic.name }}, {{ clinic.street }}, {{ clinic.houseNumber }}, {{ clinic.city }}
          </option>
        </select>
        <br /><br />

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
        clinicId: '', // New field for clinic selection
      },
      clinics: [], // List of clinics fetched from the backend
      isRegistered: false,
    };
  },
  mounted() {
    this.fetchClinics();
  },
  methods: {
    async fetchClinics() {
      try {
        const response = await axios.get('/dentists/clinics');
        this.clinics = response.data
      } catch (error) {
        console.error('Error fetching clinics:', error);
      }
    },
    async submitDentist() {
      try {
        const response = await axios.post('/dentists/register', this.formData);
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
