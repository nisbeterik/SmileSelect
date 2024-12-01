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
          left: "prev, next, today",
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
      },
      clinics: [],
      dentists: [],
      selectedClinicId: null,
      selectedDentistId: null,
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
    handleDateSelect(selectInfo) {
      console.log(selectInfo);
    },
    handleEventClick(clickInfo) {
      if (confirm(`Are you sure you want to book '${clickInfo.event.title}'`)) {
        let data = {
          id: clickInfo.event.id,
        };
        this.bookAppointment(data);
      }
    },
    bookAppointment(slotInfo) {
      console.log(slotInfo);
    },
  },
});
</script>
  
  <style lang='css'>
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
  </style>