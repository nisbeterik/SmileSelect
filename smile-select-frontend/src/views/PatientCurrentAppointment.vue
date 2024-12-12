<template>
  <div class="container py-5">
    <div class="row justify-content-center">
      <div>
        <h3>{{ appointmentText }}</h3>
        <button @click="toggleView" class="btn btn-primary mb-3">
          {{ showPastAppointments ? "Show Upcoming Appointments" : "Show Past Appointments" }}
        </button>
        <!-- Scrollable container -->
        <div class="scroll-wrapper">
          <div class="appointments-scroll-container">
            <div
              v-for="appointment in filteredAppointments"
              :key="appointment.id"
              class="glass-card mb-3"
            >
              <div class="card-body">
                <p><strong>Time:</strong> {{ formatDate(appointment.startTime) }}</p>
                <p><strong>Duration:</strong> {{ formatDuration(appointment.startTime, appointment.endTime) }}</p>
                <p><strong>Dentist:</strong> {{ appointment.dentistName }}</p>
                <p><strong>Clinic:</strong> {{ appointment.clinicName }}</p>
                <p><strong>Address:</strong> {{ appointment.address }}</p>
              </div>
              <div>
                <button
                  @click="cancelAppointment(appointment.id)"
                  class="btn btn-danger"
                  :disabled="isPast(appointment.startTime)"
                >
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';
import { format, differenceInMinutes, parseISO, isBefore } from 'date-fns';
import '/src/CSS/global.css';

export default {
  name: 'PatientCurrentAppointment',
  data() {
    const authStore = useAuthStore();
    return {
      token: authStore.token,
      patientId: authStore.id,
      appointments: [],
      appointmentText: "Your upcoming appointment(s)",
      showPastAppointments: false,
    };
  },
  computed: {
    filteredAppointments() {
      const now = new Date();
      return this.appointments.filter((appointment) =>
        this.showPastAppointments
          ? isBefore(parseISO(appointment.startTime), now) 
          : !isBefore(parseISO(appointment.startTime), now) 
      );
    },
  },
  methods: {
    async loadAppointmentData() {
      try {
        const response = await this.$axios.get(`/appointments/patient/${this.patientId}`);
        const appointments = response.data;

        for (const appointment of appointments) {
          const dentistResponse = await this.$axios.get(`/dentists/${appointment.dentistId}`);
          appointment.dentistName = `${dentistResponse.data.firstName} ${dentistResponse.data.lastName}`;
          appointment.clinicName = dentistResponse.data.clinicName;
          appointment.address = `${dentistResponse.data.street} ${dentistResponse.data.houseNumber}, ${dentistResponse.data.zip} ${dentistResponse.data.city}`;
        }

        this.appointments = appointments;
        if (this.appointments.length === 0) {
          this.appointmentText = "No appointment(s) scheduled";
        }
      } catch (error) {
        console.error('Error fetching appointments:', error);
        this.appointmentText = "No appointment(s) scheduled";
      }
    },
    toggleView() {
      this.showPastAppointments = !this.showPastAppointments;
      if (this.showPastAppointments){
        this.appointmentText = "Your past appointment(s)"
      } else {
        this.appointmentText = "Your upcoming appointment(s)"
      }
    },
    formatDate(dateString) {
      const date = parseISO(dateString);
      return format(date, 'PPpp');
    },
    formatDuration(startTime, endTime) {
      const start = parseISO(startTime);
      const end = parseISO(endTime);
      const durationInMinutes = differenceInMinutes(end, start);
      const hours = Math.floor(durationInMinutes / 60);
      const minutes = durationInMinutes % 60;
      return `${hours} hr ${minutes} min`;
    },
    isPast(dateString) {
      return isBefore(parseISO(dateString), new Date());
    },
    async cancelAppointment(appointmentId) {
      try {
        await this.$axios.patch(`/appointments/${appointmentId}/cancel`, {
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        });
        this.loadAppointmentData();
      } catch (error) {
        console.error('Error cancelling appointment:', error);
      }
    },
  },
  mounted() {
    this.loadAppointmentData();
  },
};
</script>

<style scoped>
.scroll-wrapper {
  position: relative;
  max-height: 510px; /* Set the maximum height of the wrapper */
  overflow: hidden; /* Hide the scrollable content that exceeds the container */
}
.appointments-scroll-container {
  max-height: 510px;
  overflow-y: auto;
  padding-right: 10px;
  -webkit-mask-image: -webkit-linear-gradient(
    to bottom,
    rgba(0, 0, 0, 1) 90%, /* Fully opaque for 90% of the container */
    rgba(0, 0, 0, 0) 100% /* Fade only in the last 10% */
  );
  mask-image: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 1) 90%, /* Fully opaque for 90% of the container */
    rgba(0, 0, 0, 0) 100% /* Fade only in the last 10% */
  );
  mask-size: 10% 10%;
  -webkit-mask-size: 100% 100%;
  mask-repeat: no-repeat;
  -webkit-mask-repeat: no-repeat;
}
/*
.scroll-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 30px; /* Height of the top fade
  pointer-events: none; /* Allow interaction with the scrollable area
  background: linear-gradient(
    to bottom,
    rgba(255, 255, 255, 0.8), /* Adjust this opacity to match your semi-transparent background
    rgba(255, 255, 255, 0) 100%
  );
  z-index: 1; /* Ensure it stays above the scrolling content
}

/* Bottom fade overlay using opacity
.scroll-wrapper::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 30px; /* Height of the bottom fade
  pointer-events: none;
  background: linear-gradient(
    to top,
    rgba(255, 255, 255, 0.8), /* Adjust this opacity to match your semi-transparent background
    rgba(255, 255, 255, 0) 100%
  );
  z-index: 1;
}
*/
.container {
  max-width: 100%;
  overflow: hidden;
}
.glass-card{
  max-height: 250px;
  display: flex;
  justify-content: space-between;
}
</style>