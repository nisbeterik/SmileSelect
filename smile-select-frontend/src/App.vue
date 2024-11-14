<template>
  <div id="app">
    <router-view></router-view>
    <RegistrationForm />
  </div>
</template>

<script>
import RegistrationForm from "@/views/RegistrationForm.vue";


export default {
  name: "App",
  components: {
    RegistrationForm,
  },
};
</script>
