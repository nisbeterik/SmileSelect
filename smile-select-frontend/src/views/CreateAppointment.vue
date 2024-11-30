<template>
  <div>
    <FullCalendar
      ref="calendar"
      :options="calendarOptions"
      @select="handleSelect"
    />
    <div
      v-if="showCreateAppointmentModal"
      class="modal"
      @click.self="closeCurrentModal()"
    >
      <div class="modal-content">
        <h3>Create Appointment</h3>

        <div v-if="selectedSlot.date">
          <strong>{{ selectedSlot.date }}</strong>
        </div>

        <div class="time-input-group">
          <label for="startTime">Start:</label>
          <div class="time-controls">
            <input
              type="time"
              v-model="selectedSlot.startTime"
              step="300"
              required
            />
            <button class="time-btn" @click="adjustTime('start', -5)">-</button>
            <button class="time-btn" @click="adjustTime('start', 5)">+</button>
          </div>
        </div>

        <div class="time-input-group">
          <label for="endTime">End:</label>
          <div class="time-controls">
            <input
              type="time"
              v-model="selectedSlot.endTime"
              step="300"
              required
            />
            <button class="time-btn" @click="adjustTime('end', -5)">-</button>
            <button class="time-btn" @click="adjustTime('end', 5)">+</button>
          </div>
        </div>

        <div class="patient-group">
          <label for="assign-patient">Patient</label>
          <div class="time-controls">
            <input
              type="text-field"
              v-model="patientQuery"
            />
            <button class="patient-check" @click="findPatientByEmail()"> check </button>
          </div>
        </div>

        <button @click="saveAppointment()">Save Appointment</button>
        <button @click="closeCurrentModal()">Cancel</button>
      </div>
    </div>

    <div
      v-if="showAppointmentDetailsModal"
      class="modal"
      @click.self="closeCurrentModal"
    >
      <div class="modal-content">
        <h3>Appointment Details:</h3>
        <p>
          <strong>Status:</strong><br />
          {{ selectedEvent.status }}
        </p>
        <p>
          <strong>Date:</strong><br />
          {{ selectedEvent.date }}
        </p>
        <p>
          <strong>Time:</strong><br />
          {{ selectedEvent.time }}
        </p>
        <div
          v-if="selectedEvent.patientId && selectedEvent.patientId !== 'null'"
        >
          <p>
            <strong>Patient Name:</strong><br />
            {{ selectedEvent.patientName }}
            <button @click="removePatientFromAppointment">Remove</button>
          </p>
          <p>
            <strong>Patient Email:</strong><br />
            {{ selectedEvent.patientEmail }}
          </p>
          
        </div>
        <div v-else>
          <div class="patient-group">
          <label for="assign-patient">Patient</label>
          <div class="time-controls">
            <input
              type="text-field"
              v-model="patientQuery"
            />
            <button class="patient-check" @click="findPatientByEmail()"> check </button>
          </div>
        </div>
        </div>
        <button @click="closeCurrentModal">Close</button>
        <button @click="deleteAppointment">Delete</button>
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

const bookedColor = '#FF5733';
const availableColor = '#28A745';
const selectedColor = '#C6B700';

export default {
  components: {
    FullCalendar,
  },
  mounted() {
    this.loadAppointments();
    this.intervalId = setInterval(this.loadAppointments, 10000); // Update exisiting appointments every 10 seconds
  },
  beforeUnmount() {
    clearInterval(this.intervalId); // Clear appointment reload interval once component is unmounted
  },
  data() {
    return {
      multiSlotMode: false, // Tracks if multi-slot mode is active    
      selectedSlots: [],
      selectedEventId: null,
      calendarOptions: {
        plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin, listPlugin],
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
          left: 'prev,next today,toggleWeekends multiSlotMode,createTimeSlots',
          center: 'title',
          right: 'prev,next,dayGridMonth,timeGridWeek,listWeek',
        },
        customButtons: {
          toggleWeekends: {
            text: 'Toggle Weekends',
            click: this.toggleWeekends,
          },
          multiSlotMode: {
            text: 'MultiSlotMode',
            click: this.toggleMultiSlotMode,
          },
          createTimeSlots: {
            text: 'Create Time Slots',
            click: this.createMultipleTimeSlots
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
      HARDCODED_DENTIST_ID: 123, // REMOVE ME LATER
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
    toggleWeekends() {
      const weekendsStatus = this.calendarOptions.weekends;
      if (weekendsStatus === true) {
        this.calendarOptions.weekends = false;
      } else {
        this.calendarOptions.weekends = true;
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

    createMultiSlotModeTempSlot(){
      var index = -1;
      
      this.selectedSlots.forEach(slot => {
      var overlap = false; 
      
      if((index * -1) === this.selectedSlots.length) { //checks if the latest slot overlaps
        overlap = this.checkOverlap(slot);
        
      } 
      if (overlap){
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
      this.selectedSlot = { startTime: '', endTime: '', date: null };
    },

    async saveAppointment(slotData = null) {
      let slot;
      if(slotData === null) {
        slot = { ...this.selectedSlot };
      } else{
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
          dentistId: `${this.HARDCODED_DENTIST_ID}`,
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
        var response = await this.$axios.get(`/appointments/dentist/${this.HARDCODED_DENTIST_ID}`); // PLACEHOLDER ID
        var existingAppointments = response.data;

        if (this.selectedSlots) { this.createMultiSlotModeTempSlot() }
        

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
    async deleteAppointment(){
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
    async findPatientByEmail(){
      try {
          var response = await this.$axios.get(`/patients/email/${this.patientQuery}`);
          console.log(response.data);
          this.patientId = response.data.id;

          console.log(this.patientId, "ideeet");


    } catch (error) {
          console.error('Error finding patient email patients:', error);
      }
    },
    async removePatientFromAppointment() {
      const patientId = this.selectedEvent.id
      if (patientId) {
        const response = await this.$axios.patch(`/appointments`, 
          {
            "id": this.selectedEvent.id,
            "patientId": null
          })
        console.log(response)
        this.selectedEvent.patientId = null; 
     }
    },
  }
};
</script>

<style scoped>
:root {
  --selected-event-color: #333333; /* Default color for the selected event */
}
.active-button {
  background-color: #007bff; /* Highlight color */
  color: white;
  border-color: #0056b3;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
}
.selected-event {
  background-color: var(--selected-event-color) !important;
  border-color: var(--selected-event-color) !important;
  filter: brightness(70%)
}

.time-input-group {
  margin: 10px 0;
}

.time-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.time-btn {
  width: 30px;
  height: 30px;
  border-radius: 4px;
  border: 1px solid #ccc;
  background-color: #f5f5f5;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
}

.time-btn:hover {
  background-color: #e0e0e0;
}

button {
  margin-top: 10px;
}
</style>
