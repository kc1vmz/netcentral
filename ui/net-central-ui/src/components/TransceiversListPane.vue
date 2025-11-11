<script setup>
import { ref, watch, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { selectedTransceiver , updateSelectedTransceiver, setSelectedTransceiverSelectionValue, transceiverRefresh, forceTransceiverRefresh } from "@/SelectedTransceiver.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

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
