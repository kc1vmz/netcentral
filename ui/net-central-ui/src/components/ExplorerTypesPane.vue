<script setup>
import { selectedObjectType , updateSelectedObjectType } from "@/SelectedObjectType.js";
import { selectedObject , updateSelectedObject } from "@/SelectedObject.js";
import { ref, watch, reactive, onMounted } from 'vue';

watch(selectedObjectType, (newValue, oldValue) => {
  localStorage.setItem('NetCentral-explorer-type', JSON.stringify(newValue))
  updateSelectedObject( null ) ;
});

onMounted(async () => {
  const explorerType = localStorage.getItem('NetCentral-explorer-type')
  if (explorerType) {
    var value = JSON.parse(explorerType)
    updateSelectedObjectType(value.value);
  } else {
    updateSelectedObjectType('STATION');
  }
  updateSelectedObject( null ) ;
});
</script>

<template>
  <div>
    <form>
    <div class="grid-container">
      <div class="grid-item">
      </div>
      <div class="grid-item">
        <label for="objectTypeField">APRS resource type:</label>
        <select name="objectTypeField" id="objectType" v-model="selectedObjectType.value" style="display: inline;">
          <div v-if="(selectedObjectType.value == 'BBS')">
            <option value="BBS" selected>Bulletin Boards</option>
          </div>
          <div v-else>
            <option value="BBS">Bulletin Boards</option>
          </div>
          <div v-if="(selectedObjectType.value == 'CALLSIGN')">
            <option value="CALLSIGN" selected>Callsigns</option>
          </div>
          <div v-else>
            <option value="CALLSIGN">Callsigns</option>
          </div>
          <div v-if="(selectedObjectType.value == 'DSTAR')">
            <option value="DSTAR" selected>D-Stars</option>
          </div>
          <div v-else>
            <option value="DSTAR">D-Stars</option>
          </div>
          <div v-if="(selectedObjectType.value == 'DIGIPEATER')">
            <option value="DIGIPEATER" selected>Digipeaters</option>
          </div>
          <div v-else>
            <option value="DIGIPEATER">Digipeaters</option>
          </div>
          <div v-if="(selectedObjectType.value == 'IGATE')">
            <option value="IGATE" selected>iGates</option>
          </div>
          <div v-else>
            <option value="IGATE">iGates</option>
          </div>
          <div v-if="(selectedObjectType.value == 'IGNORE')">
            <option value="IGNORE" selected>Ignored</option>
          </div>
          <div v-else>
            <option value="IGNORE">Ignored</option>
          </div>
          <div v-if="(selectedObjectType.value == 'IS')">
            <option value="IS" selected>Internet Servers</option>
          </div>
          <div v-else>
            <option value="IS">Internet Servers</option>
          </div>
          <div v-if="(selectedObjectType.value == 'MMDVM')">
            <option value="MMDVM" selected>MMDVM</option>
          </div>
          <div v-else>
            <option value="MMDVM">MMDVM</option>
          </div>
          <div v-if="(selectedObjectType.value == 'OBJECT')">
            <option value="OBJECT" selected>Objects</option>
          </div>
          <div v-else>
            <option value="OBJECT">Objects</option>
          </div>
          <div v-if="(selectedObjectType.value == 'PRIORITYOBJECT')">
            <option value="PRIORITYOBJECT" selected>Priority Objects</option>
          </div>
          <div v-else>
            <option value="PRIORITYOBJECT">Priority Objects</option>
          </div>
          <div v-if="(selectedObjectType.value == 'REPEATER')">
            <option value="REPEATER" selected>Repeaters</option>
          </div>
          <div v-else>
            <option value="REPEATER">Repeaters</option>
          </div>
          <div v-if="(selectedObjectType.value == 'STATION')">
            <option value="STATION" selected>Stations</option>
          </div>
          <div v-else>
            <option value="STATION">Stations</option>
          </div>
          <div v-if="(selectedObjectType.value == 'UNKNOWN')">
            <option value="UNKNOWN" selected>Unknowns</option>
          </div>
          <div v-else>
            <option value="UNKNOWN">Unknowns</option>
          </div>
          <div v-if="(selectedObjectType.value == 'WEATHER')">
            <option value="WEATHER" selected>Weather Stations</option>
          </div>
          <div v-else>
            <option value="WEATHER">Weather Stations</option>
          </div>
          <div v-if="(selectedObjectType.value == 'WINLINK_GATEWAY')">
            <option value="WINLINK_GATEWAY" selected>Winlink Gateways</option>
          </div>
          <div v-else>
            <option value="WINLINK_GATEWAY">Winlink Gateways</option>
          </div>
        </select>
      </div>
    </div>
    </form>
    <div class="line"><hr/></div>
  </div>

</template>

<style scoped>
.box {
  font-size: 1rem;
  font-weight: 400;

  border: 2px solid black;
  width: 100px;
  height: 30px;
  text-align: center;
  vertical-align: middle;
}
.grid-container {
  display: grid;
  grid-template-columns: 50% 50%;
  margin: 5px;
  gap: 5px;
}
</style>
