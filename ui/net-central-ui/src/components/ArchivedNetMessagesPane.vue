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
    if (localSelectedCompletedNet.value.callsign != null) {
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
