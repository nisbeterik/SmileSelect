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
                <button @click="showCancelModal(appointment.id)"
                        class="btn btn-danger"
                        :disabled="isPast(appointment.startTime)">
                  Cancel
                </button>
              </div>
            </div>
          </div>
        </div>
        <div v-if="isModalVisible" class="availability-modal-overlay">
          <div class="availability-modal">
            <h2>Cancel Appointment</h2>
            <p>Are you sure you want to cancel this appointment?</p>
            <div class="modal-actions">
              <button @click="cancelAppointment" class="btn-cancel">
                Cancel
              </button>
              <button @click="closeModal" class="btn-confirm" style="background-color: #aaaaaa">Back</button>
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
import { EventBus } from '/src/bus/eventBus';

export default {
  name: 'PatientAppointmentsComponent',
  data() {
    const authStore = useAuthStore();
    return {
      token: authStore.token,
      patientId: authStore.id,
      appointments: [],
      appointmentText: "Your upcoming appointment(s)",
      showPastAppointments: false,
      infoText: "",
      infoTextClass: "",
      isModalVisible: false,
      selectedEventId: null,
    };
  },
  created() {
    EventBus.on('appointment-booked', this.loadAppointmentData);
  },
  beforeUnmount() {
    EventBus.off('appointment-booked', this.loadAppointmentData);
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
    closeModal() {
      this.isModalVisible = false;
    },
    showCancelModal(appointmentId){
      this.isModalVisible = true;
      this.selectedEventId = appointmentId
    },
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
        this.infoText = "Error loading appointments.";
        this.infoTextClass = "error-text";
      }
    },
    toggleView() {
      this.showPastAppointments = !this.showPastAppointments;
      if (this.showPastAppointments) {
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
    async cancelAppointment() {
      try {
        await this.$axios.patch(`/appointments/${this.selectedEventId}/cancel`, {
          headers: {
            Authorization: `Bearer ${this.token}`,
          },
        });
        this.loadAppointmentData();
        this.infoText = "Appointment canceled successfully.";
        this.infoTextClass = "success-text";
        this.isModalVisible = false;
        EventBus.emit('appointment-updated');
      } catch (error) {
        console.error('Error cancelling appointment:', error);
        this.infoText = "Error canceling appointment.";
        this.infoTextClass = "error-text";
      }
    },
  },
  mounted() {
    this.loadAppointmentData();
  },
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
