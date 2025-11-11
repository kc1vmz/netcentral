<script setup>
import "leaflet/dist/leaflet.css"; // Import Leaflet CSS
import { LMap, LMarker, LCircleMarker, LPopup, LTileLayer } from "@vue-leaflet/vue-leaflet";
import * as L from 'leaflet';
import { selectedNet } from "@/SelectedNet.js";
import { selectedCallsign, updateSelectedCallsign } from "@/SelectedCallsign.js";
import { ref, watch, reactive, computed  } from 'vue';
import { configShowInfrastructure, configShowObjects, configShowTrackedStationsOnly, configShowPriorityObjects, updateShowInfrastructure, updateShowObjects, updateShowTrackedStationsOnly, updateShowPriorityObjects } from "@/ConfigurationDisplay.js";
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
const mapPoints = reactive({});
const center = ref([]);
const maxBounds = ref([]);
const zoom = ref(13);
const localConfigShowInfrastructure = reactive({value : null });
const localConfigShowTrackedStationsOnly = reactive({value : null });
const localConfigShowObjects = reactive({value : null });
const localConfigShowPriorityObjects = reactive({value : null });
const fetchUrl = ref({ value : buildNetCentralUrl('/summaries/netMapPoints/')});
var callsign = ref('');
const description = reactive({ value : null });
const voiceFrequency = reactive({ value : null });
const type = reactive({ value : null });
const dialogCreateObjectShow = reactive({ value : false });
const dialogCreateObject = ref(null);
const latlng = reactive({ });
const mapRef = ref(null);
const errorMessage = reactive({ value : null });

function updateLocalSelectedNet(newObject) {
  localSelectedNet.ncSelectedNet = newObject.ncSelectedNet;
}
function updateLocalConfigShowTrackedStationsOnly(newVal) {
  localConfigShowTrackedStationsOnly.value = newVal.value;
}
function updateLocalConfigShowInfrastructure(newVal) {
  localConfigShowInfrastructure.value = newVal.value;
}
function updateLocalConfigShowObjects(newVal) {
  localConfigShowObjects.value = newVal.value;
}
function updateLocalConfigShowPriorityObjects(newVal) {
  localConfigShowPriorityObjects.value = newVal.value;
}

// Watch for changes in the selected object ref
watch(selectedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
});
watch(configShowInfrastructure, (newVal, oldVal) => {
  updateLocalConfigShowInfrastructure(newVal);
});
watch(configShowTrackedStationsOnly, (newVal, oldVal) => {
  updateLocalConfigShowTrackedStationsOnly(newVal);
});
watch(configShowObjects, (newVal, oldVal) => {
  updateLocalConfigShowObjects(newVal);
});
watch(configShowPriorityObjects, (newVal, oldVal) => {
  updateLocalConfigShowPriorityObjects(newVal);
});

watch(localConfigShowInfrastructure, (newVal, oldVal) => {
  getMapItems();
});
watch(localConfigShowTrackedStationsOnly, (newVal, oldVal) => {
  getMapItems();
});
watch(localConfigShowObjects, (newVal, oldVal) => {
  getMapItems();
});
watch(localConfigShowPriorityObjects, (newVal, oldVal) => {
  getMapItems();
});

function buildUrl() {
  fetchUrl.value = buildNetCentralUrl('/summaries/netMapPoints/' +localSelectedNet.ncSelectedNet.callsign);
  var added = false;
  if (localConfigShowTrackedStationsOnly.value) {
      fetchUrl.value = fetchUrl.value + "?includeTrackedStations=true";
      added = true;
  }
  if (localConfigShowInfrastructure.value) {
    if (added) {
      fetchUrl.value = fetchUrl.value + "&";
    } else {
      fetchUrl.value = fetchUrl.value + "?";
    }
      fetchUrl.value = fetchUrl.value + "includeInfrastructure=true";
    added = true;
  }
  if (localConfigShowObjects.value) {
    if (added) {
      fetchUrl.value = fetchUrl.value + "&";
    } else {
      fetchUrl.value = fetchUrl.value + "?";
    }
    fetchUrl.value = fetchUrl.value + "includeObjects=true";
    added = true;
  }
  if (localConfigShowPriorityObjects.value) {
    if (added) {
      fetchUrl.value = fetchUrl.value + "&";
    } else {
      fetchUrl.value = fetchUrl.value + "?";
    }
    fetchUrl.value = fetchUrl.value + "includePriorityObjects=true";
    added = true;
  }
}

function isReady() {
  if ((mapRef.value) && (mapRef.value.leafletObject)) {
     mapRef.value.leafletObject.fitBounds(maxBounds.value);                                             
  }
}

function getMapItems() {
    if ((localSelectedNet.ncSelectedNet.callsign != null) && (localSelectedNet.ncSelectedNet.type == null)) {
      buildUrl();

      // dont do this for scheduled
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      fetch(fetchUrl.value, requestOptions)
        .then(response => response.json())
        .then(data => {
              if ((data != null) && (data.items != null)) {
                data.items.forEach( function(objectItem){
                  if (objectItem.infrastructure) {
                    objectItem.color = "green"
                  } else if (objectItem.object) {
                    if (objectItem.itemObject.type == 'SHELTER') {
                      objectItem.color = "orange"
                    } else if (objectItem.itemObject.type == 'EOC') {
                      objectItem.color = "black"
                    } else if (objectItem.itemObject.type == 'MEDICAL') {
                      objectItem.color = "red"
                    } else {
                      objectItem.color = "purple"
                    }
                  } else {
                    objectItem.color = "blue"
                  }
                });
              }

            mapPoints.value = data;
            center.value = [ mapPoints.value.centerLatitude, mapPoints.value.centerLongitude ];
            maxBounds.value = [[mapPoints.value.minLatitude, mapPoints.value.maxLongitude],[mapPoints.value.maxLatitude,  mapPoints.value.minLongitude]];

            if ((mapRef.value != null) && (mapRef.value.leafletObject != null)) {
              mapRef.value.leafletObject.fitBounds(maxBounds.value);                                             
            }
        })
        .catch(error => { console.error('Error getting net map points from server:', error); })
    }
}

watch(
  localSelectedNet,
  async () => {
    getMapItems();
  },
  { immediate: true }
)

function onMapClick(e) {
  updateSelectedCallsign(this.options.callsign);
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
          // update the map possibly
          getMapItems();
        }
        return response.json()
      })
      .then(data => {
      })
      .catch(error => { console.error('Error creating APRS object:', error); })
  }
}

function createObjectNo() {
    dialogCreateObjectShow.value = false;
}
</script>

<template>
    <!-- dialog -->
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

  <div class="grid-container" v-if="((localSelectedNet.ncSelectedNet.type == null) && (mapPoints != null) && (mapPoints.value != null) )">
        <div class="grid-item">
            <l-map style="flex:1;" ref="mapRef" @ready="isReady" v-bind:zoom="zoom" :center="center" :useGlobalLeaflet="false" v-on:click.right.native="onMapClickAdd">
              <l-tile-layer url="https://tile.openstreetmap.org/{z}/{x}/{y}.png"
                            layer-type="base"
                            name="OpenStreetMap"></l-tile-layer>
                <!-- display all points -->
                <!-- <l-marker v-for="mapPoint in mapPoints.value.items" :lat-lng="[mapPoint.latitude, mapPoint.longitude]"></l-marker> -->

                <l-circle-marker v-for="mapPoint in mapPoints.value.items"  :lat-lng="[mapPoint.latitude, mapPoint.longitude]" 
                          :options="{ callsign: mapPoint.title }" :key="mapPoint.title" :radius=8  :color="mapPoint.color" :fillColor="mapPoint.color" :fillOpacity="0.7"
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
                      </div>
                      <div v-else-if="((mapPoint.itemObject != null) && (mapPoint.object) && (mapPoint.itemObject.type == 'MEDICAL'))">
                        <b>{{ mapPoint.itemObject.name }} - Medical</b>
                        <br><i>{{mapPoint.itemObject.description}}</i>
                      </div>
                      <div v-else-if="((mapPoint.itemObject != null) && (mapPoint.object))">
                        <b>{{ mapPoint.itemObject.callsignFrom }}</b>
                        <br><i>{{mapPoint.itemObject.comment}}</i>
                      </div>
                      <div v-else-if="((mapPoint.itemObject != null) && (mapPoint.infrastructure))">
                        <b>{{ mapPoint.itemObject.callsign }}</b>
                        <br>{{ mapPoint.itemObject.type }}
                      </div>
                      <div v-else-if="((mapPoint.itemObject != null) && (mapPoint.itemObject.startTime != null))">
                        <b>{{ mapPoint.itemObject.callsign }} ({{ mapPoint.itemObject.tacticalCallsign }})</b>
                        <br><i>Status: {{mapPoint.itemObject.status}}</i>
                      </div>
                      <div v-else-if="(mapPoint.itemObject != null)">
                        <b>{{ mapPoint.itemObject.name }}</b>
                        <br><i>{{mapPoint.itemObject.description}}</i>
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
    </div>
</template>

<style scoped>
html, body {
  margin: 0;
  padding: 0;
}

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
