<template>
  <div class="form-container">
    <div v-if="!isRegistered">
      <h1>Dentist Registration</h1>
      <form @submit.prevent="submitDentist">
        <div class="form-group">
          <label for="first_name_dentist">First Name:</label>
          <input
            type="text"
            id="first_name_dentist"
            placeholder="John"
            v-model="formData.firstName"
            :class="{ 'input-error': inputErrors.firstName }"
            required
          />
          <small v-if="inputErrors.firstName" class="error">{{ inputErrors.firstName }}</small>
          <br /><br />
        </div>

        <div class="form-group">
          <label for="last_name_dentist">Last Name:</label>
          <input
            type="text"
            id="last_name_dentist"
            placeholder="Johnsson"
            v-model="formData.lastName"
            :class="{ 'input-error': inputErrors.lastName }"
            required
          />
          <small v-if="inputErrors.lastName" class="error">{{ inputErrors.lastName }}</small>
          <br /><br />
        </div>

        <div class="form-group">
          <label for="email_dentist">Email:</label>
          <input
            type="email"
            id="email_dentist"
            placeholder="john.johnsson@example.com"
            v-model="formData.email"
            :class="{ 'input-error': inputErrors.email }"
            required
          />
          <small v-if="inputErrors.email" class="error">{{ inputErrors.email }}</small>
          <br /><br />
        </div>
        <div class="form-group">
          <label for="password_dentist">Password:</label>
          <input
            type="password"
            id="password_dentist"
            placeholder="S3ctr€T!96"
            v-model="formData.password"
            :class="{ 'input-error': inputErrors.password }"
            required
          />
          <small v-if="inputErrors.password" class="error">{{ inputErrors.password }}</small>
          <br /><br />
        </div>
        <div class="form-group">
          <label for="clinic_dentist">Select Clinic:</label>
          <Multiselect
            class="clinic-field"
            :options="formattedClinics"
            id="clinic_dentist"
            v-model="formData.clinicId"
            :class="{ 'input-error': inputErrors.clinicId }"
            required
            searchable
          >
          </Multiselect>
          <small v-if="inputErrors.clinicId" class="error">{{ inputErrors.clinicId }}</small>
          <br /><br />
        </div>

        <button type="submit" class="button-primary">Register as Dentist</button>
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
import Multiselect from '@vueform/multiselect';

export default {
  components: {
    Multiselect
  },
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
      errorMessage: '',
      inputErrors: {}
    };
  },
  computed: {
    formattedClinics() {
      return this.clinics.map(clinic => ({
        value: clinic.id,
        label: `${clinic.name}, ${clinic.street}, ${clinic.houseNumber}, ${clinic.city}`,
      }));
    },
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
      this.errorMessage = '';
      if (!this.validateForm()) {
        return;
      }
      try {
        const response = await axios.post('/dentists/register', this.formData);
        console.log('Registration Successful:', response.data);

        // Emit success event to parent component
        this.isRegistered = true;

        // Automatically redirect to dentist login after 3 seconds
        setTimeout(() => {
          this.$emit('registrationSuccess');
        }, 3000);

      } catch (error) {
        console.error('Registration Error:', error);
        if (error.response && error.response.data && error.response.data.message) {
          this.errorMessage = error.response.data.message;
        } else {
          this.errorMessage = 'An error occurred during registration. Please try again.';
        }
      }
    },
    validateForm() {
      this.inputErrors = {};
      let isValid = true;

      if (!this.formData.firstName.trim()) {
        this.inputErrors.firstName = 'First name is required.';
        isValid = false;
      }

      if (!this.formData.lastName.trim()) {
        this.inputErrors.lastName = 'Last name is required.';
        isValid = false;
      }

      // Basic email pattern check
      const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailPattern.test(this.formData.email)) {
        this.inputErrors.email = 'Please enter a valid email address.';
        isValid = false;
      }

      // Password complexity check (basic example)
      if (this.formData.password.length < 6) {
        this.inputErrors.password = 'Password must be at least 6 characters long.';
        isValid = false;
      }
      if (!this.formData.clinicId) {
        this.inputErrors.clinicId = 'Please select a clinic.';
        isValid = false;
      }
      return isValid;
    },
  },
};
</script>

<style scoped>
/* Style for dropdown options */
::v-deep(.multiselect-option) {
  background-color: rgba(255, 255, 255, 0.5) !important; /* Set a semi-transparent white background */
  color: #333 !important;
  padding: 5px 10px; /* Add some spacing */
  border-radius: 4px; /* Optional: Add rounded corners */
  cursor: pointer; /* Make it clear the options are clickable */
  margin: 3px;
}

/* Hover effect for options */
::v-deep(.multiselect-option:hover) {
  background-color: rgba(200, 200, 200, 0.8) !important; /* Slightly darker on hover */
}

/* Selected option styling */
::v-deep(.multiselect-option-selected) {
  background-color: #206050 !important; /* Green for selected */
  color: white !important; /* White text for contrast */
}
::v-deep(.multiselect-fake-input) {
  display: none; /* Hides the fake input */
}
.clinic-field {
  border: 0px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  width: 100%;
  box-sizing: border-box;
  flex: 1;
}

.error {
  color: red;
  margin-top: 10px;
  font-size: 0.875rem;
}
.input-error {
  border: 1px solid red;
}

</style>
