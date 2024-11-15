<template>
  <div id="app">
    <router-view></router-view>
    <PatientRegistrationForm />
    <DentistRegistrationForm />
  </div>
  <MapPage></MapPage>
</template>

<script>
import PatientRegistrationForm from "@/views/PatientRegistrationForm.vue";
import DentistRegistrationForm from "@/views/DentistRegistrationForm.vue";

import MapPage from './components/MapPage.vue';

export default {
  name: "App",
  components: {
    PatientRegistrationForm,
    DentistRegistrationForm,
    MapPage,
  },
};
</script>
