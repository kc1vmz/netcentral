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
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser } from "@/LoginInformation.js";
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue } from "@/SelectedNet.js";
import { selectedObject , updateSelectedObject } from "@/SelectedObject.js";
import { ref, watch, reactive } from 'vue';
import { nudgeObject, nudge } from "@/nudgeObject.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateNetParticipantEvent, updateNetParticipant, updateParticipant, updateParticipantEvent } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";
import { isMobileClient } from "@/composables/MobileLibrary";

const { socket } = useSocketIO();
socket.on("updateNetParticipant", (data) => {
  updateNetParticipant(data)
});
socket.on("updateParticipant", (data) => {
  updateParticipant(data)
});
socket.on("updateNetExpectedParticipant", (data) => {
  updateNetExpectedParticipant(data)
});

function updateNetExpectedParticipant(data) {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedNet.ncSelectedNet == null) || ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign !== data.callsign))) {
    // not this net
    return;
  }
  if (data.action == "Create") {
    var found = false;
    if (netParticipants.value != null) {
      netParticipants.value.forEach(function(netParticipant){
        var root = netParticipant.callsign.split("-")[0];
        if ((!found) && (root == data.object.callsign)) {
          found = true;  // correct net and participant
        }
      });
      if (found) {
        return;
      }
    } else {
      netParticipants.value = [];
    }
    var expectedParticipant = data.object;
    // tweak it and add it 
    expectedParticipant.status = "Absent";
    expectedParticipant.tacticalCallsign = "";
    expectedParticipant.prettyStartTime = "";
    expectedParticipant.lat = "";
    expectedParticipant.lat = "";
    expectedParticipant.lon = "";
    expectedParticipant.prettyLastHeardTime = "";
    netParticipants.value.push(expectedParticipant);
  } else if (data.action == "Delete") {
    removeExpectedParticipant(data.object.callsign);
  }
}

watch(updateNetParticipantEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedNet.ncSelectedNet == null) || ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign !== newValue.value.callsign))) {
    // not this net
    return;
  }
  if (newValue.value.action == "Create") {
    var found = false;
    if (netParticipants.value != null) {
      netParticipants.value.forEach(function(netParticipant){
        if ((!found) && (netParticipant.callsign == newValue.value.object.callsign)) {
          found = true;  // correct net and participant
        }
      });
      if (found) {
        return;
      }
    } else {
      netParticipants.value = [];
    }
    netParticipants.value.push(newValue.value.object);
    removeExpectedParticipant(newValue.value.object.callsign);
  } else if (newValue.value.action == "Delete") {
    const indexToRemove = netParticipants.value.findIndex(obj => obj.callsign === newValue.value.object.callsign);
    if (indexToRemove !== -1) {
      if ((selectedItem.value != null)&& (selectedItem.value.callsign === newValue.value.callsign)) {
        selectedItem.value = null;
        updateSelectedObject(null); 
      }
      netParticipants.value.splice(indexToRemove, 1);
      getExpectedParticipants(localSelectedNet);
    }
  } else if (newValue.value.action == "Update") {
    if (netParticipants.value != null) {
      netParticipants.value.forEach(function(netParticipant){
        if (netParticipant.callsign == newValue.value.object.callsign) {
          netParticipant.voiceFrequency = newValue.value.object.voiceFrequency;
          netParticipant.prettyStartTime = newValue.value.object.prettyStartTime;
          netParticipant.lat = newValue.value.object.lat;
          netParticipant.lon = newValue.value.object.lon;
          netParticipant.electricalPowerType = newValue.value.object.electricalPowerType;
          netParticipant.backupElectricalPowerType = newValue.value.object.backupElectricalPowerType;
          netParticipant.radioStyle = newValue.value.object.radioStyle;
          netParticipant.transmitPower = newValue.value.object.transmitPower;
          netParticipant.tacticalCallsign = newValue.value.object.tacticalCallsign;
          netParticipant.prettyLastHeardTime = newValue.value.object.prettyLastHeardTime;
        }
      });
    }
  }
});

function removeExpectedParticipant(callsign) {
    var root = callsign.split("-")[0];
    const indexToRemove = netParticipants.value.findIndex(obj => (obj.callsign === root));
    if (indexToRemove !== -1) {
      netParticipants.value.splice(indexToRemove, 1);
    }
}

watch(updateParticipantEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if (newValue.value.action == "Update") {
    if (netParticipants.value != null) {
      netParticipants.value.forEach(function(netParticipant){
        if (netParticipant.callsign == newValue.value.object.callsign) {
          netParticipant.voiceFrequency = newValue.value.object.voiceFrequency;
          netParticipant.status = newValue.value.object.status;
        }
      });
    }
  }
});

const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
const selectedItem = ref(null);

function updateLocalSelectedNet(newObject) {
  if (newObject == null) {
    localSelectedNet.ncSelectedNet = null;
  } else {
    localSelectedNet.ncSelectedNet = newObject.ncSelectedNet;
  }
}

// Watch for changes in the selected object ref
watch(selectedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
});

const netParticipants = reactive({ value : [] });
const netExpectedParticipants = reactive({ value : [] });
const headers = [
        { text: "Callsign", value: "callsign", sortable: true },
        { text: "Tactical Callsign", value: "tacticalCallsign", sortable: true},
        { text: "Start time", value: "prettyStartTime", sortable: true},
        { text: "Lat", value: "lat", sortable: true},
        { text: "Lon", value: "lon", sortable: true},
        { text: "Status", value: "status", sortable: true},
        { text: "Last Heard", value: "prettyLastHeardTime", sortable: true}];

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

watch(
  localSelectedNet,
  async () => {
    getParticipants(localSelectedNet);
  },
  { immediate: true }
)

function getParticipants(localSelectedNet) {
    if ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign != null)  && (localSelectedNet.ncSelectedNet.type == null)) {
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      // only for active nets
      fetch(buildNetCentralUrl('/nets/'+localSelectedNet.ncSelectedNet.callsign+'/participants'), requestOptions)
        .then(response => response.json())
        .then(data => {
            netParticipants.value = data;
            updateSelectedObject(null);
            selectedItem.value = null;
            getExpectedParticipants(localSelectedNet);
        })
        .catch(error => { console.error('Error getting net participants from server:', error); })
    }
}

function getExpectedParticipants(localSelectedNet) {
    if ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign != null)  && (localSelectedNet.ncSelectedNet.type == null)) {
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      // only for active nets
      fetch(buildNetCentralUrl('/nets/'+localSelectedNet.ncSelectedNet.callsign+'/expectedParticipants'), requestOptions)
        .then(response => response.json())
        .then(data => {
            netExpectedParticipants.value = data;

            // see who is missing and add them
            if (netExpectedParticipants.value != null) {
                netExpectedParticipants.value.forEach(function(expectedParticipant){
                    if (!isParticipantHere(netParticipants, expectedParticipant)) {
                      // tweak it and add it 
                      expectedParticipant.status = "Absent";
                      expectedParticipant.tacticalCallsign = "";
                      expectedParticipant.prettyStartTime = "";
                      expectedParticipant.lat = "";
                      expectedParticipant.lat = "";
                      expectedParticipant.lon = "";
                      expectedParticipant.prettyLastHeardTime = "";
                      netParticipants.value.push(expectedParticipant);
                  }});
            }

        })
        .catch(error => { console.error('Error getting net expected participants from server:', error); })
    }
}

function isParticipantHere(participantList, participant){
    var found = false;
    participantList.value.forEach(function(participantItem) {
      var participantCallsignPieces = participantItem.callsign.split("-"); 
      if ((!found) && (participant.callsign == participantCallsignPieces[0])) {
          found = true;
      }});
    return found;
}

watch(nudgeObject, (newNudgeObject, oldNudgeObject) => {
  if (newNudgeObject.value != null) {
    // find the localObject
    if (netParticipants.value != null) {
        var found = { value: null };
        netParticipants.value.forEach(function(objectItem){
            if (objectItem.callsign == newNudgeObject.value) {
              found = objectItem;
          }});
        updateSelectedObject(found);
    }
    nudgeObject.value = null;
  }
});

</script>

<template>
  <!-- main page -->
  <div v-if="((localSelectedNet == null) || (localSelectedNet.ncSelectedNet == null) || (localSelectedNet.ncSelectedNet.callsign == null))">
    <!-- no nets -->
  </div>
  <div v-else-if="((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.type == null))">
    <div v-if="!isMobileClient()" class="pagesubheader">Participants</div>
    <div v-else class="mobilepagesubheader">Participants</div>
    <div class="line"><hr/></div>

    <div v-if="((netParticipants.value == null) || (netParticipants.value.length == 0))">
      <br>
      <br>
      <br><i>No participants.</i>
    </div>
    <div v-else>
      <EasyDataTable :headers="headers" :items="netParticipants.value" 
      :body-row-class-name="getBodyRowClass"
      :rows-per-page="10" @click-row="showRow" buttons-pagination/>
    </div>
  </div>
</template>

<style scoped>
</style>
