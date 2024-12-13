<template>
  <div class="availability-app">
    <div class="availability-sidebar">
      <div class="availability-sidebar-section">
        <h2>Instructions</h2>
        <ul>
          <li>Click an available slot to book it</li>
        </ul>
      </div>
      <div class="availability-sidebar-section">
        <h2>Select Clinic</h2>
        <div v-if="clinics.length">
          <select v-model="selectedClinicId" @change="fetchDentistsByClinic" class="clinic-dropdown">
            <option v-for="clinic in clinics" :key="clinic.id" :value="clinic.id">
              {{ clinic.name }}
            </option>
          </select>
          <p v-if="selectedClinic">Selected Clinic: {{ selectedClinic.name }}</p>
        </div>
        <p v-else>No clinics available.</p>
      </div>
      <div class="availability-sidebar-section">
        <h2>Select Dentist</h2>
        <div v-if="dentists.length">
          <select v-model="selectedDentistId" @change="fetchAppointmentsByDentist" class="dentist-dropdown">
            <option v-for="dentist in dentists" :key="dentist.id" :value="dentist.id">
              {{ dentist.firstName }} {{ dentist.lastName }}
            </option>
          </select>
          <p v-if="selectedDentist">Selected Dentist: {{ selectedDentist.firstName }} {{ selectedDentist.lastName }}</p>
        </div>
        <p v-else>No dentists available.</p>
      </div>
    </div>
    <div v-if="isBookingConfirmed" class="availability-modal-overlay">
      <div class="availability-modal">
        <h2>Booking Confirmed</h2>
        <p>Your appointment has been successfully booked!</p>
        <button @click="closeBookingConfirmation" class="btn-confirm">Close</button>
      </div>
    </div>
    <div class="availability-main">
      <FullCalendar
          class="availability-calendar"
          :options="calendarOptions"
      >
        <template v-slot:eventContent="arg">
          <b>{{ arg.timeText }}</b>
          <i>{{ arg.event.title }}</i>
        </template>
      </FullCalendar>
    </div>
    <div v-if="isModalVisible" class="availability-modal-overlay">
      <div class="availability-modal">
        <h2>Confirm Booking</h2>
        <p>Are you sure you want to book this appointment?</p>
        <input v-model="email" type="email" placeholder="Enter your email" class="modal-email-input" />
        <div class="modal-actions">
          <button @click="confirmBooking" class="btn-confirm">Confirm</button>
          <button @click="closeModal" class="btn-cancel">Cancel</button>
        </div>
        <p v-if="emailError" class="error-message">{{ emailError }}</p>
      </div>
    </div>
  </div>
</template>

<script>
import { defineComponent } from "vue";
import FullCalendar from "@fullcalendar/vue3";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";
import axios from "@/axios";

export default defineComponent({
  components: {
    FullCalendar,
  },
  data() {
    return {
      calendarOptions: {
        plugins: [dayGridPlugin, timeGridPlugin, interactionPlugin],
        headerToolbar: {
          left: "prev,next,today",
          center: "title",
          right: "dayGridMonth,timeGridWeek",
        },
        initialView: "timeGridWeek",
        events: [],
        editable: false,
        selectable: false,
        selectMirror: true,
        dayMaxEvents: true,
        weekends: false,
        slotMinTime: "06:00:00",
        slotMaxTime: "23:00:00",
        allDaySlot: false,
        eventClick: this.handleEventClick,
        validRange: {
          start: new Date().toISOString().split('T')[0],
        }
      },
      clinics: [],
      dentists: [],
      selectedClinicId: null,
      selectedDentistId: null,
      isModalVisible: false,
      isBookingConfirmed: false,
      emailError: null,
      selectedEventId: null,
      email: '',
    };
  },
  computed: {
    selectedClinic() {
      return this.clinics.find((clinic) => clinic.id === this.selectedClinicId);
    },
    selectedDentist() {
      return this.dentists.find((dentist) => dentist.id === this.selectedDentistId);
    },
  },
  mounted() {
    this.fetchClinics();
    this.handleClinicSelectionFromRoute();
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
        this.calendarOptions.events = response.data.map((appointment) => ({
          id: appointment.id,
          title: "Dentist Appointment",
          start: appointment.startTime,
          end: appointment.endTime,
        }));
      } catch (error) {
        console.error("Error fetching appointments:", error);
      }
    },
    handleEventClick(clickInfo) {
      this.selectedEventId = clickInfo.event.id;
      this.isModalVisible = true;
    },
    async confirmBooking() {
      if (!this.email) {
        this.emailError = "Please enter a valid email.";
        return;
      }

      try {
        const patientResponse = await axios.get(`/patients/email/${this.email}`);
        const patient = patientResponse.data;

        const appointmentData = {
          id: this.selectedEventId,
          patientId: patient.id,
        };

        const updateResponse = await axios.patch(`/appointments/add-patient`, appointmentData);

        console.log("Appointment updated successfully:", updateResponse.data);
        this.isModalVisible = false;
        this.isBookingConfirmed = true;

        setTimeout(() => {
          this.isBookingConfirmed = false;
        }, 3000);
      } catch (error) {
        if (error.response && error.response.status === 404) {
          this.emailError = "Patient not found. Please check the email.";
        } else {
          console.error("Error confirming booking:", error);
          this.emailError = "An error occurred while confirming your booking. Please try again.";
        }
      }
    },
    closeModal() {
      this.isModalVisible = false;
    },
    closeBookingConfirmation() {
      this.isBookingConfirmed = false;
    },
    handleClinicSelectionFromRoute() {
      const clinicIdFromRoute = this.$route.params.clinicId;
      if (clinicIdFromRoute) {
        this.selectedClinicId = clinicIdFromRoute;
        this.fetchDentistsByClinic();
      }
    },
  },
});
</script>

<style>
h2 {
  margin: 0;
  font-size: 16px;
}

ul {
  margin: 0;
  padding: 0 0 0 1.5em;
}

li {
  margin: 1.5em 0;
  padding: 0;
}

b {
  margin-right: 3px;
}

.availability-app {
  display: flex;
  min-height: 100%;
  font-family: Arial, Helvetica Neue, Helvetica, sans-serif;
  font-size: 14px;
}

.availability-sidebar {
  width: 300px;
  line-height: 1.5;
  background: #eaf9ff;
  border-right: 1px solid #d3e2e8;
}

.availability-sidebar-section {
  padding: 2em;
}

.availability-main {
  flex-grow: 1;
  padding: 3em;
}

.fc {
  max-width: 1100px;
  margin: 0 auto;
}

.availability-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.availability-modal {
  background: white;
  padding: 2em;
  border-radius: 5px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  max-width: 400px;
  width: 100%;
  text-align: center;
}

.modal-actions {
  margin-top: 1em;
  display: flex;
  justify-content: space-around;
}

.btn-confirm {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 0.5em 1em;
  cursor: pointer;
}

.btn-cancel {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 0.5em 1em;
  cursor: pointer;
}

.error-message {
  color: red;
  font-size: 14px;
  margin-top: 1em;
}
</style>