<template>
  <LMap :zoom="13" :center="[57.7089, 11.9746]" style="height: 500px; width: 100%;">
    <LTileLayer :url="'https://tiles.stadiamaps.com/tiles/alidade_smooth_dark/{z}/{x}/{y}{r}.png'" />
    <LMarker :lat-lng="[47.413220, -1.219482]" />
    <l-control-scale position="topright" />
  </LMap>
</template>

<script>
import "leaflet/dist/leaflet.css";
import { LControlScale, LMap, LTileLayer } from "@vue-leaflet/vue-leaflet";

export default {
  components: {
    LMap,
    LTileLayer,
    LControlScale
  },
  data() {
    return {};
  },
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
</style>