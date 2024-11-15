<template>
  <div>
    <FullCalendar
      ref="calendar"
      :options="calendarOptions"
      @select="handleSelect"
    />

    <div v-if="showModal" class="modal" @click.self="cancelAppointment">
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

        <button @click="saveAppointment">Save Appointment</button>
        <button @click="cancelAppointment">Cancel</button>
      </div>
    </div>
  </div>
</template>

<script>
import FullCalendar from '@fullcalendar/vue3';
import dayGridPlugin from '@fullcalendar/daygrid';
import timeGridPlugin from '@fullcalendar/timegrid';
import interactionPlugin from '@fullcalendar/interaction';

export default {
  components: {
    FullCalendar,
  },
  data() {
    return {
      calendarOptions: {
        plugins: [dayGridPlugin, interactionPlugin, timeGridPlugin],
        initialView: 'timeGridWeek',
        weekends: true,
        eventOverlap: false,
        events: [],
        selectable: true,
        selectHelper: true,
        select: this.handleSelect,
        slotMinTime: '07:00:00',
        slotMaxTime: '19:00:00',
        slotDuration: '00:15:00',
        snapDuration: '00:05:00',
        headerToolbar: {
          left: 'prev,next today',
          center: 'title',
          right: 'prev,next,dayGridMonth,timeGridWeek',
        },
      },
      showModal: false,
      selectedSlot: {
        startTime: null,
        endTime: null,
        date: null,
      },
      HARDCODED_DENTIST_ID: 123, // REMOVE ME LATER
    };
  },
  methods: {
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

    handleSelect(info) {
      const calendarApi = this.$refs.calendar.getApi();
      const isWeekView = calendarApi.view.type === 'timeGridWeek';

      const formatTime = (date) => date.toTimeString().slice(0, 5);

      const formatDate = (date) => {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      };

      this.selectedSlot.date = formatDate(info.start);

      if (isWeekView) {
        this.selectedSlot.startTime = formatTime(info.start);
        this.selectedSlot.endTime = formatTime(info.end);
      } else {
        this.selectedSlot.startTime = '08:00';
        this.selectedSlot.endTime = '10:00';
      }

      // Check for overlapping events
      const overlaps = this.calendarOptions.events.some((event) => {
        const eventStart = new Date(event.start);
        const eventEnd = new Date(event.end);

        const selectedStart = new Date(info.start);
        const selectedEnd = new Date(info.end);

        return (
          (selectedStart >= eventStart && selectedStart < eventEnd) || 
          (selectedEnd > eventStart && selectedEnd <= eventEnd) || 
          (selectedStart <= eventStart && selectedEnd >= eventEnd)
        );
      });

      if (overlaps) {
        alert('This time slot overlaps with an existing appointment.');
        return;
      }

      this.showModal = true;
    },

    cancelAppointment() {
      this.showModal = false;
      this.selectedSlot = { startTime: '', endTime: '', date: null };
    },

    async saveAppointment() {
      try {
        const formatToLocalDateTime = (date, time) => {
          return `${date}T${time}:00`;
        };

        const startDateTime = formatToLocalDateTime(
          this.selectedSlot.date,
          this.selectedSlot.startTime
        );
        const endDateTime = formatToLocalDateTime(
          this.selectedSlot.date,
          this.selectedSlot.endTime
        );

        var newAppointment = {
          dentistId: `${this.HARDCODED_DENTIST_ID}`,
          startTime: `${startDateTime}`,
          endTime: `${endDateTime}`,
        };

        await this.$axios.post('/appointments', newAppointment);

        this.calendarOptions.events.push({
          title: 'Open Appointment',
          start: `${this.selectedSlot.date}T${this.selectedSlot.startTime}`,
          end: `${this.selectedSlot.date}T${this.selectedSlot.endTime}`,
        });
      } catch (error) {
        alert('Error saving appointment');
        console.error(
          'Error saving appointment:',
          error.response?.data || error.message
        );
      }

      this.cancelAppointment();
    },

    loadAppointments() {
      // TODO: ADD LOGIC HERE FOR LOADING EXISTING APPOINTMENTS FOR THE LOGGED IN DENTIST
    },
  },
};
</script>

<style scoped>
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
