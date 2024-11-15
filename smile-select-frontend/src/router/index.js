import { createRouter, createWebHistory } from "vue-router";
import LoginView from "@/views/LoginView.vue";
import HomePage from "@/views/HomePage.vue";
import RegistrationForm from "@/views/RegistrationForm.vue";
import MapPage from "@/views/MapPage.vue";
import CreateAppointment from "@/views/CreateAppointment.vue";

const routes = [
  { path: "/", name: "Home", component: HomePage },
  { path: "/login", name: "Login", component: LoginView },
  { path: "/registration", name: "Registration", component: RegistrationForm },
  { path: "/create-appointment", name: "CreateAppointment", component: CreateAppointment },
  { path: "/map", name: "Map", component: MapPage },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;