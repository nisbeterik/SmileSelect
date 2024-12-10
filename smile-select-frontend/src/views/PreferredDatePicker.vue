<template>
    <div>
        <h5>Select preferred appointment dates</h5>
        <p>You will be notified if an open appointment slot is published</p>
        <p>{{ infoText }}</p>

        <Datepicker v-model="selectedDates" :multi-dates="{ limit: 5 }" :enable-time-picker="false"
            :disabled-dates="disableDatesBeforeToday" @update:modelValue="handleDateSelection" class="custom-datepicker" />

        <button @click="handleSelect" :disabled="selectedDates.length === 0">
            Save Dates
        </button>

        <p>Your current preferred dates (capped at 5)</p>
        <li v-for="date in existingDates" :key="date.id">
            {{ dayjs(date.preferredDate).format('dddd') }} - {{ dayjs(date.preferredDate).format('YYYY-MM-DD') }} <button class="btn btn-danger btn-sm ms-2" @click="this.deleteDate(date.id)">Delete</button>
        </li>
    </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';
import Datepicker from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';
import dayjs from 'dayjs';

export default {
    name: 'PreferredDatePicker',
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

/* Style the date picker input field */
.custom-datepicker input {
  width: 200px; /* Adjust the width of the input field */
  max-width: 100%; /* Ensure it doesn't stretch beyond its container */
}

/* Style the date picker dropdown calendar */
.custom-datepicker .datepicker {
  width: auto; /* Let the dropdown resize to fit the calendar content */
  max-width: 300px; /* Restrict the width of the calendar */
  min-width: 200px; /* Ensure the calendar has a minimum width */
}

/* Optional: Style the container to ensure proper alignment */
.custom-datepicker {
  display: inline-block;
  margin: 10px 0;
  width: auto;
}

</style>