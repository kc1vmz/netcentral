<!--
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
-->

<script setup>
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser, registerPageShow } from "@/LoginInformation.js";
import { selectedObjectType } from "@/SelectedObjectType.js";
import { updateSelectedObject } from "@/SelectedObject.js";
import { ref, watch, reactive, onMounted } from 'vue';
import { nudgeObject, nudge, nudgeUpdateObject, nudgeUpdate, nudgeRemoveObject, nudgeRemove, nudgeAddObject, nudgeAdd  } from "@/nudgeObject.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateTrackedStationEvent, updateObjectEvent, updateCallsignEvent, updateCallsign, updateAll, updateAllEvent, updateIgnored, updateIgnoredEvent } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";


const { socket } = useSocketIO();
socket.on("updateTrackedStation", (data) => {
  updateTrackedStation(data)
});
socket.on("updateObject", (data) => {
  updateObject(data)
});
socket.on("updateCallsign", (data) => {
  updateCallsign(data)
});
socket.on("updateIgnored", (data) => {
  updateIgnored(data)
});
socket.on("updateAll", (data) => {
  updateAll(data)
});

function updateTrackedStation(data) {
    updateTrackedStationEvent.value = JSON.parse(data);
}
function updateObject(data) {
    updateObjectEvent.value = JSON.parse(data);
}

const localSelectedObjectType = reactive({value : ""});
const trackedStations = reactive({value : null});
const internetServers = reactive({value : null});

const selectedItem = ref(null);

function populate() {
  trackedStations.value = null;
  selectedItem.value = null;
  const explorerType = localStorage.getItem('NetCentral-explorer-type')
  if (explorerType) {
    var value = JSON.parse(explorerType)
    updateLocalSelectedObjectType(value.value);
  } else {
    updateLocalSelectedObjectType('STATION');
  }
  updateSelectedObject( null ) ;
}

onMounted(async () => {
  populate();
});

watch(updateIgnoredEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if (localSelectedObjectType.value == "IGNORE") {
    if (newValue.value.action == "Create") {
        var found = false;
        if (trackedStations.value != null) {
          trackedStations.value.forEach(function(trackedStation){
            if ((!found) && (trackedStation.callsign == newValue.value.callsign)) {
              found = true;
            }
          });
        }
        if (!found) {
          if (trackedStations.value == null) {
            trackedStations.value = [];
          }
          newValue.value.object.id = newValue.value.object.callsign; // UI looking for callsign 
          trackedStations.value.push(newValue.value.object);
        }
    } else if (newValue.value.action == "Delete") {
      const indexToRemove = trackedStations.value.findIndex(obj => obj.callsign === newValue.value.callsign);
      if (indexToRemove !== -1) {
        if ((selectedItem.value != null) && (selectedItem.value.callsign === newValue.value.callsign)) {
          selectedItem.value = null;
          updateSelectedObject(null); 
        }
        trackedStations.value.splice(indexToRemove, 1);
      }
    }
  }
});


watch(updateTrackedStationEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedObjectType.value == "OBJECT") || (localSelectedObjectType.value == "PRIORITYOBJECT") || (localSelectedObjectType.value == "CALLSIGN")) {
    return;
  }
  if (newValue.value.object.type != localSelectedObjectType.value) {
    // not the right object for the selected object type
    return;
  }
  if (newValue.value.action == "Create") {
      var found = false;
      if (trackedStations.value != null) {
        trackedStations.value.forEach(function(trackedStation){
          if ((!found) && (trackedStation.callsign == newValue.value.callsign)) {
            found = true;
          }
        });
      }
      if (!found) {
        if (trackedStations.value == null) {
          trackedStations.value = [];
        }
        trackedStations.value.push(newValue.value.object);
      }
  } else if (newValue.value.action == "Delete") {
    const indexToRemove = trackedStations.value.findIndex(obj => obj.callsign === newValue.value.callsign);
    if (indexToRemove !== -1) {
      if ((selectedItem.value != null) && (selectedItem.value.callsign === newValue.value.callsign)) {
        selectedItem.value = null;
        updateSelectedObject(null); 
      }
      trackedStations.value.splice(indexToRemove, 1);
    }
  } else if (newValue.value.action == "Update") {
    if (trackedStations.value != null) {
      trackedStations.value.forEach(function(trackedStation){
        if (trackedStation.callsign == newValue.value.callsign) {
          trackedStation.name = newValue.value.object.name;
          trackedStation.status = newValue.value.object.status;
          trackedStation.lat = newValue.value.object.lat;
          trackedStation.lon = newValue.value.object.lon;
          trackedStation.radioStyle = newValue.value.object.radioStyle;
          trackedStation.transmitPower = newValue.value.object.transmitPower;
          trackedStation.prettyLastHeard = newValue.value.object.prettyLastHeard;
          trackedStation.type = newValue.value.object.type;
        }
      });
    }
  }
});


watch(updateObjectEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedObjectType.value != "OBJECT") && (localSelectedObjectType.value != "PRIORITYOBJECT")) {
    return;
  }
  if (((newValue.value.object.type == null) && (localSelectedObjectType.value == "PRIORITYOBJECT")) || 
      (newValue.value.object.type != null) && (localSelectedObjectType.value == "OBJECT")) {
        // not the right objects for the selected object type
        return;
      }
  if (newValue.value.action == "Create") {
      var found = false;
      if (trackedStations.value != null) {
        trackedStations.value.forEach(function(trackedStation){
          if ((!found) && (trackedStation.callsign == newValue.value.callsign)) {
            found = true;
          }
        });
      }

      if(!found) {
        newValue.value.object.name = newValue.value.object.callsignFrom;
        newValue.value.object.callsign = newValue.value.object.callsignFrom;
        newValue.value.object.prettyLastHeard = newValue.value.object.prettyLdtime;
        if (newValue.value.object.alive) {
          newValue.value.object.status = "Alive";
        } else {
          newValue.value.object.status = "Dead";
        }
        if (trackedStations.value == null) {
          trackedStations.value = [];
        }
        trackedStations.value.push(newValue.value.object);
      }
  } else if (newValue.value.action == "Delete") {
    const indexToRemove = trackedStations.value.findIndex(obj => obj.callsign === newValue.value.callsign);
    if (indexToRemove !== -1) {
      if ((selectedItem.value != null) && (selectedItem.value.callsign === newValue.value.callsign)) {
        selectedItem.value = null;
        updateSelectedObject(null); 
      }
      trackedStations.value.splice(indexToRemove, 1);
    }
  } else if (newValue.value.action == "Update") {
    if (trackedStations.value != null) {
      trackedStations.value.forEach(function(trackedStation){
        if (trackedStation.callsign == newValue.value.callsign) {
          trackedStation.name = newValue.value.object.name;
          trackedStation.status = newValue.value.object.status;
          trackedStation.lat = newValue.value.object.lat;
          trackedStation.lon = newValue.value.object.lon;
          trackedStation.radioStyle = newValue.value.object.radioStyle;
          trackedStation.transmitPower = newValue.value.object.transmitPower;
          trackedStation.prettyLastHeard = newValue.value.object.prettyLastHeard;
          trackedStation.type = newValue.value.object.type;
        }
      });
    }
  }
});

watch(updateCallsignEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if (localSelectedObjectType.value != "CALLSIGN"){
    return;
  }
  if (newValue.value.action == "Create") {
    var found = false;
      if (trackedStations.value != null) {
        trackedStations.value.forEach(function(trackedStation){
          if ((!found) && (trackedStation.callsign == newValue.value.callsign)) {
            found = true;
          }
        });
      }

    if (!found) {
      newValue.value.object.id = newValue.value.callsign;
      newValue.value.object.callsign = newValue.value.callsign;
      newValue.value.object.prettyLastHeard = "";
      newValue.value.object.status = "Heard";
      if (trackedStations.value == null) {
        trackedStations.value = [];
      }
      trackedStations.value.push(newValue.value.object);
    }
  } else if (newValue.value.action == "Delete") {
    const indexToRemove = trackedStations.value.findIndex(obj => obj.callsign === newValue.value.callsign);
    if (indexToRemove !== -1) {
      if ((selectedItem.value != null) && (selectedItem.value.callsign === newValue.value.callsign)) {
        selectedItem.value = null;
        updateSelectedObject(null); 
      }
      trackedStations.value.splice(indexToRemove, 1);
    }
  } else if (newValue.value.action == "Update") {
    if (trackedStations.value != null) {
      trackedStations.value.forEach(function(trackedStation){
        if (trackedStation.callsign == newValue.value.callsign) {
          trackedStation.name = newValue.value.object.name;
          trackedStation.country = newValue.value.object.country;
          trackedStation.state = newValue.value.object.state;
          trackedStation.license = newValue.value.object.license;
          trackedStation.id = newValue.value.callsign;
        }
      });
    }
  }
});

watch(updateAllEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  // reset the world
  populate();
});

const headers = [
        { text: "Callsign", value: "callsign", sortable: true },
        { text: "Name", value: "name", sortable: true},
        { text: "Status", value: "status", sortable: true},
        { text: "Lat", value: "lat", sortable: true},
        { text: "Lon", value: "lon", sortable: true},
        { text: "Radio Type", value: "radioStyle", sortable: true},
        { text: "Transmit Power", value: "transmitPower", sortable: true},
        { text: "Last Heard", value: "prettyLastHeard", sortable: true}];
const headersCallsign = [
        { text: "Callsign", value: "callsign", sortable: true },
        { text: "Name", value: "name", sortable: true},
        { text: "State", value: "state", sortable: true},
        { text: "Country", value: "country", sortable: true},
        { text: "License", value: "license", sortable: true}];
const headersPriority = [
        { text: "Callsign", value: "callsign", sortable: true },
        { text: "Name", value: "name", sortable: true},
        { text: "Type", value: "type", sortable: true},
        { text: "Status", value: "status", sortable: true},
        { text: "Lat", value: "lat", sortable: true},
        { text: "Lon", value: "lon", sortable: true},
        { text: "Last Heard", value: "prettyLastHeard", sortable: true}];
const headersIgnore = [
        { text: "Callsign", value: "callsign", sortable: true },
        { text: "Type", value: "type", sortable: true},
        { text: "Lat", value: "lat", sortable: true},
        { text: "Lon", value: "lon", sortable: true},
        { text: "Ignored since", value: "prettyLastHeard", sortable: true}];  // reusing prettyLastHeard
const headersInternetServer = [
        { text: "Server name", value: "name", sortable: true },
        { text: "IP address", value: "ipAddress", sortable: true },
        { text: "Login callsign", value: "loginCallsign", sortable: true},
        { text: "Query", value: "query", sortable: true}];

function showRow(item) {
    selectedItem.value = item;
    updateSelectedObject(item); 
}

function getBodyRowClass(item, rowNumber) {
    if ((selectedItem.value != null) && (selectedItem.value.callsign === item.callsign)) {
      return 'bold-row';
    }
    return '';
}


function updateLocalSelectedObjectType(newObject) {
  localSelectedObjectType.value = newObject;
}

// Watch for changes in the selected object ref
watch(selectedObjectType, (newSelectedObjectType, oldSelectedObjectType) => {
  updateLocalSelectedObjectType(newSelectedObjectType.value);
});

function updateObjects() {
    if ((localSelectedObjectType.value != null) && (localSelectedObjectType.value != '')) {
      let url = buildNetCentralUrl('/trackedStations');
      if (localSelectedObjectType.value == 'UNKNOWN') {
        url = buildNetCentralUrl('/trackedStations?type=UNKNOWN');
      } else if (localSelectedObjectType.value == 'STATION') {
        url = buildNetCentralUrl('/trackedStations?type=STATION')
      } else if (localSelectedObjectType.value == 'WEATHER') {
        url = buildNetCentralUrl('/trackedStations?type=WEATHER');
      } else if (localSelectedObjectType.value == 'DIGIPEATER') {
        url = buildNetCentralUrl('/trackedStations?type=DIGIPEATER');
      } else if (localSelectedObjectType.value == 'REPEATER') {
        url = buildNetCentralUrl('/trackedStations?type=REPEATER');
      } else if (localSelectedObjectType.value == 'BBS') {
        url = buildNetCentralUrl('/trackedStations?type=BBS');
      } else if (localSelectedObjectType.value == 'MMDVM') {
        url = buildNetCentralUrl('/trackedStations?type=MMDVM');
      } else if (localSelectedObjectType.value == 'OBJECT') {
        url = buildNetCentralUrl('/APRSObjects');
      } else if (localSelectedObjectType.value == 'PRIORITYOBJECT') {
        url = buildNetCentralUrl('/APRSObjects?priority=yes');
      } else if (localSelectedObjectType.value == 'IGATE') {
        url = buildNetCentralUrl('/trackedStations?type=IGATE');
      } else if (localSelectedObjectType.value == 'IS') {
        url = buildNetCentralUrl('/internetServers');
      } else if (localSelectedObjectType.value == 'WINLINK_GATEWAY') {
        url = buildNetCentralUrl('/trackedStations?type=WINLINK_GATEWAY');
      } else if (localSelectedObjectType.value == 'DSTAR') {
        url = buildNetCentralUrl('/trackedStations?type=DSTAR');
      } else if (localSelectedObjectType.value == 'CALLSIGN') {
        url = buildNetCentralUrl('/callsigns');
      } else if (localSelectedObjectType.value == 'IGNORE') {
        url = buildNetCentralUrl('/ignoreStations');
      }
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      fetch(url, requestOptions)
        .then(response => response.json())
        .then(data => {
            selectedItem.value = null;
            if ((localSelectedObjectType.value == 'OBJECT') || (localSelectedObjectType.value == 'PRIORITYOBJECT')) {
              var objects = data;
              if (objects != null) {
                objects.forEach(function(objectItem){
                                objectItem.name = objectItem.callsignFrom;
                                objectItem.callsign = objectItem.callsignFrom;
                                objectItem.prettyLastHeard = objectItem.prettyLdtime;
                                if (objectItem.alive) {
                                  objectItem.status = "Alive";
                                } else {
                                  objectItem.status = "Dead";
                                }
                            });
              }
              trackedStations.value = objects;
            } else if (localSelectedObjectType.value == 'CALLSIGN') {
              var objects = data;
              if (objects != null) {
                objects.forEach(function(objectItem){
                                objectItem.id = objectItem.callsign;
                                objectItem.prettyLastHeard = "";
                                objectItem.status = "Heard";
                            });
              }
              trackedStations.value = objects;
            } else if (localSelectedObjectType.value == 'IGNORE') {
              var objects = data;
              if (objects != null) {
                objects.forEach(function(objectItem){
                                objectItem.id = objectItem.callsign;
                            });
              }
              trackedStations.value = objects;
            } else if (localSelectedObjectType.value == 'IS') {
              var objects = data;
              internetServers.value = objects;
            } else {
              trackedStations.value = data;
            }
            updateSelectedObject( null );
        })
        .catch(error => { console.error('Error getting resources from server:', error); })
    }
}

watch(
  localSelectedObjectType,
  async () => {
      updateObjects();
  },
  { immediate: true }
)

watch(nudgeObject, (newNudgeObject, oldNudgeObject) => {
  if (newNudgeObject.value != null) {
    // find the localObject
    if (trackedStations.value != null) {
        var found = null;
        trackedStations.value.forEach(function(objectItem){
            if (objectItem.callsign == newNudgeObject.value) {
              if (found == null) {
                found = objectItem;
              }
            }
        });
        updateSelectedObject(found);
    }
    nudgeObject.value = null;
  }
});

watch(nudgeAddObject, (newNudgeAddObject, oldNudgeAddObject) => {
  if (newNudgeAddObject.value != null) {
    //  UNKNOWN, ITEM, STANDARD, SHELTER, MEDICAL, EOC, NET
    if ( ((localSelectedObjectType.value == 'OBJECT') && ((newNudgeAddObject.value.type == '2'))) || 
         ((localSelectedObjectType.value == 'PRIORITYOBJECT') && ((newNudgeAddObject.value.type == '3') || (newNudgeAddObject.value.type == '4') || (newNudgeAddObject.value.type == '5')))) {
      updateObjects();
    }
  }
});

watch(nudgeUpdateObject, (newNudgeUpdateObject, oldNudgeUpdateObject) => {
  if (newNudgeUpdateObject.value != null) {
    // find the localObject
    if (trackedStations.value != null) {
        var found = null;
        trackedStations.value.forEach(function(trackedStation){
            if (trackedStation.callsign == newNudgeUpdateObject.value.callsign) {
              if (found == null) {
                found = trackedStation;
              }
            }
        });
        if (found != null) {
          found.name = newNudgeUpdateObject.value.name;
          found.description = newNudgeUpdateObject.value.description;
          found.frequencyTx = newNudgeUpdateObject.value.frequencyTx;
          found.frequencyRx = newNudgeUpdateObject.value.frequencyRx;
          found.transmitPower = newNudgeUpdateObject.value.transmitPower;
          found.electricalPowerType = newNudgeUpdateObject.value.electricalPowerType;
          found.backupElectricalPowerType = newNudgeUpdateObject.value.backupElectricalPowerType;
          found.radioStyle = newNudgeUpdateObject.value.radioStyle;
          found.tone = newNudgeUpdateObject.value.tone;
          found.license = newNudgeUpdateObject.value.license;
          found.country = newNudgeUpdateObject.value.country;
          found.state = newNudgeUpdateObject.value.state;
          updateSelectedObject(found);
        }
    }
    nudgeObject.value = null;
  }
});



watch(nudgeRemoveObject, (newNudgeRemoveObject, oldNudgeRemoveObject) => {
  if (newNudgeRemoveObject.value != null) {
    // find the localObject
    if (trackedStations.value != null) {
        var found = null;
        var index = 0;
        var foundIndex = -1;
        trackedStations.value.forEach(function(trackedStation){
            if (trackedStation.callsign == newNudgeRemoveObject.value.callsign) {
              if (found == null) {
                found = trackedStation;
                foundIndex = index;
              }
            }
            index++;
        });
        if (foundIndex != -1) {
          trackedStations.value.splice(foundIndex, 1); 
          updateSelectedObject(null);
        }
    }
    nudgeRemoveObject.value = null;
  }
});
</script>

<template>
    <!-- main page -->
    <div class="pagesubheader">Resources</div>
    <div class="line"><hr/></div>
    <div v-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'PRIORITYOBJECT'))">
      <div v-if="((trackedStations.value == null) || (trackedStations.value.length == 0))">
        <br>
        <br>
        <br><i>No resources of this type.</i>
      </div>
      <div v-else>
        <EasyDataTable :headers="headersPriority" :items="trackedStations.value" 
        :rows-per-page="10"
        :body-row-class-name="getBodyRowClass"
        @click-row="showRow" buttons-pagination
        />
      </div>
    </div>
    <div v-else-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'CALLSIGN'))">
      <div v-if="((trackedStations.value == null) || (trackedStations.value.length == 0))">
        <br>
        <br>
        <br><i>No callsigns heard.</i>
      </div>
      <div v-else>
        <EasyDataTable :headers="headersCallsign" :items="trackedStations.value" 
        :rows-per-page="10"
        :body-row-class-name="getBodyRowClass"
        @click-row="showRow" buttons-pagination
        />
      </div>
    </div>
    <div v-else-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'IGNORE'))">
      <div v-if="((trackedStations.value == null) || (trackedStations.value.length == 0))">
        <br>
        <br>
        <br><i>No stations being ignored.</i>
      </div>
      <div v-else>
        <EasyDataTable :headers="headersIgnore" :items="trackedStations.value" 
        :rows-per-page="10"
        :body-row-class-name="getBodyRowClass"
        @click-row="showRow" buttons-pagination
        />
      </div>
    </div>
    <div v-else-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null) && (localSelectedObjectType.value == 'IS'))">
      <div v-if="((internetServers.value == null) || (internetServers.value.length == 0))">
        <br>
        <br>
        <br><i>No internet servers contacted.</i>
      </div>
      <div v-else>
        <EasyDataTable :headers="headersInternetServer" :items="internetServers.value" 
        :rows-per-page="10"
        :body-row-class-name="getBodyRowClass"
        @click-row="showRow" buttons-pagination
        />
      </div>
    </div>
    <div v-else-if="((localSelectedObjectType != null) && (localSelectedObjectType.value != null))">
      <div v-if="((trackedStations.value == null) || (trackedStations.value.length == 0))">
        <br>
        <br>
        <br><i>No resources of this type.</i>
      </div>
      <div v-else>
        <EasyDataTable :headers="headers" :items="trackedStations.value" 
        :rows-per-page="10"
        :body-row-class-name="getBodyRowClass"
        @click-row="showRow" buttons-pagination
        />
      </div>
    </div>
    <div v-else>
    </div>
</template>

<style scoped>
</style>

