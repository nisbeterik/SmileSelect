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
            :class="{ 'input-error': inputErrors.firstName }"
            required
          />
          <small v-if="inputErrors.firstName" class="error">{{ inputErrors.firstName }}</small>
          <br /><br />
        </div>

        <div class="form-group">
          <label for="last_name_patient">Last Name:</label>
          <input
            type="text"
            id="last_name_patient"
            placeholder="eg. Johnsson"
            v-model="formData.lastName"
            :class="{ 'input-error': inputErrors.lastName }"
            required
          />
          <small v-if="inputErrors.lastName" class="error">{{ inputErrors.lastName }}</small>
          <br /><br />
        </div>

        <div class="form-group">
          <label for="email_patient">Email:</label>
          <input
            type="email"
            id="email_patient"
            placeholder="john.johnsson@example.com"
            v-model="formData.email"
            :class="{ 'input-error': inputErrors.email }"
            required
          />
          <small v-if="inputErrors.email" class="error">{{ inputErrors.email }}</small>
          <br /><br />
        </div>

        <div class="form-group">
          <label for="password_patient">Password:</label>
          <input
            type="password"
            id="password_patient"
            placeholder="S3ctr€T!96"
            v-model="formData.password"
            :class="{ 'input-error': inputErrors.password }"
            required
          />
          <small v-if="inputErrors.password" class="error">{{ inputErrors.password }}</small>
          <br /><br />
        </div>

        <BirthdayPickerComponent @dateSelected="updateDob" />
        <small v-if="inputErrors.dateOfBirth" class="error">{{ inputErrors.dateOfBirth }}</small>
        <br /><br />

        <button type="submit" class="button-primary">Register as Patient</button>
        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
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
      errorMessage: '',
      inputErrors: {},
    };
  },
  methods: {
    updateDob(dob) {
      this.formData.dateOfBirth = dob;
    },
    validateForm() {
      this.inputErrors = {};
      let isValid = true;

      // Validate first name
      if (!this.formData.firstName.trim()) {
        this.inputErrors.firstName = 'First name is required.';
        isValid = false;
      }

      // Validate last name
      if (!this.formData.lastName.trim()) {
        this.inputErrors.lastName = 'Last name is required.';
        isValid = false;
      }

      // Validate email
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailPattern.test(this.formData.email)) {
        this.inputErrors.email = 'Please enter a valid email address.';
        isValid = false;
      }

      // Validate password complexity (example: at least 8 characters)
      if (this.formData.password.length < 6) {
        this.inputErrors.password = 'Password must be at least 6 characters long.';
        isValid = false;
      }

      // Validate date of birth
      if (!this.formData.dateOfBirth) {
        this.inputErrors.dateOfBirth = 'Please select your date of birth.';
        isValid = false;
      }

      return isValid;
    },
    async submitPatient() {
      this.errorMessage = '';
      if (!this.validateForm()) {
        return; // Prevent submission if validation fails
      }

      try {
        const response = await axios.post('/patients/register', this.formData);
        console.log('Registration Successful:', response.data);

        this.isRegistered = true;
        this.$emit('registrationSuccess');
      } catch (error) {
        console.error('Registration Error:', error);
        // Show a meaningful message from the server if available
        if (error.response && error.response.data && error.response.data.message) {
          this.errorMessage = error.response.data.message;
        } else {
          this.errorMessage = 'An error occurred during registration. Please try again.';
        }
      }
    },
  },
};
</script>

<style scoped>
.error {
  color: red;
  margin-top: 10px;
  font-size: 0.875rem;
}

.input-error {
  border: 1px solid red;
}
</style>
