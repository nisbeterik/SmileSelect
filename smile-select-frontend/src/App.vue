<template>
  <create-appointment></create-appointment>
</template>

<script>
import CreateAppointment from './components/CreateAppointment.vue';

export default {
  name: 'App',
  components: {
    CreateAppointment,
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
