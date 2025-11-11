<script setup>
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser } from "@/LoginInformation.js";
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue } from "@/SelectedNet.js";
import { selectedObject , updateSelectedObject } from "@/SelectedObject.js";
import { ref, watch, reactive } from 'vue';
import { nudgeObject, nudge } from "@/nudgeObject.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
const selectedItem = ref(null);

function updateLocalSelectedNet(newObject) {
  localSelectedNet.ncSelectedNet = newObject.ncSelectedNet;
}

// Watch for changes in the selected object ref
watch(selectedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
});

const netParticipants = reactive([]);
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
    if ((localSelectedNet.ncSelectedNet.callsign != null)  && (localSelectedNet.ncSelectedNet.type == null)) {
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
  <div v-if="((localSelectedNet != null) && (localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign == null))">
    <!-- no nets -->
  </div>
  <div v-else-if="localSelectedNet.ncSelectedNet.type == null">
    <div class="pagesubheader">Participants</div>
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
