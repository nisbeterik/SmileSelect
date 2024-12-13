<template>
  <LMap
      :zoom="13"
      :center="[57.7089, 11.9746]"
      :options="mapOptions"
      style="height: 1080px; width: 100%;"
  >
    <LTileLayer :url="'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'" />
    <LMarker
        v-for="clinic in clinics"
        :key="clinic.id"
        :lat-lng="[clinic.latitude, clinic.longitude]"
    />

    <l-control-scale position="topright" />
  </LMap>
</template>

<script>
import "leaflet/dist/leaflet.css";
import { LControlScale, LMap, LTileLayer, LMarker } from "@vue-leaflet/vue-leaflet";
import axios from "@/axios";

export default {
  components: {
    LMap,
    LTileLayer,
    LControlScale,
    LMarker,
  },
  data() {
    return {
      mapOptions: {
        maxBounds: [
          [57.65, 11.85],
          [57.75, 12.05],
        ],
        maxBoundsViscosity: 1.0,
        minZoom: 12,
      },
      clinics: []
    };
  },
  created() {
    this.fetchClinics();
  },
  methods: {
    async fetchClinics() {
      try {

        const response = await axios.get('/dentists/clinics');
        this.clinics = response.data;
      } catch (error) {
        console.error("Error fetching clinics:", error);
      }
    }
  }
};
</script>

<style>
body, html, #app {
  margin: 0;
  padding: 0;
  height: 100%;
}

.l-map-container {
  height: 100vh;
  width: 100vw;
}


.leaflet-control-attribution {
  display: none !important;
}
</style>
