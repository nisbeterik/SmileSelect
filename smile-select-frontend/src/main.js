import { createApp } from 'vue';
import App from './App.vue';
import axios from './axios';
import router from './router';
import { createPinia } from 'pinia'
import 'bootstrap/dist/css/bootstrap.min.css';
import mqtt from 'mqtt';

const app = createApp(App);
const pinia = createPinia()

app.use(pinia)
app.config.globalProperties.$axios = axios;
app.use(router);
app.mount('#app');
app.use(mqtt);