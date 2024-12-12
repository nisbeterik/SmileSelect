<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <div class="header-content">
        <h1>Welcome to the Dashboard</h1>
      </div>
    </header>

    <div class="text-center mb-4">
      <PreferredDatePicker/>
    </div>
    <div class="text-center mb-4">
      <PatientCurrentAppointment />
    </div>
    <div class="text-center mb-4">
      <AvailabilityPage />
    </div>
  </div>
</template>

<script>
import PatientCurrentAppointment from '@/views/PatientCurrentAppointment.vue';
import PreferredDatePicker  from './PreferredDatePicker.vue';
import AvailabilityPage from '@/views/AvailabilityPage.vue';

export default {
  name: 'PatientDashboard',
  components: {

    PatientCurrentAppointment, 
    PreferredDatePicker,
    AvailabilityPage
  },
};
</script>

<style scoped>
.header-title {
  padding: 20px;
}
.dashboard-header {
  background-color: #206050;
  color: white;
  padding: 10px 10px;
}

.header-content {
  display: flex;
  padding-top: 15px;
  justify-content: center;
  align-items: center;
}
</style>