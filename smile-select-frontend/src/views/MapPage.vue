<template>
  <LMap
      :zoom="12"
      :center="[57.7089, 11.9746]"
      :options="mapOptions"
      style="height: 1080px; width: 100%;"
  >
    <LTileLayer :url="'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'"/>

    <LMarker
        v-for="clinic in clinics"
        :key="clinic.id"
        :lat-lng="[clinic.latitude, clinic.longitude]"
        :visible="true"
    />

    <LMarker
        :lat-lng="[57.7075, 11.97]"
        :visible="true"
    />

    <LMarker
        :lat-lng="[57.683, 11.959]"
        :visible="true"
    />

    <l-control-scale position="topright"/>
  </LMap>
</template>

<script>
import "leaflet/dist/leaflet.css";
import {LControlScale, LMap, LTileLayer, LMarker} from "@vue-leaflet/vue-leaflet";
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
          [57.76, 12.1]
        ],
        maxBoundsViscosity: 1.0,
        minZoom: 12,
      },
      clinics: [],
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
  },
};
</script>
