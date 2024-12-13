<template>
  <LMap
      :zoom="14"
      :center="[57.7069, 11.9746]"
      :options="mapOptions"
      style="height: 500px; width: 100%;"
      @zoomend="onZoomEnd"
  >
    <LTileLayer :url="'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'" />

    <LMarker
        v-for="clinic in clinics"
        :key="clinic.id"
        :lat-lng="[clinic.latitude, clinic.longitude]"
        :visible="true"
        @click="handleMarkerClick(clinic)"
    >
      <LTooltip :offset="[0, -30]" :opacity="tooltipOpacity">
        <div class="tooltip-box">{{ clinic.name }} <br> {{ clinic.street }}, {{ clinic.houseNumber }}</div>
      </LTooltip>
    </LMarker>

    <l-control-scale position="topright" />
  </LMap>
</template>

<script>
import "leaflet/dist/leaflet.css";
import { LControlScale, LMap, LTileLayer, LMarker, LTooltip } from "@vue-leaflet/vue-leaflet";
import axios from "@/axios";

export default {
  components: {
    LMap,
    LTileLayer,
    LControlScale,
    LMarker,
    LTooltip,
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
      currentZoom: 14,
      tooltipOpacity: 0,  // Default opacity to hide the tooltips
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
    },
    handleMarkerClick(clinic) {
      this.$router.push({ name: 'AvailabilityPage', params: { clinicId: clinic.id } });
    }
  },
};
</script>

<style scoped>
.tooltip-box {
  background-color: rgba(255, 255, 255, 0.8);
  padding: 5px;
  border-radius: 5px;
  box-shadow: 0 0 5px rgba(0, 0, 0, 0.2);
  font-weight: bold;
}
</style>