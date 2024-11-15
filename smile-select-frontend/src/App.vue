<template>
  <div id="app">
    <router-view></router-view>
    <RegistrationForm />
  </div>
  <MapPage></MapPage>
</template>

<script>
import RegistrationForm from "@/views/RegistrationForm.vue";

import MapPage from './components/MapPage.vue';

export default {
  name: "App",
  components: {
    RegistrationForm,
    MapPage,
  },
};
</script>
