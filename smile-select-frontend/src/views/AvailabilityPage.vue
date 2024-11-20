<template>
    <div class='availability-app'>
      <div class='availability-sidebar'>
        <div class='availability-sidebar-section'>
          <h2>Instructions</h2>
          <ul>
            <li>Select dates and you will be prompted to create a new event</li>
            <li>Drag, drop, and resize events</li>
            <li>Click an event to delete it</li>
          </ul>
        </div>
        <div class='availability-sidebar-section'>
          <h2>All Events ({{ currentEvents.length }})</h2>
          <ul>
            <li v-for='event in currentEvents' :key='event.id'>
              <b>{{ event.startStr }}</b>
              <i>{{ event.title }}</i>
            </li>
          </ul>
        </div>
      </div>
      <div class='availability-main'>
        <FullCalendar
          class='availability-calendar'
          :options='calendarOptions'
        >
          <template v-slot:eventContent='arg'>
            <b>{{ arg.timeText }}</b>
            <i>{{ arg.event.title }}</i>
          </template>
        </FullCalendar>
      </div>
    </div>
  </template>
  
  <script>
  import { defineComponent } from 'vue'
  import FullCalendar from '@fullcalendar/vue3'
  import dayGridPlugin from '@fullcalendar/daygrid'
  import timeGridPlugin from '@fullcalendar/timegrid'
  import interactionPlugin from '@fullcalendar/interaction'
  
  export default defineComponent({
    components: {
      FullCalendar,
    },

    mounted() {
        this.loadSlots();
    },
    data() {
      return {
        calendarOptions: {
          plugins: [
            dayGridPlugin,
            timeGridPlugin,
            interactionPlugin 
          ],
          headerToolbar: {
            left: 'prev, next, today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek'
          },
          initialView: 'dayGridMonth',
          events: [
  {
    id: '1',
    title: 'Meeting with Client',
    start: '2024-11-18T09:00:00',
    end: '2024-11-18T10:00:00'
  },
  {
    id: '2',
    title: 'Project Discussion',
    start: '2024-11-20T14:00:00',
    end: '2024-11-20T15:00:00'
  },
  {
    id: '3',
    title: 'Team Sync-Up',
    start: '2024-11-22T11:00:00',
    end: '2024-11-22T12:00:00'
  }
], 
          editable: true,
          selectable: true,
          selectMirror: true,
          dayMaxEvents: true,
          weekends: false,
          select: this.handleDateSelect,
          eventClick: this.handleEventClick,
          eventsSet: this.handleEvents
        },
        currentEvents: [],
      }
    },
    methods: {
      handleEventClick(clickInfo) {
        if (confirm(`Are you sure you want to book '${clickInfo.event.title}'`)) {
          let data = {
            id: clickInfo.event.id
          }
          this.bookAppointment(data);
        }
      },
      handleEvents(events) {
        this.currentEvents = events
      },
      loadSlots(){
        console.log('placeholder');
      },
      bookAppointment(slotInfo){
        console.log(slotInfo);
      }
    }
  })
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
  
  b { /* used for event dates/times */
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
  
  .fc { /* the calendar root */
    max-width: 1100px;
    margin: 0 auto;
  }
  </style>