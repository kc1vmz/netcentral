<script setup>
import "leaflet/dist/leaflet.css"; // Import Leaflet CSS
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser, registerPageShow } from "@/LoginInformation.js";
import { LMap, LMarker, LCircleMarker, LPopup, LTileLayer } from "@vue-leaflet/vue-leaflet";
import * as L from 'leaflet';
import { selectedObjectType } from "@/SelectedObjectType.js";
import { reactive, ref, watch, onMounted } from 'vue';
import { nudgeObject, nudge, nudgeAddObject, nudgeAdd, nudgeRemoveObject } from "@/nudgeObject.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateTrackedStationEvent, updateObjectEvent, updateCallsignEvent, updateAll, updateIgnoredEvent, updateIgnored, updateWeatherReportEvent, updateWeatherReport } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";

const { socket } = useSocketIO();
socket.on("updateTrackedStation", (data) => {
  updateTrackedStation(data)
});
socket.on("updateObject", (data) => {
  updateObject(data)
});
socket.on("updateAll", (data) => {
  updateAll(data)
});
socket.on("updateIgnored", (data) => {
  updateIgnored(data)
});
socket.on("updateWeatherReport", (data) => {
  updateWeatherReport(data)
});

function updateTrackedStation(data) {
    updateTrackedStationEvent.value = JSON.parse(data);
}
function updateObject(data) {
    updateObjectEvent.value = JSON.parse(data);
}

watch(updateWeatherReportEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if (localSelectedObjectType.value != "WEATHER") {
    return;
  }
  if (newValue.value.action == "Create") {
    updateMapObjects();
  }
});

watch(updateTrackedStationEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedObjectType.value == "OBJECT") || (localSelectedObjectType.value == "PRIORITYOBJECT") || (localSelectedObjectType.value == "CALLSIGN")) {
    return;
  }
  if (newValue.value.action == "Create") {
    updateMapObjects();
  } else if (newValue.value.action == "Delete") {
    updateMapObjects();
  } else if (newValue.value.action == "Update") {
  }
});

watch(updateObjectEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedObjectType.value != "OBJECT") && (localSelectedObjectType.value != "PRIORITYOBJECT")) {
    return;
  }
  if (newValue.value.action == "Create") {
    updateMapObjects();
  } else if (newValue.value.action == "Delete") {
    updateMapObjects();
  } else if (newValue.value.action == "Update") {
  }
});

watch(updateIgnoredEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if (localSelectedObjectType.value != "IGNORE") {
    return;
  }
  if (newValue.value.action == "Create") {
    updateMapObjects();
  } else if (newValue.value.action == "Delete") {
    updateMapObjects();
  } else if (newValue.value.action == "Update") {
  }
});


const localSelectedObjects = reactive({value : null});
const localSelectedObjectType = reactive({value : null});
const center = ref([]);
const zoom = ref(11);
const maxBounds = ref([]);
const mapPoints = reactive({});
var callsign = ref('');
const description = reactive({ value : null });
const voiceFrequency = reactive({ value : null });
const type = reactive({ value : null });
const dialogCreateObjectShow = reactive({ value : false });
const dialogCreateObject = ref(null);
const latlng = reactive({ });
const explorerMapRef = ref(null);
const errorMessage = reactive({ value : null });

onMounted(() => {
  const explorerType = localStorage.getItem('NetControl-explorer-type')
  if (explorerType) {
    var value = JSON.parse(explorerType)
    updateLocalSelectedObjectType(value.value);
  } else {
    updateLocalSelectedObjectType('STATION');
  }
});

function updateLocalSelectedObjects(newObjects) {
  localSelectedObjects.value = newObjects.value;
}
function updateLocalSelectedObjectType(newObjectType) {
  localSelectedObjectType.value = newObjectType;
}

watch(selectedObjectType, (newSelectedObjectType, oldSelectedObjectType) => {
  updateLocalSelectedObjectType(newSelectedObjectType.value);
});

function isReady() {
  if ((explorerMapRef.value) && (explorerMapRef.value.leafletObject)) {
     explorerMapRef.value.leafletObject.fitBounds(maxBounds.value);                                             
  }
}

function updateMapObjects() {
    if ((localSelectedObjectType.value != null) && (localSelectedObjectType.value != "CALLSIGN")) {
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      fetch(buildNetCentralUrl('/summaries/objectTypeMapPoints/'+localSelectedObjectType.value), requestOptions)
        .then(response => response.json())
        .then(data => {
            mapPoints.value = data;
            center.value = [ mapPoints.value.centerLatitude, mapPoints.value.centerLongitude ];
            maxBounds.value = [[mapPoints.value.minLatitude, mapPoints.value.maxLongitude],[mapPoints.value.maxLatitude,  mapPoints.value.minLongitude]];

            if ((explorerMapRef.value != null) && (explorerMapRef.value.leafletObject != null)) {
              explorerMapRef.value.leafletObject.fitBounds(maxBounds.value);                                             
            }
        })
        .catch(error => { console.error('Error getting object type map points from server:', error); })
    } else {
      mapPoints.value = null;
      center.value = null;
      maxBounds.value = null;
    }
}

watch(
  localSelectedObjectType,
  async () => {
    updateMapObjects();
  },
  { immediate: true }
)

function onMapClick(e) {
    nudge(this.options.callsign);
}

function onMapClickAdd(e) {
    type.value = '2';
    callsign.value = '';
    description.value = '';
    voiceFrequency.value = '';
    dialogCreateObjectShow.value = true;
    latlng.value = e.latlng;
    errorMessage.value = '';
}

var enableButtonRef = ref(false);
watch(callsign, (newValue, oldValue) => {
  if (newValue != '') {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});

watch(nudgeRemoveObject, (newNudgeRemoveObject, oldNudgeRemoveObject) => {
  if (newNudgeRemoveObject.value != null) {
    updateMapObjects();
  }
});

watch(nudgeAddObject, (newNudgeAddObject, oldNudgeAddObject) => {
  if (newNudgeAddObject.value != null) {
    // status and type
    if ( ((localSelectedObjectType.value == 'OBJECT') && ((newNudgeAddObject.value.type == '2'))) || 
         ((localSelectedObjectType.value == 'PRIORITYOBJECT') && ((newNudgeAddObject.value.type == '3') || (newNudgeAddObject.value.type == '4') || (newNudgeAddObject.value.type == '5')))) {
      updateMapObjects();
    }
  }
});

function convertDecimalToDDMMSSx(value, suffixValuesPosNeg) {
  return convertDecimalToFormat(value, suffixValuesPosNeg, false);
}

function convertDecimalToDDDMMSSx(value, suffixValuesPosNeg) {
  return convertDecimalToFormat(value, suffixValuesPosNeg, true);
}

function convertDecimalToFormat(value, suffixValuesPosNeg, isThree) {
  var absolute = Math.abs(value);
  var degrees = Math.floor(absolute);
  var diff = (absolute - degrees);
  var minutesNotTruncated = diff * 60;
  var minutes = Math.floor(minutesNotTruncated);
  var seconds = Math.floor((diff*3600) - Math.floor(((diff*3600))/60) * 60);
  seconds = Math.floor((seconds * 100) / 60);  // convert from seconds to APRS percent of seconds
  var suffix;

  var degreesStr = "";
  if ((degrees >= 0) && (degrees <=9)) {
    degreesStr = "0"+degrees;
  } else {
    degreesStr = ""+degrees;
  }
  if (isThree) {
    if (degreesStr.length == 2) {
      degreesStr = "0"+degreesStr;
    }
  }
  var minutesStr = "";
  if ((minutes >= 0) && (minutes <=9)) {
    minutesStr = "0"+minutes;
  } else {
    minutesStr = ""+minutes;
  }
  var secondsStr = "";
  if ((seconds >= 0) && (seconds <=9)) {
    secondsStr = "0"+seconds;
  } else {
    secondsStr = ""+seconds;
  }

  if (value < 0) {
    suffix = suffixValuesPosNeg.charAt(1);
  } else {
    suffix = suffixValuesPosNeg.charAt(0);
  }
  // %02d%02d.%02d%c or %03d%02d.%02d%c
  return degreesStr+minutesStr+"."+secondsStr+suffix;
}

function createObjectYes() {
  var url = buildNetCentralUrl('/APRSObjects/requests');
  var bodyObject = {
    type : type.value,
    callsign : callsign.value,
    description :  description.value,
    voiceFrequency : voiceFrequency.value,
    lat : convertDecimalToDDMMSSx(latlng.value.lat, "NS"),
    lon : convertDecimalToDDDMMSSx(latlng.value.lng, "EW"),
    up : true
  };
  var requestOptions = {
    method: "POST",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: JSON.stringify(bodyObject)
  };

  if ((callsign.value != null) && (latlng.value != null)) {
    fetch(url, requestOptions)
      .then(response => {
        if (response.status != 200) {
          errorMessage.value = "Error creating object - "+response.status;
        } else {
          dialogCreateObjectShow.value = false;
        }
        return response.json()
      })
      .then(data => {
        // status and type
          nudgeAdd(data)
      })
      .catch(error => { console.error('Error creating APRS object:', error); })
  }
}

function createObjectNo() {
    dialogCreateObjectShow.value = false;
}
</script>

<template>
    <!-- dialogs -->
    <div v-if="dialogCreateObjectShow.value">
      <teleport to="#modals">    
        <dialog :open="dialogCreateObjectShow.value" ref="dialogCreateObject" @close="dialogCreateObjectShow.value = false" class="topz">  
          <form v-if="dialogCreateObjectShow.value" method="dialog">
            <div class="pagesubheader">Create object</div>
            <div class="line"><hr/></div>
            Create a general APRS object or Net Central Priority Object.
            <br>
              <div>
                <label for="callsignField">Callsign:</label>
                <input type="text" id="callsignField" v-model="callsign" />
              </div>
              <div>
                <label for="descriptionField">Description:</label>
                <input type="text" id="descriptionField" v-model="description.value" />
              </div>
              <div>
                <label for="voiceFrequencyField">Voice Frequency:</label>
                <input type="text" id="voiceFrequencyField" v-model="voiceFrequency.value" />
              </div>
                <div>
                    <label for="objectTypeField">Object type:</label>
                    <select name="objectTypeField" id="type" v-model="type.value" style="display: inline;">
                      <div v-if="(type.value == '2')">
                        <option value="2" selected>Standard</option>
                      </div>
                      <div v-else>
                        <option value="2">Standard</option>
                      </div>
                      <div v-if="(type.value == '3')">
                        <option value="3" selected>Shelter</option>
                      </div>
                      <div v-else>
                        <option value="3">Shelter</option>
                      </div>
                      <div v-if="(type.value == '4')">
                        <option value="4" selected>Medical</option>
                      </div>
                      <div v-else>
                        <option value="4">Medical</option>
                      </div>
                      <div v-if="(type.value == '5')">
                        <option value="5" selected>Emergency Operations Center (EOC)</option>
                      </div>
                      <div v-else>
                        <option value="5">Emergency Operations Center (EOC)</option>
                      </div>
                    </select>
                </div>
              <div>
                <b>{{ errorMessage.value }}</b>
              </div>
              <br>
            <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Create</button>
            <button class="boxButton" v-if="enableButtonRef" v-on:click.native="createObjectYes">Create</button>
            <button class="boxButton" v-on:click.native="createObjectNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

    <!-- main page-->
    <div class="grid-container" v-if="((mapPoints != null) && (mapPoints.value != null) )">
        <div class="grid-item">
            <l-map style="flex:1;" ref="explorerMapRef" @ready="isReady" v-bind:zoom="zoom" :center="center" :useGlobalLeaflet="false" v-on:click.right.native="onMapClickAdd">
              <l-tile-layer url="https://tile.openstreetmap.org/{z}/{x}/{y}.png"
                            layer-type="base"
                            name="OpenStreetMap"></l-tile-layer>
                <!-- display all points -->
                <!-- <l-marker v-for="mapPoint in mapPoints.value.items" :lat-lng="[mapPoint.latitude, mapPoint.longitude]"></l-marker> -->
                  <l-circle-marker v-for="mapPoint in mapPoints.value.items"  :lat-lng="[mapPoint.latitude, mapPoint.longitude]" 
                            :options="{ callsign: mapPoint.title }" :key="mapPoint.title" :radius=8  color="blue" fillColor="blue" :fillOpacity="0.7"
                            v-on:click.native="onMapClick">
                    <l-popup>
                      <div v-if="((mapPoint.itemObject != null) && (mapPoint.object) && (mapPoint.itemObject.type == 'SHELTER'))">
                        <b>{{ mapPoint.itemObject.callsign }} - Shelter</b>
                        <br><i>{{mapPoint.itemObject.description}}</i>
                        <br>Status: {{ mapPoint.itemObject.status }}
                        <br>State: {{ mapPoint.itemObject.state }}
                      </div>
                      <div v-else-if="((mapPoint.itemObject != null) && (mapPoint.object) && (mapPoint.itemObject.type == 'EOC'))">
                        <b>{{ mapPoint.itemObject.callsign }} - EOC</b>
                        <br><i>{{mapPoint.itemObject.description}}</i>
                        <br>Status: {{ mapPoint.itemObject.status }}
                        <br>Mobilization level: {{ mapPoint.itemObject.level }}
                      </div>
                      <div v-else-if="((mapPoint.itemObject != null) && (mapPoint.object) && (mapPoint.itemObject.type == 'MEDICAL'))">
                        <b>{{ mapPoint.itemObject.callsign }}</b>
                        <br><i>{{mapPoint.itemObject.description}}</i>
                        <br>Type: {{ mapPoint.itemObject.type }}
                      </div>
                      <div v-else-if="((mapPoint.itemObject != null) && (mapPoint.object))">
                        <b>{{ mapPoint.itemObject.callsignFrom }}</b>
                        <br><i>{{mapPoint.itemObject.comment}}</i>
                      </div>
                      <div v-else-if="(mapPoint.itemObject != null)">
                        <b>{{ mapPoint.itemObject.name }}</b>
                        <br><i>{{mapPoint.itemObject.description}}</i>
                        <br>Last heard: {{mapPoint.itemObject.prettyLastHeard}}
                      </div>
                      <div v-else>
                        {{ mapPoint.title }}<br>{{ mapPoint.name }}
                      </div>
                    </l-popup>
                    </l-circle-marker>
            </l-map>
        </div>
      </div>
    <div v-else>
        <!-- show no map -->
        <br>
        <br>
        <br><i>No map available.</i>
    </div>
</template>

<style scoped>
.grid-container {
  display: grid;
  flex: 1;
  height: 100%;
  width: 100%;
  grid-template-columns: 100%;
  margin: 0px;
  gap: 0px;
}
.grid-item {
  flex: 1;
}
</style>
