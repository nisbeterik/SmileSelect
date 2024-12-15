<template>
  <div class="glass-container">
    <div class="row justify-content-center">
      <div>
        <div class="header">
          <h3>{{ appointmentText }}</h3>
          <button @click="toggleView" class="button-primary mb-3">
            {{ showPastAppointments ? "Show Upcoming Appointments" : "Show Past Appointments" }}
          </button>
        </div>

        <p :class="infoTextClass">{{ infoText }}</p>

        <div class="scroll-wrapper">
          <div class="appointments-scroll-container">
            <div v-for="appointment in filteredAppointments" :key="appointment.id" class="glass-card mb-3">
              <div class="card-body">
                <p><strong>Time:</strong> {{ formatDate(appointment.startTime) }}</p>
                <p><strong>Duration:</strong> {{ formatDuration(appointment.startTime, appointment.endTime) }}</p>
                <p><strong>Dentist:</strong> {{ appointment.dentistName }}</p>
                <p><strong>Clinic:</strong> {{ appointment.clinicName }}</p>
                <p><strong>Address:</strong> {{ appointment.address }}</p>
              </div>
              <div>
                <button @click="cancelAppointment(appointment.id)" class="btn btn-danger"
                        :disabled="isPast(appointment.startTime)">
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
import axios from '@/axios';

export default {
  name: 'AvailableAppointmentComponent',
  data() {
    const authStore = useAuthStore();
    return {
      token: authStore.token,
      patientId: authStore.id,
      appointments: [],
      appointmentText: "Your upcoming appointment(s)",
      showPastAppointments: false,
      infoText: "",
      infoTextClass: ""
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
    async fetchClinics() {
      try {
        const response = await axios.get("/dentists/clinics");
        this.clinics = response.data;
      } catch (error) {
        console.error("Error fetching clinics:", error);
      }
    },
    async fetchDentistsByClinic() {
      try {
        const response = await axios.get(`/dentists?clinicId=${this.selectedClinicId}`);
        this.dentists = response.data;
      } catch (error) {
        console.error("Error fetching dentists:", error);
      }
    },
    async fetchAppointmentsByDentist() {
      try {
        const response = await axios.get(`/appointments/dentist/${this.selectedDentistId}?onlyAvailable=true`);
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
        this.infoText = "Error loading appointments.";
        this.infoTextClass = "error-text";
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
  }
};
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
}

.scroll-wrapper {
  position: relative;
  max-height: 620px;
  /* Set the maximum height of the wrapper */
  overflow: hidden;
  /* Hide the scrollable content that exceeds the container */
}

.appointments-scroll-container {
  max-height: 620px;
  overflow-y: auto;
  padding-right: 10px;
  -webkit-mask-image: -webkit-linear-gradient(to bottom,
  rgba(0, 0, 0, 0) 0%,
    /* Fade at the top */
  rgba(0, 0, 0, 1) 2%,
    /* Fully opaque from 10% to 90% */
  rgba(0, 0, 0, 1) 90%,
    /* Fully opaque */
  rgba(0, 0, 0, 0) 100%
    /* Fade at the bottom */
  );
  mask-image: linear-gradient(to bottom,
  rgba(0, 0, 0, 0) 0%,
    /* Fade at the top */
  rgba(0, 0, 0, 1) 2%,
    /* Fully opaque from 10% to 90% */
  rgba(0, 0, 0, 1) 90%,
    /* Fully opaque */
  rgba(0, 0, 0, 0) 100%
    /* Fade at the bottom */
  );
  mask-size: 10% 10%;
  -webkit-mask-size: 100% 100%;
  mask-repeat: no-repeat;
  -webkit-mask-repeat: no-repeat;
}

.button-primary {
  max-width: 50%;
}

.glass-container{
  max-width: 100%;
  overflow: hidden;
  margin: 20px;
}

.row {
  margin-top: 0px;
}

.glass-card {
  margin-right: 10px;
  margin-left: 10px;
  margin-top: 10px;
  max-height: 250px;
  display: flex;
  justify-content: space-between;
}

/* Dynamic text color classes */
.success-text {
  color: green;
}
@media (max-width: 480px) {
  .appointments-scroll-container {
    max-height: 400px; /* Adjust height for smaller screens */
  }
}

.error-text {
  color: darkred;
}
</style>
