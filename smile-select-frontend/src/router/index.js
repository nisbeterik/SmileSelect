import { createRouter, createWebHistory } from "vue-router";
import LoginView from "@/views/LoginView.vue";
import HomePage from "@/views/HomePage.vue";
import MapPage from "@/views/MapPage.vue";
import CreateAppointment from "@/views/CreateAppointment.vue";
import DentistRegistrationForm from "@/views/DentistRegistrationForm.vue";
import PatientRegistrationForm from "@/views/PatientRegistrationForm.vue";

const routes = [
  { path: "/", name: "Home", component: HomePage },
  { path: "/login", name: "Login", component: LoginView },
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