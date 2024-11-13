<template>
  <h1>Patient Registration</h1>
  <form action="/register_patient" method="post">
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

    <button type="submit">Register as Patient</button>
  </form>
</template>

<script>
export default {
  data() {
    return {
      text: "",
    };
  },
  methods: {},
};
</script>

<style scoped></style>
