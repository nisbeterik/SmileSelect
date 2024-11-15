<template>
  <div id="app">
    <router-view></router-view>
    <RegistrationForm />
    <create-appointment></create-appointment>
  </div>
</template>

<script>
import RegistrationForm from "@/views/RegistrationForm.vue";
import MapPage from './components/MapPage.vue'
import CreateAppointment from './components/CreateAppointment.vue';

export default {
  name: "App",
  components: {
    MapPage,
    CreateAppointment,
    RegistrationForm
  },
};
</script>
