<template>
  <div class="glass-container">
    <h3>Available Appointments</h3>
    <div class="row justify-content-center">
      <div>
        <div class="header">
          <div v-if="clinics.length" class="form-group select-field">
            <label for="clinic_select">Select Clinic:</label>
            <div class="field-container">
              <Multiselect
                class="clinic-field"
                :options="formattedClinics"
                :append-to-body="true"
                placeholder="Select Clinic"
                @update:modelValue="handleClinicChange"
                id="clinic_select"
                v-model="selectedClinicId"
                :class="{ 'input-error': inputErrors.clinicId }"
                required
                searchable
              >
              </Multiselect>
              <button
                v-if="selectedClinicId"
                @click="emitClinicName"
                class="button-primary map-pin-btn"
              >
                <img
                  src="/images/White-Pin.png"
                  alt="Pin Icon"
                  class="pin-icon"
                />
              </button>
            </div>
            <small v-if="inputErrors.clinicId" class="error">{{
              inputErrors.clinicId
            }}</small>
            <br /><br />
          </div>
          <div class="form-group select-field">
            <label for="dentist_select">Select Dentist:</label>
            <Multiselect
              class="clinic-field"
              :options="formattedDentists"
              :disabled="!selectedClinicId"
              placeholder="Select Dentist"
              :append-to-body="true"
              @update:modelValue="handleDentistChange"
              id="dentist_select"
              v-model="selectedDentistId"
              :class="{ 'input-error': inputErrors.dentistId }"
              required
              searchable
            >
            </Multiselect>
            <p v-if="!selectedClinicId">Select clinic first</p>
            <small v-if="inputErrors.dentistId" class="error">{{
              inputErrors.dentistId
            }}</small>
            <br /><br />
          </div>
        </div>
        <teleport to="body">
          <Datepicker
            v-if="selectedClinicId"
            v-model="selectedDate"
            :key="datePickerKey"
            :enable-time-picker="false"
            :append-to-body="true"
            :auto-apply="true"
            placeholder="Select a date with available appointments"
            :disabled-dates="disableDatesBeforeToday"
            :allowed-dates="allowedDates"
            @update:modelValue="handleDateSelection"
            class="custom-datepicker"
          ></Datepicker>
        </teleport>

        <p :class="infoTextClass">{{ infoText }}</p>

        <div class="scroll-wrapper">
          <div
            v-if="!appointments.length"
            class="no-appointment align-content-center"
          >
            <h1 v-if="selectedClinicId">
              Please select a date to show available appointments
            </h1>
            <h1 v-if="!selectedClinicId">Please select a clinic</h1>
          </div>
          <div class="appointments-scroll-container">
            <div
              v-for="appointment in appointments"
              :key="appointment.id"
              class="glass-card mb-3"
            >
              <div class="card-body">
                <p>
                  <strong>Time:</strong> {{ formatDate(appointment.startTime) }}
                </p>
                <p>
                  <strong>Duration:</strong>
                  {{
                    formatDuration(appointment.startTime, appointment.endTime)
                  }}
                </p>
                <p><strong>Dentist:</strong> {{ appointment.dentistName }}</p>
              </div>
              <button
                @click="showBookModal(appointment.id)"
                class="button-primary"
                :disabled="isPast(appointment.startTime)"
              >
                Book Now
              </button>
              <div></div>
            </div>
          </div>
        </div>
        <div v-if="isModalVisible" class="availability-modal-overlay">
          <div class="availability-modal">
            <h2>Confirm Booking</h2>
            <p>Are you sure you want to book this appointment?</p>
            <div class="modal-actions">
              <button @click="confirmBooking" class="btn-confirm">
                Confirm
              </button>
              <button @click="closeModal" class="btn-cancel">Cancel</button>
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
import Multiselect from '@vueform/multiselect';
import Datepicker from '@vuepic/vue-datepicker';

export default {
  name: 'AvailableAppointmentComponent',
  components: {
    Datepicker,
    Multiselect,
  },
  data() {
    const authStore = useAuthStore();
    return {
      token: authStore.token,
      patientId: authStore.id,
      appointments: [],
      appointmentText: 'Your upcoming appointment(s)',
      showPastAppointments: false,
      infoText: '',
      infoTextClass: '',
      clinics: [],
      dentists: [],
      selectedClinicId: null,
      selectedDentistId: null,
      isModalVisible: false,
      isBookingConfirmed: false,
      bookingFailed: false,
      emailError: null,
      selectedEventId: null,
      email: '',
      errorMessage: '',
      inputErrors: {},
      selectedDate: null,
      availableDates: [],
      datePickerKey: 0,
    };
  },
  props: {
    selectedClinicFromMap: {
      default: null,
    },
  },
  watch: {
    selectedClinicFromMap(newClinicID) {
      if (newClinicID) {
        this.selectedClinicId = newClinicID; // Set selectedClinicId
        this.handleClinicChange(); // Fetch appointments
      }
    },
  },
  computed: {
    formattedClinics() {
      return this.clinics.map((clinic) => ({
        value: clinic.id,
        label: `${clinic.name}, ${clinic.street}, ${clinic.houseNumber}, ${clinic.city}`,
      }));
    },
    allowedDates() {
      return this.availableDates.map((dateString) => new Date(dateString));
    },
    formattedDentists() {
      return this.dentists.map((dentist) => ({
        value: dentist.id,
        label: `${dentist.firstName} ${dentist.lastName}`,
      }));
    },
    selectedClinic() {
      return this.clinics.find((clinic) => clinic.id === this.selectedClinicId);
    },
    selectedDentist() {
      return this.dentists.find(
        (dentist) => dentist.id === this.selectedDentistId
      );
    },
  },
  mounted() {
    this.fetchClinics();
    this.loadSelections();
  },
  methods: {
    emitClinicName() {
      if (this.selectedClinic) {
        this.$emit('clinicLocation', this.selectedClinic.name);
      }
    },
    async fetchAvailableDatesForClinic() {
      try {
        const response = await axios.get(
          `/appointments/available-dates/clinic/${this.selectedClinicId}/`,
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
            },
          }
        );
        this.availableDates = response.data; // Update available dates
        this.refreshDatepicker(); // Refresh Datepicker to reflect new dates
      } catch (error) {
        console.error('Error fetching available dates:', error);
      }
    },
    async fetchAvailableDatesForDentist() {
      try {
        const response = await axios.get(
          `/appointments/available-dates/dentist/${this.selectedDentistId}/`,
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
            },
          }
        );
        this.availableDates = response.data; // Update available dates
        this.refreshDatepicker(); // Refresh Datepicker to reflect new dates
      } catch (error) {
        console.error('Error fetching available dates:', error);
      }
    },
    refreshDatepicker() {
      this.datePickerKey += 1; // Increment key to force Datepicker re-render
    },
    handleClinicChange(newValue) {
      if (newValue) {
        this.selectedClinicId = newValue;
      }
      this.appointments = [];
      this.selectedDentistId = null;
      this.fetchDentistsByClinic();

      this.fetchAvailableDatesForClinic();
      localStorage.setItem('selectedClinicId', this.selectedClinicId);
      localStorage.setItem('selectedDentistId', null);
      localStorage.setItem('selectedDate', null);
    },
    handleDentistChange(newValue) {
      if (newValue) {
        this.selectedDentistId = newValue;
      }
      this.appointments = [];
      this.fetchAvailableDatesForDentist();

      localStorage.setItem('selectedDentistId', this.selectedDentistId);
      localStorage.setItem('selectedDate', null);
    },
    handleDateSelection() {
      localStorage.setItem('selectedDate', this.selectedDate);

      const selectedDate = new Date(this.selectedDate);

      if (!isNaN(selectedDate)) {
        // Format the date as YYYY-MM-DD
        this.selectedDate = selectedDate.toISOString().split('T')[0];
      } else {
        console.error('Invalid date selected');
        this.selectedDate = null;
      }
      this.appointments = [];
      if (this.selectedDentistId === null) {
        this.fetchAppointmentsByClinic();
      } else {
        this.fetchAppointmentsByDentist();
      }
    },
    disableDatesBeforeToday(date) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return date < today;
    },
    loadSelections() {
      const savedClinicId = localStorage.getItem('selectedClinicId');
      const savedDentistId = localStorage.getItem('selectedDentistId');
      const savedDate = localStorage.getItem('selectedDate');
      if (savedClinicId) {
        this.selectedClinicId = savedClinicId;
        this.fetchDentistsByClinic();

        if (savedDentistId) {
          this.selectedDentistId = savedDentistId;
          this.fetchAvailableDatesForDentist();
          this.selectedDate = savedDate;
          this.handleDateSelection();
        } else {
          this.fetchAvailableDatesForClinic();
          this.selectedDate = savedDate;
          this.handleDateSelection();
        }
      }
    },
    async fetchClinics() {
      try {
        const response = await axios.get('/dentists/clinics');
        this.clinics = response.data;
      } catch (error) {
        console.error('Error fetching clinics:', error);
      }
    },
    async fetchDentistsByClinic() {
      try {
        this.dentists = [];
        const response = await axios.get(
          `/dentists?clinicId=${this.selectedClinicId}`
        );
        this.dentists = response.data;
      } catch (error) {
        console.error('Error fetching dentists:', error);
      }
    },
    async fetchAppointmentsByClinic() {
      try {
        const params = {
          onlyAvailable: true,
        };
        if (this.selectedDate) {
          params.date = this.selectedDate;
        }

        const response = await axios.get(
          `/appointments/clinic/${this.selectedClinicId}`,
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
            },
            params: params,
          }
        );
        const appointments = response.data;

        for (const appointment of appointments) {
          const dentistResponse = await this.$axios.get(
            `/dentists/${appointment.dentistId}`
          );
          appointment.dentistName = `${dentistResponse.data.firstName} ${dentistResponse.data.lastName}`;
        }
        this.appointments = appointments;
        if (this.appointments.length === 0) {
          this.appointmentText = 'No appointment(s) scheduled';
        }
        this.infoText = '';
      } catch (error) {
        console.error('Error fetching appointments:', error);
        this.appointmentText = 'No appointment(s) scheduled';
        this.infoText = 'Error loading appointments.';
        this.infoTextClass = 'error-text';
      }
    },
    async fetchAppointmentsByDentist() {
      try {
        const params = {
          onlyAvailable: true,
        };
        if (this.selectedDate) {
          params.date = this.selectedDate;
        }

        const response = await axios.get(
          `/appointments/dentist/${this.selectedDentistId}`,
          {
            headers: {
              Authorization: `Bearer ${this.token}`,
            },
            params: params,
          }
        );
        const appointments = response.data;

        for (const appointment of appointments) {
          const dentistResponse = await this.$axios.get(
            `/dentists/${appointment.dentistId}`
          );
          appointment.dentistName = `${dentistResponse.data.firstName} ${dentistResponse.data.lastName}`;
        }
        this.appointments = appointments;
        if (this.appointments.length === 0) {
          this.appointmentText = 'No appointment(s) scheduled';
        }
        this.infoText = '';
      } catch (error) {
        console.error('Error fetching appointments:', error);
        this.appointmentText = 'No appointment(s) scheduled';
        this.infoText = 'Error loading appointments.';
        this.infoTextClass = 'error-text';
      }
    },
    async confirmBooking() {
      try {
        const patientId = this.patientId;
        const appointmentData = {
          id: this.selectedEventId,
          patientId: patientId,
        };
        await axios.patch(`/appointments/booked-by-patient`, appointmentData);
        this.isModalVisible = false;
        this.isBookingConfirmed = true;

        console.log('Booking confirmed successfully!');
      } catch (error) {
        console.error('Error confirming booking:', error);
        this.isModalVisible = false;
        this.bookingFailed = true;
        if (error.response) {
          console.error('Error Response Status:', error.response.status);
          console.error('Error Response Data:', error.response.data);
        }
      }
    },
    showBookModal(appointmentId) {
      this.selectedEventId = appointmentId;
      this.isModalVisible = true;
    },
    closeModal() {
      this.isModalVisible = false;
    },
    formatDate(dateString) {
      const date = parseISO(dateString);
      return format(date, 'PP p');
    },
    closeBookingConfirmation() {
      this.isBookingConfirmed = false;
    },
    closeBookingFailed() {
      this.bookingFailed = false;
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
  },
};
</script>

<style scoped>
.clinic-field {
  border: 0px;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  background-color: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  width: 100%;
  box-sizing: border-box;
  flex: 1;
}
.form-group {
  width: 100%;
}
.button-primary {
  width: auto;
}
.field-container {
  display: flex;
  align-items: center;
  gap: 8px; /* Space between the field and button */
  position: relative;
}

.map-pin-btn {
  background-color: #206050; /* Green button */
  color: white;
  border: none;
  border-radius: 4px;
  padding: 6px 12px;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s ease;
}

.map-pin-btn:hover {
  background-color: #18403a;
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
.modal-actions {
  margin-top: 1em;
  display: flex;
  justify-content: space-around;
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
.btn-confirm {
  background-color: #206050;
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

.map-pin-btn:focus {
  outline: none;
  box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
}

.pin-icon {
  width: 16px;
  display: block;
  margin: 0 auto;
}
.error {
  color: red;
  margin-top: 10px;
  font-size: 0.875rem;
}
.input-error {
  border: 1px solid red;
}

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
  -webkit-mask-image: -webkit-linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0) 0%,
    /* Fade at the top */ rgba(0, 0, 0, 1) 2%,
    /* Fully opaque from 10% to 90% */ rgba(0, 0, 0, 1) 90%,
    /* Fully opaque */ rgba(0, 0, 0, 0) 100% /* Fade at the bottom */
  );
  mask-image: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0) 0%,
    /* Fade at the top */ rgba(0, 0, 0, 1) 2%,
    /* Fully opaque from 10% to 90% */ rgba(0, 0, 0, 1) 90%,
    /* Fully opaque */ rgba(0, 0, 0, 0) 100% /* Fade at the bottom */
  );
  mask-size: 10% 10%;
  -webkit-mask-size: 100% 100%;
  mask-repeat: no-repeat;
  -webkit-mask-repeat: no-repeat;
}

.button-primary {
  max-width: 50%;
}
.glass-container {
  max-width: 100%;
  margin: 20px;
  overflow: visible;
  z-index: auto;
}
.card-body {
  display: flex;
  flex-wrap: wrap;
  gap: 1em;
  align-items: center;
}
.card-body p {
  flex: 1 1 auto; /* Allow the elements to grow and shrink as needed */
  margin: 0; /* Remove default margin for cleaner layout */
}

.row {
  margin-top: 0px;
}
.custom-datepicker input {
  width: 100%;
  box-sizing: border-box;
}

.custom-datepicker {
  display: block;
  margin: 10px 0;
  width: 100%;
  --dp-primary-color: #206050;
  z-index: 2000;
  position: absolute;
  --dp-menu-min-width: 500px;
}

.custom-datepicker.datepicker {
  width: 100%;
  min-width: 200px;
}
.no-appointment {
  min-height: 300px;
  text-align: center;
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
    overflow: visible;
  }
}

@media (max-width: 768px) {
  .custom-datepicker {
    width: 100%; /* Adapt to smaller screens */
    left: 0;
    margin: 0 auto; /* Center align */
  }
}

.select-field {
  max-width: 50%;
  margin: 10px;
}

.error-text {
  color: darkred;
}
</style>
