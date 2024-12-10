<template>
    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-12 col-md-8 col-lg-6">
                <h3>{{ appointmentText}}</h3>
                <div v-for="appointment in appointments" :key="appointment.id" class="card mb-3">
                    <div class="card-body">
                        <p><strong>Time:</strong> {{ formatDate(appointment.startTime) }}</p>
                        <p><strong>Duration:</strong> {{ formatDuration(appointment.startTime, appointment.endTime) }}
                        </p>
                        <p><strong>Dentist:</strong> {{ appointment.dentistName }}</p>
                        <p><strong>Clinic:</strong> {{ appointment.clinicName }}</p>
                        <p><strong>Address:</strong> {{ appointment.address }}</p>
                        <button @click="cancelAppointment(appointment.id)" class="btn btn-danger">Cancel</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { useAuthStore } from '@/stores/auth';
import { format, differenceInMinutes, parseISO } from 'date-fns';

export default {
    name: 'PatientCurrentAppointment',
    data() {
        const authStore = useAuthStore();
        return {
            token: authStore.token,
            patientId: authStore.id,
            appointments: [],
            appointmentText: "Your appointment(s)"
        };
    },
    methods: {
        async loadAppointmentData() {
            try {
                const response = await this.$axios.get(`/appointments/patient/${this.patientId}`);
                const appointments = response.data;

                // Fetch dentist details for each appointment and attach to the appointment
                for (const appointment of appointments) {
                    const dentistResponse = await this.$axios.get(`/dentists/${appointment.dentistId}`);
                    appointment.dentistName = `${dentistResponse.data.firstName} ${dentistResponse.data.lastName}`;
                    appointment.clinicName = `${dentistResponse.data.clinicName}`;
                    appointment.address = `${dentistResponse.data.street} ${dentistResponse.data.houseNumber}, ${dentistResponse.data.zip} ${dentistResponse.data.city}`;
                }

                this.appointments = appointments;
                if (this.appointments.length === 0){
                    this.appointmentText = "No appointment(s) scheduled"
                }
            } catch (error) {
                console.error('Error fetching appointments:', error);
                this.appointmentText = "No appointment(s) scheduled"
            }
        },

        formatDate(dateString) {
            const date = parseISO(dateString);
            return format(date, 'PPpp');
        },

        formatDuration(startTime, endTime) {
            const start = parseISO(startTime);
            const end = parseISO(endTime);

            // Calculate the duration in minutes
            const durationInMinutes = differenceInMinutes(end, start);

            // Calculate hours and minutes from the duration in minutes
            const hours = Math.floor(durationInMinutes / 60);
            const minutes = durationInMinutes % 60;

            // Format the duration nicely
            return `${hours} hr ${minutes} min`;
        },

        async cancelAppointment(appointmentId) {
            try {
                await this.$axios.patch(`/appointments/${appointmentId}/cancel`, {
                    headers: {
                        Authorization: `Bearer ${this.token}`,
                    },
                });

                this.loadAppointmentData();
            } catch (error) {
                console.error('Error cancelling appointment:', error);
            }
        },
    },
    mounted() {
        this.loadAppointmentData();
    },
};
</script>

<style scoped>
.container {
    max-width: 100%;
}

.card {
    border-radius: 10px;
    box-shadow: 0px 2px 10px rgba(0, 0, 0, 0.1);
}
</style>