<template>
  <div class="patient-dashboard container-fluid">
    <div class="background-layer"></div>
    <header class="dashboard-header p-3 mb-4 shadow-sm">
      <div class="header-content d-flex justify-content-between align-items-center">
        <img src="/images/smileSelectIcon.png" alt="Smile Select Icon" class="header-image" />
        <h1>Welcome {{ patientName }}!</h1>
        <button class="button-primary" @click="logOutUser">Log Out</button>
      </div>
    </header>

    <div v-if="validUser" class="top-row">
      <div class="preferred-date-picker">
        <PreferredDateComponent />
      </div>
      <div class="glass-card patient-current-appointment">
        <PatientAppointmentsComponent />
      </div>
    </div>

    <!-- Bottom row with AvailabilityPage -->
    <div v-if="validUser" class="glass-card availability-page" ref="availabilitySection">
      <AvailableAppointmentComponent
        :clinics="clinics"
        @updateClinics="updateClinics"
        @clinicLocation="handleClinicLocation"
        v-bind:selected-clinic-from-map="selectedClinicFromMap"
      />
    </div>

    <!-- Map Page (with clinic selection) -->
    <div v-if="validUser"
        class="glass-card map-page"
        ref="mapPageSection"
    >
      <MapPage
        @clinic-selected="handleClinicSelectionFromMap"
        v-bind:selected-clinic-name="selectedClinicName"
      />
    </div>

    <div v-if="!validUser" class="glass-card not-auth">
      <h1 style="text-align: center">NOT LOGGED IN</h1>
      <button class="button-primary" @click="logOutUser">Home</button>
    </div>
  </div>
</template>

<script>
import axios from '../axios';
import { useAuthStore } from "@/stores/auth";
import PatientAppointmentsComponent from "@/components/PatientAppointmentsComponent.vue";
import PreferredDateComponent from "../components/PreferredDateComponent.vue";
import AvailableAppointmentComponent from '@/components/AvailableAppointmentComponent.vue';
import MapPage from "@/views/MapPage.vue";
import "/src/CSS/global.css";

export default {
  name: "PatientDashboard",
  components: {
    AvailableAppointmentComponent,
    PatientAppointmentsComponent,
    PreferredDateComponent,
    MapPage,
  },
  data() {
    const authStore = useAuthStore();
    return {
      validUser: false,
      patientId: authStore.id,
      role: authStore.role,
      patientName: '',
      selectedClinicName: null,
      selectedClinicFromMap: null,
      clinics: [],
    };
  },
  methods: {
    async checkUser() {
      this.validUser = this.role === "PATIENT" && this.patientId !== null;
    },
    handleClinicSelectionFromMap(clinicId) {
      if (clinicId) {
        this.selectedClinicFromMap = clinicId;
      }
      this.$refs.availabilitySection.scrollIntoView({ behavior: "smooth" });
    },
    handleClinicLocation(clinicName) {
      this.selectedClinicName = clinicName;
      this.$refs.mapPageSection.scrollIntoView({ behavior: "smooth" });
    },
    updateClinics(newClinics) {
      this.clinics = newClinics;
    },
    async fetchPatientDetails() {
      try {

        const response = await axios.get(`/patients/${this.patientId}`);
        this.patientName = `${response.data.firstName}`;
      } catch (error) {
        console.error('Error fetching patient details:', error);
        this.patientName = 'Unknown Patient';
      }
    },
    async logOutUser() {
      const authStore = useAuthStore();
      try {
        authStore.clearAuth();
        this.$router.push({ path: "/" });
      } catch (error) {
        console.error("LogOut Error:", error.response?.data || error.message);
      }
    },
  },
  created() {
    this.checkUser();
    if (this.validUser) {
      this.fetchPatientDetails();
    }
  },
};
</script>

<style scoped>
/*Div*/

.header-image {
  height: 100px;
  border: none;
}
.patient-dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.dashboard-header {
  width: 100vw;
  position: relative;
  background-color: #2B6C5D;
  margin-left: calc(-50vw + 50%);
  color: #FFFFFF;
}
.top-row {
  display: flex;
  flex-wrap: wrap; /* Ensure items wrap on smaller screens */
  gap: 20px; /* Add spacing between elements */
  justify-content: center; /* Center content on smaller screens */
}

/* Availability and Map sections */
.availability-page, .map-page {
  margin: 20px;
  max-width: 100%; /* Allow full width on small screens */
  z-index: 1;
}
.button-primary {
  padding: 10px 20px;
  font-size: 16px;
  border-radius: 5px;
  width: auto;
}
</style>
