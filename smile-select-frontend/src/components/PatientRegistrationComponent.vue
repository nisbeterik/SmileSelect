<template>
  <div class="form-container">
    <div v-if="!isRegistered">
      <h1>Patient Registration</h1>
      <form @submit.prevent="submitPatient">
        <div class="form-group">
          <label for="first_name_patient">First Name:</label>
          <input
            type="text"
            id="first_name_patient"
            placeholder="eg. John"
            v-model="formData.firstName"
            required
          /><br /><br />
        </div>
        <div class="form-group">
          <label for="last_name_patient">Last Name:</label>
          <input
            type="text"
            id="last_name_patient"
            placeholder="eg. Johnsson"
            v-model="formData.lastName"
            required
          /><br /><br />
        </div>

        <div class="form-group">
          <label for="email_patient">Email:</label>
          <input
            type="email"
            id="email_patient"
            placeholder="john.johnsson@example.com"
            v-model="formData.email"
            required
          /><br /><br />
        </div>

        <div class="form-group">
          <label for="password_patient">Password:</label>
          <input
            type="password"
            id="password_patient"
            placeholder="S3ctr€T!96"
            v-model="formData.password"
            required
          /><br /><br />
        </div>

        <BirthdayPickerComponent @dateSelected="updateDob" />


        <button type="submit" class="button-primary">Register as Patient</button>
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
import '/src/CSS/global.css';
import BirthdayPickerComponent from '@/components/BirthdayPickerComponent.vue';


export default {
  components: {
    BirthdayPickerComponent,
  },
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
    updateDob(dob) {
      this.formData.dateOfBirth = dob;
    },
    async submitPatient() {
      try {
        const response = await axios.post('/patients/register', this.formData);
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
