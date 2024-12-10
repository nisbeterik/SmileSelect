<template>
  <div class="dashboard">
    <div class="text-center mb-4">
      <h1 class="header-title">Welcome to the Patient Dashboard!</h1>
    </div>
    <div class="text-center mb-4">
      <PreferredDatePicker/>
    </div>
    <div class="text-center mb-4">
      <PatientCurrentAppointment />
    </div>
  </div>
</template>

<script>
import PatientCurrentAppointment from '@/views/PatientCurrentAppointment.vue';
import PreferredDatePicker  from './PreferredDatePicker.vue';

export default {
  name: 'PatientDashboard',
  components: {
    PatientCurrentAppointment, 
    PreferredDatePicker
  },
};
</script>

<style scoped>
.header-title {
  padding: 20px;
}
</style>