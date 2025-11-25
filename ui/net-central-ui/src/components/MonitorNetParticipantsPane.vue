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
  } else if (newValue.value.action == "Delete") {
    const indexToRemove = netParticipants.value.findIndex(obj => obj.callsign === newValue.value.object.callsign);
    if (indexToRemove !== -1) {
      if ((selectedItem.value != null)&& (selectedItem.value.callsign === newValue.value.callsign)) {
        selectedItem.value = null;
        updateSelectedObject(null); 
      }
      netParticipants.value.splice(indexToRemove, 1);
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
        })
        .catch(error => { console.error('Error getting net participants from server:', error); })
    }
  },
  { immediate: true }
)

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
