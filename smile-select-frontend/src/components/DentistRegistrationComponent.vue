<template>
  <div>
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
            required
          /><br /><br />
        </div>

        <div class="form-group">
          <label for="last_name_dentist">Last Name:</label>
          <input
            type="text"
            id="last_name_dentist"
            placeholder="Johnsson"
            v-model="formData.lastName"
            required
          /><br /><br />
        </div>

        <div class="form-group">
          <label for="email_dentist">Email:</label>
          <input
            type="email"
            id="email_dentist"
            placeholder="john.johnsson@example.com"
            v-model="formData.email"
            required
          /><br /><br />
        </div>
        <div class="form-group">
          <label for="password_dentist">Password:</label>
          <input
            type="password"
            id="password_dentist"
            placeholder="S3ctr€T!96"
            v-model="formData.password"
            required
          /><br /><br />
        </div>
        <div class="form-group">
          <label for="clinic_dentist">Select Clinic:</label>
          <Multiselect
            class="clinic-field"
            :options="formattedClinics"
            id="clinic_dentist"
            v-model="formData.clinicId"
            required
            searchable
          >
          </Multiselect>
          <br /><br />
        </div>

        <button type="submit" class="button-primary">Register as Dentist</button>
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

</style>
