<template>
  <div class="patient-dashboard">
    <div class="background-layer"></div>
    <header class="dashboard-header">
      <div class="header-content">
        <h1>Welcome {{ role }} {{ patientId }}</h1>
        <button class="button-secondary" @click="logOutUser">Log Out</button>
      </div>
    </header>
    <!-- Top row with PreferredDatePicker and PatientCurrentAppointment -->
    <div v-if="validUser" class="top-row">
      <div class="preferred-date-picker">
        <PreferredDateComponent />
      </div>
      <div class="glass-card patient-current-appointment">
        <PatientAppointmentsComponent />
      </div>
    </div>

    <!-- Bottom row with AvailabilityPage -->
    <div v-if="validUser" class="glass-card availability-page">
      <AvailableAppointmentsComponent />
    </div>
    <div v-if="validUser" class="glass-card map-page">
      <MapPage />
    </div>
    <div v-if ="!validUser" class="glass-card not-auth">
      <h1 style="text-align: center">NOT LOGGED IN</h1>
      <button class="button-primary" @click="logOutUser">Home</button>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';
import PatientAppointmentsComponent from '@/components/PatientAppointmentsComponent.vue';
import PreferredDateComponent from '../components/PreferredDateComponent.vue';
import AvailableAppointmentsComponent from '@/components/AvailableAppointmentsComponent.vue';
import MapPage from '@/views/MapPage.vue';
import '/src/CSS/global.css';

export default {
  name: 'PatientDashboard',
  components: {
    PatientAppointmentsComponent,
    PreferredDateComponent,
    AvailableAppointmentsComponent,
    MapPage,
  },
  created() {
    this.checkUser();
  },
  data() {
    const authStore = useAuthStore();
    return {
      validUser: false,
      patientId: authStore.id,
      role: authStore.role,
    };
  },
  methods: {
    async checkUser(){
      this.validUser = this.role === 'PATIENT' && this.dentistId !== null;
    },
    async logOutUser(){
      const authStore = useAuthStore();

      try{
        authStore.clearAuth();
        this.$router.push({ path: '/'});

      } catch(error){
        console.error('LogOut Error:', error.response?.data || error.message);
      }

    }
  }
};
</script>

<style scoped>
.header-title {
  padding: 20px;
}
.header-content {
  justify-content: space-between;
  padding-top: 15px;
}
.button-secondary {
  max-width: 100px;
  margin-top: 0px;
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
.not-auth{
  margin: auto;
}
</style>