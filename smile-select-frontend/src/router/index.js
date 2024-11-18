import { createRouter, createWebHistory } from 'vue-router';
import AuthPage from '@/views/AuthPage.vue';
import PatientDashboard from '@/views/PatientDashboard.vue';
import DentistDashboard from '@/views/DentistDashboard.vue';

const routes = [
  {
    path: '/',
    redirect: '/auth',
  },
  {
    path: '/auth',
    name: 'AuthPage',
    component: AuthPage,
  },
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
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
