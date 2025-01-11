<template>
  <LMap
    ref="map"
    :zoom="currentZoom"
    :center="mapCenter"
    :options="mapOptions"
    style="height: 500px; width: 100%;"
  >
    <LTileLayer :url="'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png'" ></LTileLayer>

    <LMarker
      v-for="clinic in clinics"
      :key="clinic.id"
      :lat-lng="[clinic.latitude, clinic.longitude]"
      :visible="true"
      @click="handleMarkerClick(clinic)"
    >
      <LTooltip :offset="[0, -30]" :opacity="tooltipOpacity">
        <div class="tooltip-box">{{ clinic.name }} <br />{{ clinic.street }}, {{ clinic.houseNumber }}</div>
      </LTooltip>
    </LMarker>

    <l-control-scale position="topright" ></l-control-scale>
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
  props: {
    selectedClinicName: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      mapOptions: {
        maxBounds: [
          [57.65, 11.85],
          [57.76, 12.1],
        ],
        maxBoundsViscosity: 1.0,
        minZoom: 14,
      },
      clinics: [],
      tooltipOpacity: 0,
      mapCenter: [57.7069, 11.9746],
      currentZoom: 14,
    };
  },
  created() {
    this.fetchClinics();
  },
  watch: {
    selectedClinicName(newClinicName) {
      if (newClinicName) {
        this.updateMapCenter(newClinicName);
      }
    },
  },
  methods: {
    async fetchClinics() {
      try {
        const response = await axios.get("/dentists/clinics");
        this.clinics = response.data;
      } catch (error) {
        console.error("Error fetching clinics:", error);
      }
    },
    handleMarkerClick(clinic) {
      this.$emit('clinic-selected', clinic.id);
    },
    updateMapCenter(clinicName) {
      const clinic = this.clinics.find((c) => c.name === clinicName);
      if (clinic) {
        this.mapCenter = [clinic.latitude, clinic.longitude];
      }
    },
  },
};
</script>

<style>
.header-title {
  padding: 20px;
}
.header-content {
  justify-content: space-between;
  padding-top: 15px;
}
.button-secondary {
  max-width: 100px;
  margin-top: 0px;
}
.patient-dashboard {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
.top-row {
  display: flex;
  justify-content: space-between;
  gap: 20px;
}

.patient-current-appointment {
  flex: 1;
  width: 100%;
  margin: 20px;
  height: 750px;
}

.availability-page {
  margin-top: 20px;
}
.map-page {
  background-color: #206050;
  max-height: 600px;
  overflow: visible;
  margin: 20px;
  margin-bottom: 50px;
}
.not-auth{
  margin: auto;
}

.leaflet-control-attribution {
  display: none;
}
</style>
