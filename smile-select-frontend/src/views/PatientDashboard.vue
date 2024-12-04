<template>
  <div class="dashboard">
    <div class="text-center mb-4">
      <h1 class="header-title">Welcome to the Patient Dashboard!</h1>
    </div>
    <PatientCurrentAppointment />
  </div>
</template>

<script>
import PatientCurrentAppointment from '@/views/PatientCurrentAppointment.vue';

export default {
  name: 'PatientDashboard',
  components: {
    PatientCurrentAppointment, 
  },
};
</script>

<style scoped>
.header-title {
  padding: 20px;
}
</style>