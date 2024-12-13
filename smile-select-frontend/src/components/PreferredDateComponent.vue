<template>
    <div class="glass-card glass-container">
        <h3>Select preferred appointment dates</h3>
        <p>You will be notified if an open appointment slot is published</p>
        <p>{{ infoText }}</p>

        <Datepicker
          v-model="selectedDates"
          :multi-dates="{ limit: 5 }"
          :enable-time-picker="false"
          :disabled-dates="disableDatesBeforeToday"
          @update:modelValue="handleDateSelection"
          class="custom-datepicker"
        />

        <button class="button-primary" @click="handleSelect" :disabled="selectedDates.length === 0">
            Save Dates
        </button>

        <p>Your current preferred dates (capped at 5)</p>
            <div class="glass-card" v-for="date in existingDates" :key="date.id">
              <div class="pref-date-flex">
                {{ dayjs(date.preferredDate).format('dddd') }} - {{ dayjs(date.preferredDate).format('YYYY-MM-DD') }}
                <button
                  class="align-right btn btn-danger btn-sm ms-2"
                  @click="this.deleteDate(date.id)"
                >
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
            infoText: ""
        };
    },
    mounted(){
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

                    // Process each selected date
                    for (const date of this.selectedDates) {
                        await this.$axios.post(`patients/preferred-dates`, {
                            patient: { id: this.patientId },
                            preferredDate: date
                        });
                    }

                    // Success message
                    this.infoText = `Successfully saved ${this.selectedDates.length} date(s)`;
                    this.loadExistingPreferredDates();
                    this.selectedDates = [];
                }
                catch (error) {
                    // Handle any errors that occur during the process
                    this.infoText = `Failed to save dates: ${error.response?.data || error.message}`;
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
                this.infoText = "Error loading existing preferred dates"
            }

        },

        async deleteDate(dateId){
            try {
                await this.$axios.delete(`patients/preferred-dates/${dateId}`);
                this.loadExistingPreferredDates();
            } catch (error) {
                this.infoText = "Error loading existing preferred dates"
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
.glass-container{
  min-height: 750px;
}
.glass-card {
  max-width: 500px;
  margin: 20px;
}
.pref-date-flex {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 18px;
}


.custom-datepicker input {
  width: 100%; /* Ensure the input field spans the entire width */
  box-sizing: border-box;
}


.custom-datepicker {
  display: block; /* Ensure the datepicker behaves like a block element */
  margin: 10px 0;
  width: 100%; /* Ensure the calendar has a minimum width */
}


.custom-datepicker.datepicker {
  width: Auto; /* Ensure the calendar dropdown matches the input field width */
  min-width: 200px; /* Set a minimum width for smaller containers */
}



</style>