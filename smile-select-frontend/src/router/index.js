import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '@/views/LoginView.vue';
import PatientDashboard from '@/views/PatientDashboard.vue';
import DentistDashboard from '@/views/DentistDashboard.vue';

const routes = [
  { path: '/login', name: 'Login', component: LoginView },
  {
    path: '/patient-dashboard',
    name: 'PatientDashboard',
    component: PatientDashboard,
  },
  {
    path: '/dentist-dashboard',
    name: 'DentistDashboard',
    component: DentistDashboard,
  },
  // Add other routes as needed
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
