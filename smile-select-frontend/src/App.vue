<template>
  <div id="app">
    <img alt="Vue logo" src="./assets/logo.png">
    <HelloWorld msg="Welcome to Your Vue.js App"/>
    
    <!-- Include the HelloComponent -->
    <HelloComponent />

    <!-- Add Check Backend button and status display -->
    <div>
      <button @click="checkBackend">Check Backend</button>
      <p v-if="backendStatus">Backend Status: {{ backendStatus }}</p>
    </div>
  </div>
</template>

<script>
import HelloWorld from './components/HelloWorld.vue'
import HelloComponent from './components/HelloComponent.vue'
import axios from 'axios'

export default {
  name: 'App',
  components: {
    HelloWorld,
    HelloComponent
  },
  data() {
    return {
      backendStatus: null,
    };
  },
  methods: {
    async checkBackend() {
      try {
        const response = await axios.get('http://localhost:8080/actuator/health');
        this.backendStatus = response.data.status;
      } catch (error) {
        console.error('Error connecting to the backend:', error);
        this.backendStatus = "Failed to connect to backend.";
      }
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  margin-top: 60px;
}
</style>
