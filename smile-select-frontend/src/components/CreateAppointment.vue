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

        <div>
          <label for="startTime">Start:</label>
          <input
            type="time"
            v-model="selectedSlot.startTime"
            step="300"
            required
          />
        </div>

        <div>
          <label for="endTime">End:</label>
          <input
            type="time"
            v-model="selectedSlot.endTime"
            step="300"
            required
          />
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
        events: [],
        selectable: true,
        selectHelper: true,
        select: this.handleSelect,
        slotMinTime: '07:00:00', // Week view starts at time
        slotMaxTime: '19:00:00', // // Week view ends at time
        slotDuration: '00:15:00', // Duration between slots
        snapDuration: '00:05:00', // Click-and-drag snap duration
        headerToolbar: {
          left: 'prev,next today',
          center: 'title',
          right: 'prev,next,dayGridMonth,timeGridWeek',
        },
      },
      showModal: false,
      selectedSlot: {
        startTime: '',
        endTime: '',
        date: null,
      },
    };
  },
  methods: {
    handleSelect(info) {
      // Necessary to know if user is currently in week or month view
      const calendarApi = this.$refs.calendar.getApi();
      const isWeekView = calendarApi.view.type === 'timeGridWeek';

      const formatTime = (date) => date.toTimeString().slice(0, 5);

      // Format the date as "<Weekday> 14-11-2024"
      const formattedDate = info.start.toLocaleDateString('en-GB', {
        weekday: 'long',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
      });

      this.selectedSlot.date = formattedDate;

      // If user is in week view, no default time is set
      if (isWeekView) {
        this.selectedSlot.startTime = formatTime(info.start);
        this.selectedSlot.endTime = formatTime(info.end);
        // If user is in month view, a default time is set
      } else {
        this.selectedSlot.startTime = '08:00';
        this.selectedSlot.endTime = '10:00';
      }

      this.showModal = true;
    },

    // Closes modal and resets the selected slot
    cancelAppointment() {
      this.showModal = false;
      this.selectedSlot = { startTime: '', endTime: '', date: null };
    },

    saveAppointment() {
      console.log(
        `Appointment saved for ${this.selectedSlot.date} from ${this.selectedSlot.startTime} to ${this.selectedSlot.endTime}`
      );

      // ADD LOGIC HERE FOR PERSISTENCE

      // Pushes new appointment event
      this.calendarOptions.events.push({
        title: 'New Appointment',
        start: `${this.selectedSlot.date}T${this.selectedSlot.startTime}`,
        end: `${this.selectedSlot.date}T${this.selectedSlot.endTime}`,
      });

      this.cancelAppointment();
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

button {
  margin-top: 10px;
}
</style>
