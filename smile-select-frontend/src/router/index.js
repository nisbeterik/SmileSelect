// import LoginView from "@/views/LoginComponent.vue";
//import HomePage from "@/views/HomePage.vue";
import MapPage from "@/views/MapPage.vue";
import CreateAppointment from "@/views/CreateAppointment.vue";
import DentistRegistrationForm from "@/components/DentistRegistrationComponent.vue";
import PatientRegistrationForm from "@/components/PatientRegistrationComponent.vue";
import { createRouter, createWebHistory } from 'vue-router';
// import AuthPage from '@/views/AuthPage.vue';
import PatientDashboard from '@/views/PatientDashboard.vue';
import DentistDashboard from '@/views/DentistDashboard.vue';
import AuthPage from "@/views/AuthPage.vue";
import AvailabilityPage from "@/views/AvailabilityPage.vue";
import Home from "@/views/Home.vue";

const routes = [
  { path: "/", name: "Home", component: Home },
  { path: "/auth", name: "AuthPage", component: AuthPage },
  { path: "/available-slots", name: "AvailabilityPage", component: AvailabilityPage },
  { path: "/patient-dashboard", name: "PatientDashboard", component: PatientDashboard },
  { path: "/dentist-dashboard", name: "DentistDashboard", component: DentistDashboard },
  { path: "/patient-registration", name: "PatientRegistration", component: PatientRegistrationForm },
  { path: "/dentist-registration", name: "DentistRegistration", component: DentistRegistrationForm },
  { path: "/create-appointment", name: "CreateAppointment", component: CreateAppointment },
  { path: "/map", name: "Map", component: MapPage },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
