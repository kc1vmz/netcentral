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
        { text: "Time", value: "prettyReceivedTime", sortable: true },
        { text: "From", value: "callsignFrom", sortable: true},
        { text: "To", value: "recipient", sortable: true},
        { text: "Message", value: "message", sortable: true}
      ]);

function updateLocalSelectedCompletedNet(newObject) {
  localSelectedCompletedNet.value = newObject.value;
}

// Watch for changes in the selected object ref
watch(selectedCompletedNet, (newSelectedCompletedNet, oldSelectedCompletedNet) => {
  updateLocalSelectedCompletedNet(newSelectedCompletedNet);
});

const netMessages = reactive({value : []});

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
      fetch(buildNetCentralUrl('/completedNets/'+localSelectedCompletedNet.value.completedNetId+'/messages'), requestOptions)
        .then(response => response.json())
        .then(data => {
            netMessages.value = data;
        })
        .catch(error => { console.error('Error getting net participants from server:', error); })
    }
  },
  { immediate: true }
)
</script>

<template>
    <!-- main page -->
    <div v-if="((localSelectedCompletedNet != null) && (localSelectedCompletedNet.value != null) && (localSelectedCompletedNet.value.callsign != null))">
      <div v-if="((netMessages.value == null) || (netMessages.value.length == 0))">
        <br>
        <br>
        <br><i>No messages during this net.</i>
      </div>
      <div v-else>
        <div class="pagesubheader">Messages</div>
        <div class="line"><hr/></div>
        <EasyDataTable :headers="headersRef" :items="netMessages.value" :rows-per-page="10" buttons-pagination/>
      </div>
    </div>
    <div v-else>
    </div>
</template>
