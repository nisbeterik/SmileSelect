<template>
  <!-- Calendar Container -->
  <div class="container-fluid">
    <div class="row justify-content-center">
      <div class="col-12">
        <div class="calendar-container">
          <FullCalendar ref="calendar" :options="calendarOptions" @select="handleSelect" />
        </div>
      </div>
    </div>
  </div>
  <div v-if="showCreateAppointmentModal" class="modal fade show d-block" tabindex="-1" @click.self="closeCurrentModal()">
    <div class="modal-dialog modal-lg">
      <div class="modal-content p-3">
        <h3 class="text-center mb-4">Create Appointment</h3>

        <div v-if="selectedSlot.date" class="mb-3">
          <strong>{{ selectedSlot.date }}</strong>
        </div>

        <div class="mb-3">
          <label for="startTime" class="form-label">Start Time</label>
          <div class="input-group">
            <input type="time" class="form-control" v-model="selectedSlot.startTime" step="300" required />
            <button class="btn btn-outline-secondary" @click="adjustTime('start', -5)">-5</button>
            <button class="btn btn-outline-secondary" @click="adjustTime('start', 5)">+5</button>
          </div>
        </div>

        <div class="mb-3">
          <label for="endTime" class="form-label">End Time</label>
          <div class="input-group">
            <input type="time" class="form-control" v-model="selectedSlot.endTime" step="300" required />
            <button class="btn btn-outline-secondary" @click="adjustTime('end', -5)">-5</button>
            <button class="btn btn-outline-secondary" @click="adjustTime('end', 5)">+5</button>
          </div>
        </div>

        <div class="mb-3">
          <label for="assign-patient" class="form-label">Patient Email</label>
          <div class="input-group">
            <input type="text" class="form-control" v-model="patientQuery" placeholder="Enter patient email" />
            <button class="btn btn-info" @click="findPatientByEmail()">Check</button>
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2">
          <button class="btn btn-success" @click="saveAppointment()">Save Appointment</button>
          <button class="btn btn-danger" @click="closeCurrentModal()">Cancel</button>
        </div>
      </div>
    </div>
  </div>
  <div v-if="showAppointmentDetailsModal" class="modal fade show d-block" tabindex="-1" @click.self="closeCurrentModal()">
    <div class="modal-dialog modal-lg">
      <div class="modal-content p-3">
        <h3 class="text-center mb-4">Appointment Details</h3>

        <p><strong>Status:</strong> {{ selectedEvent.status }}</p>
        <p><strong>Date:</strong> {{ selectedEvent.date }}</p>
        <p><strong>Time:</strong> {{ selectedEvent.time }}</p>

        <div v-if="selectedEvent.patientId && selectedEvent.patientId !== 'null'" class="mb-3">
          <p>
            <strong>Patient Name:</strong> {{ selectedEvent.patientName }}
            <button class="btn btn-outline-danger btn-sm ms-2" @click="removePatientFromAppointment">Remove</button>
          </p>
          <p><strong>Patient Email:</strong> {{ selectedEvent.patientEmail }}</p>
        </div>

        <div v-else class="mb-3">
          <label for="assign-patient" class="form-label">Add Patient</label>
          <div class="input-group">
            <input type="text" class="form-control" v-model="patientQuery" placeholder="Enter patient email" />
            <button class="btn btn-primary" @click="addPatientToAppointment()">Add</button>
          </div>
        </div>

        <div class="d-flex justify-content-end gap-2">
          <button class="btn btn-danger" @click="deleteAppointment">Delete</button>
          <button class="btn btn-secondary" @click="closeCurrentModal()">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import listPlugin from '@fullcalendar/list';
import interactionPlugin from '@fullcalendar/interaction';
import { useAuthStore } from '@/stores/auth';
import bootstrap5Plugin from '@fullcalendar/bootstrap5';
import 'bootstrap/dist/css/bootstrap.css';


const bookedColor = '#FF5733';
const availableColor = '#28A745';
const selectedColor = '#C6B700';

export default {
  name: 'CreateAppointmentComponent',
  components: {
    FullCalendar,
  },
  mounted() {
    this.loadAppointments();
    this.intervalId = setInterval(this.loadAppointments, 10000); // Update exisiting appointments every 10 seconds
  },
  beforeUnmount() {
    clearInterval(this.intervalId);
  },
  data() {
    const authStore = useAuthStore();
    return {
      token: authStore.token,
      dentistId: authStore.id,
      multiSlotMode: false,
      selectedSlots: [],
      selectedEventId: null,
      calendarOptions: {
        plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin, listPlugin, bootstrap5Plugin],
        initialView: 'timeGridWeek',
        firstDay: 1,
        weekends: false,
        eventOverlap: false,
        events: [],
        selectable: true,
        select: this.handleSelect,
        eventClick: this.handleEventClick,
        eventDidMount: this.eventDidMount,
        slotMinTime: '07:00:00',
        slotMaxTime: '19:00:00',
        slotDuration: '00:15:00',
        snapDuration: '00:05:00',
        headerToolbar: {
          left: 'title',
          center: 'prev,next today multiSlotMode createTimeSlots dayGridMonth timeGridWeek listWeek',
          right: '',
        },
        customButtons: {
          multiSlotMode: {
            text: 'MultiSlotMode',
            click: this.toggleMultiSlotMode,
          },
          createTimeSlots: {
            text: 'Create Slots',
            click: this.createMultipleTimeSlots,
          },
        },
        views: {
          timeGridMonth: {
            titleFormat: { year: 'numeric', month: 'long' },
          },
          timeGridWeek: {
            titleFormat: '',
          },
        },
      },
      showCreateAppointmentModal: false,
      showAppointmentDetailsModal: false,
      selectedSlot: {
        startTime: null,
        endTime: null,
        date: null,
      },
      patientQuery: null,
      patientId: null,
      searchResults: null,
      selectedEvent: null,
      intervalId: null,
    };
  },
  methods: {
    toggleMultiSlotMode() {
      if (this.multiSlotMode === true) {
        this.multiSlotMode = false;
        this.selectedSlots = [];
        this.loadAppointments();
      } else {
        this.multiSlotMode = true;
      }
    },
    adjustTime(type, minutes) {
      const timeString =
          type === 'start'
              ? this.selectedSlot.startTime
              : this.selectedSlot.endTime;
      const [hours, mins] = timeString.split(':').map(Number);

      const date = new Date();
      date.setHours(hours, mins + minutes);

      const newHours = date.getHours().toString().padStart(2, '0');
      const newMinutes = date.getMinutes().toString().padStart(2, '0');
      const newTime = `${newHours}:${newMinutes}`;

      if (newTime >= '07:00' && newTime <= '19:00') {
        if (type === 'start') {
          this.selectedSlot.startTime = newTime;

          if (newTime >= this.selectedSlot.endTime) {
            date.setMinutes(date.getMinutes() + 30);
            this.selectedSlot.endTime = `${date
                .getHours()
                .toString()
                .padStart(2, '0')}:${date
                .getMinutes()
                .toString()
                .padStart(2, '0')}`;
          }
        } else {
          if (newTime > this.selectedSlot.startTime) {
            this.selectedSlot.endTime = newTime;
          }
        }
      }
    },

    checkOverlap(selectedSlot) {
      const selectedStart = new Date(
          `${selectedSlot.date}T${selectedSlot.startTime}:00`
      );
      const selectedEnd = new Date(
          `${selectedSlot.date}T${selectedSlot.endTime}:00`
      );

      const overlaps = this.calendarOptions.events.some((event) => {
        const eventStart = new Date(event.start);
        const eventEnd = new Date(event.end);

        return (
            (selectedStart >= eventStart && selectedStart < eventEnd) ||
            (selectedEnd > eventStart && selectedEnd <= eventEnd) ||
            (selectedStart <= eventStart && selectedEnd >= eventEnd)
        );
      });

      if (overlaps) {
        alert('Selected time overlaps with an existing appointment!');
      }

      return overlaps;
    },

    formatTime(date) {
      return date.toTimeString().slice(0, 5);
    },

    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },

    handleSelect(info) {
      if (this.multiSlotMode == true || info.jsEvent.shiftKey) {
        const slot = {
          date: this.formatDate(info.start),
          startTime: this.formatTime(info.start),
          endTime: this.formatTime(info.end),
        };

        this.selectedSlots.push(slot);
        this.createMultiSlotModeTempSlot(slot);
      } else { //single slot creating

        const calendarApi = this.$refs.calendar.getApi();
        const isWeekView = calendarApi.view.type === 'timeGridWeek';

        this.selectedSlot.date = this.formatDate(info.start);

        if (isWeekView) {
          this.selectedSlot.startTime = this.formatTime(info.start);
          this.selectedSlot.endTime = this.formatTime(info.end);
        } else {
          this.selectedSlot.startTime = '08:00';
          this.selectedSlot.endTime = '10:00';
        }

        const overlap = this.checkOverlap(this.selectedSlot);
        if (!overlap) {
          this.showCreateAppointmentModal = true;
        }
      }
    },

    createMultiSlotModeTempSlot() {
      var index = -1;

      this.selectedSlots.forEach(slot => {
        var overlap = false;

        if ((index * -1) === this.selectedSlots.length) { //checks if the latest slot overlaps
          overlap = this.checkOverlap(slot);
        }
        if (overlap) {
          this.selectedSlots.pop();
        } else {
          this.calendarOptions.events.push({
            id: index,
            title: 'Wating for creation',
            start: `${slot.date}T${slot.startTime}`,
            end: `${slot.date}T${slot.endTime}`,
            backgroundColor: selectedColor,
          });
          index--;
        }
      });
    },

    async createMultipleTimeSlots() {

      this.calendarOptions.events = this.calendarOptions.events.filter(event => event.id >= 0);

      try {
        const appointmentPromises = [];
        for (const slot of this.selectedSlots) {
          const promise = this.saveAppointment(slot);
          appointmentPromises.push(promise);
        }

        await Promise.all(appointmentPromises);

        this.selectedSlots = [];

      } catch (error) {
        console.error('Error creating time slots:', error);
        alert('Failed to create time slots.');
      }
    },

    async getPatientInfo(patientId) {
      try {
        const response = await this.$axios.get(
            `/patients/booking/${patientId}`
        );
        return response.data;
      } catch (error) {
        console.error(
            'Error retrieving patient:',
            error.response?.data || error.message
        );
      }
    },

    async handleEventClick(info) {
      const event = info.event;

      const eventTime =
          this.formatTime(event.start) + ' - ' + this.formatTime(event.end);
      const patientId = event.extendedProps?.patientId;

      this.selectedEvent = {
        id: event.id,
        status: event.title,
        date: this.formatDate(event.start),
        time: eventTime,
      };

      if (patientId && patientId !== 'null') {
        try {
          const patient = await this.getPatientInfo(patientId);
          if (patient) {
            this.selectedEvent = {
              ...this.selectedEvent,
              patientId,
              patientName: `${patient.firstName} ${patient.lastName}`,
              patientEmail: patient.email,
            };
          }
        } catch (error) {
          console.error('Failed to fetch patient details:', error);
        }
      }
      if (!event.extendedProps.originalColor) {
        event.setExtendedProp('originalColor', event.backgroundColor);
      }
      event.setProp('backgroundColor', selectedColor);

      this.selectedEventId = event.id;

      const calendarApi = this.$refs.calendar.getApi();
      calendarApi.refetchEvents();

      this.showAppointmentDetailsModal = true;
    },

    closeCurrentModal() { //resets all modal settings

      if (this.selectedEventId) {
        const calendarApi = this.$refs.calendar.getApi();
        const selectedEvent = calendarApi.getEventById(this.selectedEventId);

        if (selectedEvent) {
          const originalColor = selectedEvent.extendedProps.originalColor
              || (selectedEvent.extendedProps.patientId === null ? availableColor : bookedColor);
          selectedEvent.setProp('backgroundColor', originalColor);
        }
      }

      this.selectedEvent = null;
      this.patientId = null;
      this.patientQuery = null;
      this.showAppointmentDetailsModal = false;
      document.querySelectorAll('.fc-event').forEach((el) => el.classList.remove('selected-event'));
      this.showCreateAppointmentModal = false;
      this.selectedSlot = {startTime: '', endTime: '', date: null};
    },

    async saveAppointment(slotData = null) {
      let slot;
      if (slotData === null) {
        slot = {...this.selectedSlot};
      } else {
        slot = slotData;
      }

      try {
        if (this.patientQuery) {
          await this.findPatientByEmail();
        }

        const overlap = this.checkOverlap(slot);
        if (overlap) {
          this.closeCurrentModal();
          return;
        }

        const formatToLocalDateTime = (date, time) => {
          return `${date}T${time}:00`;
        };

        const startDateTime = formatToLocalDateTime(
            slot.date,
            slot.startTime
        );
        const endDateTime = formatToLocalDateTime(
            slot.date,
            slot.endTime
        );

        var appointmentColor = bookedColor;
        var appointmentTitle = 'Booked';

        if (this.patientId === null) {
          appointmentColor = availableColor;
          appointmentTitle = 'Available';
        }


        var newAppointment = {
          dentistId: `${this.dentistId}`,
          startTime: `${startDateTime}`,
          endTime: `${endDateTime}`,
          patientId: `${this.patientId}`
        };

        const response = await this.$axios.post('/appointments', newAppointment);
        console.log(response)
        const appointmentId = response.data.id;


        this.calendarOptions.events.push({
          id: appointmentId,
          title: appointmentTitle,
          start: `${slot.date}T${slot.startTime}`,
          end: `${slot.date}T${slot.endTime}`,
          backgroundColor: appointmentColor,
        });
      } catch (error) {
        alert('Error saving appointment');
        console.error(
            'Error saving appointment:',
            error.response?.data || error.message
        );
      }
      this.closeCurrentModal();
    },

    // TODO: Make this method load only appointments for the logged in dentist instead of all appointments
    async loadAppointments() {
      try {
        this.calendarOptions.events = [];
        var response = await this.$axios.get(`/appointments/dentist/${this.dentistId}`);
        var existingAppointments = response.data;

        if (this.selectedSlots) {
          this.createMultiSlotModeTempSlot()
        }


        Object.values(existingAppointments).forEach((appointment) => {
          var appointmentColor = bookedColor;
          var appointmentTitle = 'Booked';

          if (appointment.patientId === null) {
            appointmentColor = availableColor;
            appointmentTitle = 'Available';
          }

          this.calendarOptions.events.push({
            id: `${appointment.id}`,
            title: appointmentTitle,
            start: `${appointment.startTime}`,
            end: `${appointment.endTime}`,
            patientId: `${appointment.patientId}`,
            backgroundColor: appointmentColor,
          });
        });
      } catch (error) {
        console.error(
            'Error loading appointment:',
            error.response?.data || error.message
        );
      }
    },

    async deleteAppointment() {
      try {
        const appointmentId = this.selectedEvent.id;

        var response = await this.$axios.delete(`/appointments/${appointmentId}`);
        var deletedAppointment = response.data;
        console.log(deletedAppointment)

        this.calendarOptions.events = this.calendarOptions.events.filter(event => event.id !== appointmentId);

        this.closeCurrentModal();

      } catch (error) {
        alert('Error deleting appointment');
        console.error(
            'Error deleteing appointment:',
            error.response?.data || error.message
        );
      }
    },
    async searchPatients() {
      try {
        var response = await this.$axios.get(`/patients/search`, {
          params: {
            query: this.patientQuery
          }
        });

        // Store or process the results
        this.searchResults = response.data;
        console.log(this.searchResults)
      } catch (error) {
        console.error('Error searching patients:', error);
      }
    },
    async findPatientByEmail() {
      try {
        var response = await this.$axios.get(`/patients/email/${this.patientQuery}`);
        console.log(response.data);
        this.patientId = response.data.id;

        console.log(this.patientId, "ideeet");
      } catch (error) {
        console.error('Error finding patient email:', error);
      }
    },

  },
}
</script>

<style>
.fc .fc-header-toolbar {
  display: inline;
  justify-content: space-between;
  flex-wrap: wrap;
}
.fc {
  color: #206050;
}


.fc .fc-header-toolbar,
.fc .fc-daygrid-day-number,
.fc .fc-daygrid-day-top,
.fc .fc-toolbar-title,
.fc .fc-day {
  color: #206050;
}
.fc .fc-header-toolbar .fc-left,
.fc .fc-header-toolbar .fc-center,
.fc .fc-header-toolbar .fc-right {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  width: 100%;
}


.fc .fc-header-toolbar button {
  margin: 0 5px;
  flex: 1 1 100%;
  text-align: center;
  background-color: #206050;
}


@media (max-width: 768px) {
  .fc .fc-header-toolbar {
    flex-direction: column;
    align-items: center;
  }

  .fc .fc-header-toolbar .fc-left,
  .fc .fc-header-toolbar .fc-center,
  .fc .fc-header-toolbar .fc-right {
    width: 100%;
    justify-content: center;
    margin-bottom: 10px;
    background-color: #206050;
  }

  .fc .fc-header-toolbar button {
    width: 100%;
    margin: 5px 0;
  }
}
</style>
