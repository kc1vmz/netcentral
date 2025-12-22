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
import { ref, watch, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { selectedTransceiver , updateSelectedTransceiver, setSelectedTransceiverSelectionValue, transceiverRefresh, forceTransceiverRefresh } from "@/SelectedTransceiver.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateTransceiverEvent } from "@/UpdateEvents";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";

const { socket } = useSocketIO();
socket.on("updateTransceiver", (data) => {
  updateTransceiver(data)
});

function updateTransceiver(data) {
    updateTransceiverEvent.value = JSON.parse(data);
}

onMounted(() => {
    accesstoken.value = getToken()
    selectedItem.value = null;
    updateLocalSelectedTransceiver({ncSelectedTransceiver : { id : null }});
    getRegisteredTransceivers();
});

const localSelectedTransceiver = reactive({ncSelectedTransceiver : { id : null }});
const selectedItem = ref(null);

var registeredTransceiversRef = reactive({value : []});
var accesstoken = ref('');

watch(transceiverRefresh, (newValue, oldValue) => {
  getRegisteredTransceivers();
});

watch(updateTransceiverEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((newValue.value.action == "Create") || (newValue.value.action == "Delete") || (newValue.value.action == "Update")) {
    getRegisteredTransceivers();
  }
});

function updateLocalSelectedTransceiver(newTransceiver) {
  localSelectedTransceiver.ncSelectedTransceiver = newTransceiver.ncSelectedTransceiver;
  updateSelectedTransceiver(newTransceiver);
}

function getRegisteredTransceivers() {
  var requestOptions = {
    method: "GET",
    headers: { "Content-Type": "application/json",
                "SessionID" : accesstoken.value
    },
    body: null
  };
  fetch(buildNetCentralUrl('/registeredTransceivers'), requestOptions)
    .then(response => response.json())
    .then(data => {
        registeredTransceiversRef.value = data;

        // find the previously selected list item and update it for everyone else
        if (registeredTransceiversRef.value != null) {
          registeredTransceiversRef.value.forEach(function(registeredTransceiver){
              if (registeredTransceiver.id == localSelectedTransceiver.ncSelectedTransceiver.id) {
                updateSelectedTransceiver({ ncSelectedTransceiver : registeredTransceiver});
              }
          });
        }
    })
    .catch(error => { console.error('Error getting registered transceivers from server:', error); })
}

function showRow(item) {
    selectedItem.value = item;
    updateLocalSelectedTransceiver({ncSelectedTransceiver : item}); 
}

function getBodyRowClass(item, rowNumber) {
    if ((selectedItem.value != null) && (selectedItem.value.id === item.id)) {
      return 'bold-row';
    }
    return '';
}

const headers = [
              { text: "Name", value: "name", sortable: true },
              { text: "Description", value: "description", sortable: true},
              { text: "Type", value: "type", sortable: true},
              { text: "Receive enabled", value: "enabledReceive", sortable: true},
              { text: "Transmit enabled", value: "enabledTransmit", sortable: true}];    

</script>

<template>
    <!-- main page -->
    <EasyDataTable :headers="headers" :items="registeredTransceiversRef.value" 
      :body-row-class-name="getBodyRowClass"
      :rows-per-page="10"
      @click-row="showRow" buttons-pagination
      />
</template>
