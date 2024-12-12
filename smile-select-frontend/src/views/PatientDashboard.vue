<template>
  <div class="patient-dashboard">
    <div class="background-layer"></div>
      <header class="dashboard-header">
        <div class="header-content">
          <h1>Welcome {{role}} {{patientId}}</h1>
        </div>
      </header>
        <!-- Top row with PreferredDatePicker and PatientCurrentAppointment -->
        <div class="top-row">
          <div class="preferred-date-picker">
            <PreferredDatePicker />
          </div>
          <div class="glass-card patient-current-appointment">
            <PatientCurrentAppointment />
          </div>
        </div>

        <!-- Bottom row with AvailabilityPage -->
        <div class="glass-card availability-page">
          <AvailabilityPage />
        </div>

    </div>
    </template>

<script>
import { useAuthStore } from '@/stores/auth';
import PatientCurrentAppointment from '@/views/PatientCurrentAppointment.vue';
import PreferredDatePicker  from './PreferredDatePicker.vue';
import AvailabilityPage from '@/views/AvailabilityPage.vue';
import '/src/CSS/global.css';

export default {
  name: 'PatientDashboard',
  components: {

    PatientCurrentAppointment, 
    PreferredDatePicker,
    AvailabilityPage
  },
  data(){
    const authStore = useAuthStore();
    return {
      patientId: authStore.id,
      role: authStore.role
    };
  },


};
</script>

<style scoped>
.header-title {
  padding: 20px;
}
.patient-dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.top-row {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}
.dashboard-header {
  background-color: #206050;
  color: white;
  padding: 10px 10px;
}
.preferred-date-picker {
}

.patient-current-appointment {
  flex: 1;
  width: 100%;
  margin: 20px;
  height: 750px;
}

.availability-page {
  margin-top: 20px;
}

.header-content {
  display: flex;
  padding-top: 15px;
  justify-content: center;
  align-items: center;
}
.background-layer {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: url('../../public/images/wavyBackground.png') no-repeat center center;
  background-size: cover;
  z-index: -1;
}
</style>