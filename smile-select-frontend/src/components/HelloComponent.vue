<!-- src/components/HelloComponent.vue -->
<template>
    <div>
      <button @click="fetchGreeting">Get Greeting</button>
      <p v-if="greeting">{{ greeting }}</p>
    </div>
  </template>
  
  <script>
  import axios from "axios";
  
  export default {
    data() {
      return {
        greeting: null,
      };
    },
    methods: {
      async fetchGreeting() {
        try {
          const response = await axios.get("http://localhost:8080/api/hello?name=Vue");
          this.greeting = response.data;
        } catch (error) {
          console.error("Error fetching greeting:", error);
          this.greeting = "Failed to load greeting from backend.";
        }
      },
    },
  };
  </script>
  