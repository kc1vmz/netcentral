<script setup>
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser } from "@/LoginInformation.js";
import { selectedCompletedNet , updateSelectedCompletedNet, setSelectedCompletedNetValue } from "@/SelectedCompletedNet.js";
import { ref, reactive, onMounted } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const selectedItem = ref(null);
const headersRef = ref([
  { text: "Callsign", value: "callsign", sortable: true },
  { text: "Name", value: "name", sortable: true},
  { text: "Description", value: "decription", sortable: true},
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
        <EasyDataTable :headers="headersRef" :items="completedNetsRef.value"
            :rows-per-page="10"
            :body-row-class-name="getBodyRowClass"
            @click-row="showRow" buttons-pagination
        />
    </div>
</template>

