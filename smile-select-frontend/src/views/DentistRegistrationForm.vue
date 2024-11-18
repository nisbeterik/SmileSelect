<template>
  <div>
    <h1>Dentist Registration</h1>
    <form @submit.prevent="submitDentist">
      <label for="first_name_dentist">First Name:</label>
      <input
        type="text"
        id="first_name_dentist"
        name="first_name"
        v-model="formData.firstName"
        required
      /><br /><br />

      <label for="last_name_dentist">Last Name:</label>
      <input
        type="text"
        id="last_name_dentist"
        name="last_name"
        v-model="formData.lastName"
        required
      /><br /><br />

      <label for="email_dentist">Email:</label>
      <input
        type="email"
        id="email_dentist"
        name="email"
        v-model="formData.email"
        required
      /><br /><br />

      <label for="password_dentist">Password:</label>
      <input
        type="password"
        id="password_dentist"
        name="password"
        v-model="formData.password"
        minlength="6"
        maxlength="16"
        required
      /><br /><br />

      <label for="longitude_dentist">Longitude:</label>
      <input
        type="number"
        id="longitude_dentist"
        name="longitude"
        v-model="formData.longitude"
        step="any"
        required
      /><br /><br />

      <label for="latitude_dentist">Latitude:</label>
      <input
        type="number"
        id="latitude_dentist"
        name="latitude"
        v-model="formData.latitude"
        step="any"
        required
      /><br /><br />

      <label for="street_dentist">Street:</label>
      <input
        type="text"
        id="street_dentist"
        name="street"
        v-model="formData.street"
        required
      /><br /><br />

      <label for="house_number_dentist">House Number:</label>
      <input
        type="text"
        id="house_number_dentist"
        name="house_number"
        v-model="formData.houseNumber"
        required
      /><br /><br />

      <label for="zip_dentist">ZIP Code:</label>
      <input
        type="number"
        id="zip_dentist"
        name="zip"
        v-model="formData.zip"
        required
      /><br /><br />

      <label for="city_dentist">City:</label>
      <input
        type="text"
        id="city_dentist"
        name="city"
        v-model="formData.city"
        required
      /><br /><br />

      <button type="submit">Register as Dentist</button>
    </form>
    <p> {{ text }} </p>
  </div>
</template>

<script>
import axios from "../axios.js";

export default {
  data() {
    return {
      formData: {
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        longitude: "",
        latitude: "",
        street: "",
        houseNumber: "",
        zip: "",
        city: "",
      },
      text: "",
    };
  },
  methods: {
    async submitDentist() {
      try {
        const response = await axios.post(
            "/accounts/dentists",
            this.formData
            );
        this.text = "Successfully created account!";
        console.log(response.data);
      } catch (error) {
        console.error("Error creating dentist account:", error);
        this.text = "Error occurred while creating account";
      }
    },
  },
};
</script>

<style scoped></style>
