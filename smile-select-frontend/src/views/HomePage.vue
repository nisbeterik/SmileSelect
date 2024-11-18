<template>
    <div class="home-page">
      <h1>Welcome to SmileSelect</h1>
      <h2>Smile-tacular dental care, just book and prepare!</h2>
      <div class="button-container">
        <button @click="navigateTo('/patient-registration')">Patient Registration Form</button>
        <button @click="navigateTo('/dentist-registration')">Dentist Registration Form</button>
        <button @click="navigateTo('/create-appointment')">Create Appointment</button>
        <button @click="navigateTo('/map')">Map Page</button>
        <button @click="navigateTo('/login')">Login</button>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: "HomePage",
    methods: {
      navigateTo(path) {
        this.$router.push(path);
      },
    },
  };
  </script>
  
  <style>
  .button-container {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
  
  button {
    padding: 0.5rem 1rem;
    font-size: 1rem;
    cursor: pointer;
  }
  </style>