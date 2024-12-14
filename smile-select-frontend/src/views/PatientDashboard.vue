<template>
  <div class="patient-dashboard">
    <div class="background-layer"></div>
    <header class="dashboard-header">
      <div class="header-content">
        <h1>Welcome {{ role }} {{ patientId }}</h1>
        <button class="button-secondary" @click="logOutUser">Log Out</button>
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
      <AvailableAppointmentsComponent
          :clinics="clinics"
          @updateClinics="updateClinics"
          @clinicLocation="handleClinicLocation"
      />
    </div>

    <!-- Map Page (with clinic selection) -->
    <div
        class="glass-card map-page"
        ref="mapPageSection"
    >
      <MapPage
          @clinic-selected="handleClinicSelection"
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
import { useAuthStore } from "@/stores/auth";
import PatientAppointmentsComponent from "@/components/PatientAppointmentsComponent.vue";
import PreferredDateComponent from "../components/PreferredDateComponent.vue";
import AvailableAppointmentsComponent from "@/components/AvailableAppointmentsComponent.vue";
import MapPage from "@/views/MapPage.vue";
import "/src/CSS/global.css";

export default {
  name: "PatientDashboard",
  components: {
    PatientAppointmentsComponent,
    PreferredDateComponent,
    AvailableAppointmentsComponent,
    MapPage,
  },
  data() {
    const authStore = useAuthStore();
    return {
      validUser: false,
      patientId: authStore.id,
      role: authStore.role,
      selectedClinicName: null, // Clinic selected in booking component
      clinics: [],
    };
  },
  methods: {
    async checkUser() {
      this.validUser = this.role === "PATIENT" && this.patientId !== null;
    },
    handleClinicSelection() {
      this.$refs.availabilitySection.scrollIntoView({ behavior: "smooth" });
    },
    handleClinicLocation(clinicName) {
      this.selectedClinicName = clinicName;
      this.$refs.mapPageSection.scrollIntoView({ behavior: "smooth" });
    },
    updateClinics(newClinics) {
      this.clinics = newClinics;
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
  },
};
</script>