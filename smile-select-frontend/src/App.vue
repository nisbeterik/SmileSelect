<template>
  <div id="app">
    <router-view></router-view>
    <RegistrationForm />
    <DentistRegistrationForm />
  </div>
  <MapPage></MapPage>
</template>

<script>
import RegistrationForm from "@/views/RegistrationForm.vue";
import DentistRegistrationForm from "@/views/DentistRegistrationForm.vue";

import MapPage from './components/MapPage.vue';

export default {
  name: "App",
  components: {
    RegistrationForm,
    DentistRegistrationForm,
    MapPage,
  },
};
</script>
