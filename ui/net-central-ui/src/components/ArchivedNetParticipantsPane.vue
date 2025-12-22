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
import { selectedCompletedNet } from "@/SelectedCompletedNet.js";
import { ref, watch, reactive } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const localSelectedCompletedNet = reactive({value : { id : null }});
const headersRef = ref([
        { text: "Callsign", value: "callsign", sortable: true },
        { text: "Tactical Callsign", value: "tacticalCallsign", sortable: true},
        { text: "Check-in", value: "prettyStartTime", sortable: true},
        { text: "Check-out", value: "prettyEndTime", sortable: true},
        { text: "Electrical Power", value: "electricalType", sortable: true},
        { text: "Backup Power", value: "backupElectricalType", sortable: true},
        { text: "Transmit Power", value: "transmitPower", sortable: true}
      ]);

function updateLocalSelectedCompletedNet(newObject) {
  localSelectedCompletedNet.value = newObject.value;
}

// Watch for changes in the selected object ref
watch(selectedCompletedNet, (newSelectedCompletedNet, oldSelectedCompletedNet) => {
  updateLocalSelectedCompletedNet(newSelectedCompletedNet);
});

const netParticipants = reactive({ value : []});
const netExpectedParticipants = reactive({ value : []});

watch(
  localSelectedCompletedNet,
  async () => {
    if ((localSelectedCompletedNet.value != null) && (localSelectedCompletedNet.value.callsign != null)) {
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      fetch(buildNetCentralUrl('/completedNets/'+localSelectedCompletedNet.value.completedNetId+'/participants'), requestOptions)
        .then(response => response.json())
        .then(data => {
            netParticipants.value = data;
            fetch(buildNetCentralUrl('/completedNets/'+localSelectedCompletedNet.value.completedNetId+'/expectedParticipants'), requestOptions)
              .then(response => response.json())
              .then(data => {
                  netExpectedParticipants.value = data;

                  // find missing and tweak / add
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

                })
              .catch(error => { console.error('Error getting net participants from server:', error); })
        })
        .catch(error => { console.error('Error getting net participants from server:', error); })
    }
  },
  { immediate: true }
)

function isParticipantHere(participantList, participant){
    var found = false;
    participantList.value.forEach(function(participantItem) {
      var participantCallsignPieces = participantItem.callsign.split("-"); 
      if ((!found) && (participant.callsign == participantCallsignPieces[0])) {
          found = true;
      }});
    return found;
}


</script>

<template>
    <!-- main page -->
    <div v-if="((localSelectedCompletedNet != null) && (localSelectedCompletedNet.value != null) && (localSelectedCompletedNet.value.callsign != null))">
      <div v-if="((netParticipants.value == null) || (netParticipants.value.length == 0))">
        <br>
        <br>
        <br><i>No participants during this net.</i>
      </div>
      <div v-else>
        <div class="pagesubheader">Participants</div>
        <div class="line"><hr/></div>
        <EasyDataTable :headers="headersRef" :items="netParticipants.value" :rows-per-page="10" buttons-pagination/>
      </div>
    </div>
    <div v-else>
        <br>
        <br>
        <br><i>Select a net for additional information.</i>
    </div>
</template>

