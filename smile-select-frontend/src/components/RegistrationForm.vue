<template>
  <h1>Patient Registration</h1>
  <form action="/register-patient" method="post">
    <label for="first_name_patient">First Name:</label>
    <input
      type="text"
      id="first_name_patient"
      name="first_name"
      required
    /><br /><br />

    <label for="last_name_patient">Last Name:</label>
    <input
      type="text"
      id="last_name_patient"
      name="last_name"
      required
    /><br /><br />

    <label for="email_patient">Email:</label>
    <input type="email" id="email_patient" name="email" required /><br /><br />

    <label for="password_patient">Password:</label>
    <input
      type="password"
      id="password_patient"
      name="password"
      minlength="6"
      maxlength="16"
      required
    /><br /><br />

    <label for="dob_patient">Date of Birth:</label>
    <input
      type="date"
      id="dob_patient"
      name="date_of_birth"
      required
    /><br /><br />

    <button @click="submitPatient">Register as Patient</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      formData: {
        first_name: "",
        last_name: "",
        email: "",
        password: "",
        date_of_birth: "",
      },
      text: "",
    };
  },
  methods: {
    async submitPatient() {
      try {
        const response = await this.$axios.post(
          "/register-patient",
          this.formData
        );
        this.text = response.data;
      } catch (error) {
        console.error("Error fetching data:", error);
        this.text = "Error occurred while fetching data";
      }
    },
  },
};
</script>

<style scoped></style>
