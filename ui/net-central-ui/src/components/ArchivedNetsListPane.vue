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
import { selectedCompletedNet , updateSelectedCompletedNet, setSelectedCompletedNetValue } from "@/SelectedCompletedNet.js";
import { ref, reactive, onMounted, watch } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { updateCompletedNetEvent } from "@/UpdateEvents.js";
import { useSocketIO } from "@/composables/socket";
import { updateTrackedStationEvent, updateObjectEvent, updateCallsignEvent, updateAll, updateAllEvent } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";

const { socket } = useSocketIO();
socket.on("updateCompletedNet", (data) => {
  updateCompletedNet(data)
});

function updateCompletedNet(data) {
    updateCompletedNetEvent.value = JSON.parse(data);
}

const selectedItem = ref(null);
const headersRef = ref([
  { text: "Callsign", value: "callsign", sortable: true },
  { text: "Name", value: "name", sortable: true},
  { text: "Start Time", value: "prettyStartTime", sortable: true},
  { text: "End Time", value: "prettyEndTime", sortable: true}
]);

const completedNetsRef = reactive({ value : [] });
const accesstokenRef = reactive({ value : '' });

onMounted(() => {
    accesstokenRef.value = getToken();
    completedNetsRef.value = [];
    getData()
})

watch(updateCompletedNetEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((newValue.value.action == "Create") || (newValue.value.action == "Delete") || (newValue.value.action == "Update")) {
    getArchivedNets();
  }
});

function getData () {
    selectedItem.value = null;
    updateSelectedCompletedNet({id:null});
    getArchivedNets();
}

function getArchivedNets() {
    const requestOptions = {
      method: "GET",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: null
    };
    fetch(buildNetCentralUrl('/completedNets'), requestOptions)
      .then(response => response.json())
      .then(data => {
          completedNetsRef.value = data;
          if ((completedNetsRef.value != null) && (completedNetsRef.value.length > 0)) {
            updateSelectedCompletedNet({id:null}); 
          }
      })
      .catch(error => { console.error('Error getting archived nets from server:', error); })
}

function showRow(item) {
    selectedItem.value = item;
    updateSelectedCompletedNet(item); 
}

function getBodyRowClass(item, rowNumber) {
    if ((selectedItem.value != null) && (selectedItem.value.completedNetId === item.completedNetId)) {
      return 'bold-row';
    }
    return '';
}
</script>

<template>
    <!-- main page -->
    <div>
        <div class="pagesubheader">Archived Nets</div>
        <div class="line"><hr/></div>
        <EasyDataTable :headers="headersRef" :items="completedNetsRef.value"
            :rows-per-page="10"
            :body-row-class-name="getBodyRowClass"
            @click-row="showRow" buttons-pagination
        />
    </div>
</template>

