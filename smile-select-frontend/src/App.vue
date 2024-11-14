<template>
  <div id="app">
    <router-view></router-view>
  </div>
</template>

<script>
import RegistationForm from "./components/RegistrationForm.vue";

export default {
  name: "App",
  components: {
    RegistationForm,
  },
};
</script>
