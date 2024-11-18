<template>
  <div id="app">
    <router-view></router-view>
    <RegistrationForm />
  </div>
  <MapPage></MapPage>
</template>

<script>
import MapPage from './components/MapPage.vue';

export default {
  name: 'App',
  components: {
    MapPage,
  },
};
</script>
