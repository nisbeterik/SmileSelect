<template>
  <div class="patient-dashboard">
    <div class="background-layer"></div>
    <header class="dashboard-header">
      <div class="header-content">
        <h1>Welcome {{ role }} {{ patientId }}</h1>
      </div>
    </header>
    <!-- Top row with PreferredDatePicker and PatientCurrentAppointment -->
    <div v-if="role === 'PATIENT'" class="top-row">
      <div class="preferred-date-picker">
        <PreferredDateComponent />
      </div>
      <div class="glass-card patient-current-appointment">
        <PatientAppointmentsComponent />
      </div>
    </div>

    <!-- Bottom row with AvailabilityPage -->
    <div v-if="role === 'PATIENT'" class="glass-card availability-page">
      <AvailabilityPage />
    </div>
    <div v-if="role === 'PATIENT'" class="glass-card map-page">
      <MapPage />
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';
import PatientAppointmentsComponent from '@/components/PatientAppointmentsComponent.vue';
import PreferredDateComponent from '../components/PreferredDateComponent.vue';
import AvailabilityPage from '@/views/AvailabilityPage.vue';
import MapPage from '@/views/MapPage.vue';
import '/src/CSS/global.css';

export default {
  name: 'PatientDashboard',
  components: {
    PatientAppointmentsComponent,
    PreferredDateComponent,
    AvailabilityPage,
    MapPage,
  },
  data() {
    const authStore = useAuthStore();
    return {
      patientId: authStore.id,
      role: authStore.role,
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
.map-page {
  background-color: #206050;
  max-height: 600px;
  overflow: hidden;
  margin: 20px;
  margin-bottom: 50px;
}
</style>