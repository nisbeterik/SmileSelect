<template>
  <div class="glass-card glass-container">
    <h3>Select preferred appointment dates</h3>
    <p>You will be notified if an open appointment slot is published</p>
    <p :class="infoTextClass">{{ infoText }}</p>

    <Datepicker v-model="selectedDates"
                :multi-dates="{ limit: 5 }"
                :enable-time-picker="false"
                :disabled-dates="disableDatesBeforeToday"
                @update:modelValue="handleDateSelection"
                class="custom-datepicker" ></Datepicker>

    <button class="button-primary" @click="handleSelect" :disabled="selectedDates.length === 0">
      Save Dates
    </button>

    <p>Your current preferred dates (max 5)</p>
    <div class="glass-card" v-for="date in existingDates" :key="date.id">
      <div class="pref-date-flex">
        {{ dayjs(date.preferredDate).format('dddd') }} - {{ dayjs(date.preferredDate).format('YYYY-MM-DD') }}
        <button class="align-right btn btn-danger btn-sm ms-2" @click="deleteDate(date.id)">
          Delete
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';
import Datepicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import '/src/CSS/global.css';
import dayjs from 'dayjs';

export default {
  name: 'PreferredDateComponent',
  components: {
    Datepicker,
  },
  data() {
    const authStore = useAuthStore();
    return {
      token: authStore.token,
      patientId: authStore.id,
      selectedDates: [],
      existingDates: [],
      infoText: "",
      infoTextClass: ""
    };
  },
  mounted() {
    this.loadExistingPreferredDates();
  },
  methods: {
    dayjs,

    // Handle selection within the calendar
    handleDateSelection(dates) {
      this.selectedDates = Array.isArray(dates) ? dates : [];
    },

    // Handle the "Select" button click
    async handleSelect() {
      if (this.selectedDates.length > 0) {
        try {
          // Clear previous info text
          this.infoText = "";
          this.infoTextClass = "";

          // Process each selected date
          for (const date of this.selectedDates) {
            await this.$axios.post(`patients/preferred-dates`, {
              patient: { id: this.patientId },
              preferredDate: date
            });
          }

          // Success message
          this.infoText = `Successfully saved ${this.selectedDates.length} date(s)`;
          this.infoTextClass = "success-text";
          this.loadExistingPreferredDates();
          this.selectedDates = [];
        }
        catch (error) {
          // Handle any errors that occur during the process
          this.infoText = `Failed to save dates: ${error.response?.data || error.message}`;
          this.infoTextClass = "error-text"; 
          this.loadExistingPreferredDates();
        }
      }
    },

    // Disable dates before today
    disableDatesBeforeToday(date) {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      return date < today;
    },

    // Loads any exisiting preferred dates
    async loadExistingPreferredDates() {
      try {
        const response = await this.$axios.get(`patients/${this.patientId}/preferred-dates`);
        this.existingDates = response.data;
      } catch (error) {
        this.infoText = "Error loading existing preferred dates";
        this.infoTextClass = "error-text"; 
      }

    },

    // Delete a preferred date
    async deleteDate(dateId) {
      try {
        await this.$axios.delete(`patients/preferred-dates/${dateId}`);

        // After deletion, reload the preferred dates and show a success message
        await this.loadExistingPreferredDates();
        this.infoText = "Successfully deleted the preferred date";
        this.infoTextClass = "success-text"; 
      } catch (error) {
        // Handle the error and update infoText
        this.infoText = "Error deleting preferred date";
        this.infoTextClass = "error-text";
      }
    }
  },
};
</script>

<style scoped>
.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
  line-height: 1.5;
}

.ms-2 {
  margin-left: 0.5rem;
}

.mt-2 {
  margin-top: 0.5rem;
}

.align-right {
  margin-left: auto;
}

.glass-container {
  min-height: 750px;
}

.glass-card {
  margin: 20px;
}

.pref-date-flex {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 18px;
}

.custom-datepicker input {
  width: 100%;
  box-sizing: border-box;
}

.custom-datepicker {
  display: block;
  margin: 10px 0;
  width: 100%;
}


.custom-datepicker.datepicker {
  width: 100%;
  min-width: 200px;
}

@media (max-width: 1150px) {
  .glass-container{
    width: 90vw;
    min-height: auto;
  }
}
@media (max-width: 700px) {
  .pref-date-flex{
    font-size: 12px;
  }
}

/* Dynamic text color classes */
.success-text {
  color: green;
}

.error-text {
  color: darkred;
}
</style>
