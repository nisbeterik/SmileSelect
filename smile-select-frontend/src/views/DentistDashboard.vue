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
export default {
  name: 'DentistDashboard',
  data() {
    return {
      message: ''
    }
  },

  methods: {
    getAllDentists() {
      // TODO - Placeholder for logic
      this.message = "Fetching all dentists...";
      console.log(this.message);
    },
    getDentistById() {
      // TODO - Placeholder for logic
      this.message = "Fetching dentist by ID...";
      console.log(this.message);
    },
    updateDentistById() {
      // TODO - Placeholder for logic
      this.message = "Update dentist by ID...";
      console.log(this.message);
    },
    deleteDentistById() {
      // TODO - Placeholder for logic
      this.message = "Deleteing dentist by ID...";
      console.log("Deleteing dentist by ID...");
    }
  }
};
</script>


