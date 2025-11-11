<script setup>
import { ref, watch, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { Tabs, Tab } from 'super-vue3-tabs';
import { selectedTransceiver , updateSelectedTransceiver, setSelectedTransceiverSelectionValue, transceiverRefresh, forceTransceiverRefresh } from "@/SelectedTransceiver.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const localSelectedTransceiver = reactive({ncSelectedTransceiver : { id : null }});
const dialogEnableTransceiverReceive = ref(null);
const dialogEnableTransceiverReceiveShow = reactive({value : false});
const dialogDisableTransceiverReceive = ref(null);
const dialogDisableTransceiverReceiveShow = reactive({value : false});
const dialogEnableTransceiverTransmit = ref(null);
const dialogEnableTransceiverTransmitShow = reactive({value : false});
const dialogDisableTransceiverTransmit = ref(null);
const dialogDisableTransceiverTransmitShow = reactive({value : false});
const dialogDeleteTransceiver = ref(null);
const dialogDeleteTransceiverShow = reactive({value : false});
const accesstokenRef = reactive({value : null});
const localLoggedInUserRef = reactive({value : null});


onMounted(() => {
    accesstokenRef.value = getToken();
    localLoggedInUserRef.value = getUser();
})

function updateLocalSelectedTransceiver(newTransceiver) {
  localSelectedTransceiver.ncSelectedTransceiver = newTransceiver.ncSelectedTransceiver;
}

// Watch for changes in the selected Transceiver ref
watch(selectedTransceiver, (newselectedTransceiver, oldselectedTransceiver) => {
  updateLocalSelectedTransceiver(newselectedTransceiver);
});


function enableTransceiverReceive() {
    dialogEnableTransceiverReceiveShow.value = true;
}

function enableTransceiverReceiveYes() {
    performEnableTransceiverReceive();
}

function enableTransceiverReceiveNo() {
    dialogEnableTransceiverReceiveShow.value = false;
}

function performEnableTransceiverReceive() {
    performTransceiverUpdate(true, localSelectedTransceiver.ncSelectedTransceiver.enabledTransmit, dialogEnableTransceiverReceiveShow)
}

function performTransceiverUpdate(enabledReceive, enabledTransmit, dialogShow) {
    var bodyObject = { id : localSelectedTransceiver.ncSelectedTransceiver.id,
                        name : localSelectedTransceiver.ncSelectedTransceiver.name,
                        description: localSelectedTransceiver.ncSelectedTransceiver.description, 
                        fqdName: localSelectedTransceiver.ncSelectedTransceiver.fqdName,
                        type: localSelectedTransceiver.ncSelectedTransceiver.type,
                        port: localSelectedTransceiver.ncSelectedTransceiver.port,
                        enabledReceive : enabledReceive,
                        enabledTransmit : enabledTransmit
                      };
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/registeredTransceivers/"+localSelectedTransceiver.ncSelectedTransceiver.id), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogShow.value = false;
            forceTransceiverRefresh();
        } else {
          errorMessageRef.value = "An error occurred updating transceiver information.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error updating transceiver information:', error); })   
}

function disableTransceiverReceive() {
    dialogDisableTransceiverReceiveShow.value = true;
}

function disableTransceiverReceiveYes() {
    performDisableTransceiverReceive();
}

function disableTransceiverReceiveNo() {
    dialogDisableTransceiverReceiveShow.value = false;
}

function performDisableTransceiverReceive() {
    performTransceiverUpdate(false, localSelectedTransceiver.ncSelectedTransceiver.enabledTransmit, dialogDisableTransceiverReceiveShow)
}

function enableTransceiverTransmit() {
    dialogEnableTransceiverTransmitShow.value = true;
}

function enableTransceiverTransmitYes() {
    performEnableTransceiverTransmit();
}

function enableTransceiverTransmitNo() {
    dialogEnableTransceiverTransmitShow.value = false;
}

function performEnableTransceiverTransmit() {
    performTransceiverUpdate(localSelectedTransceiver.ncSelectedTransceiver.enabledReceive, true, dialogEnableTransceiverTransmitShow)
}

function disableTransceiverTransmit() {
    dialogDisableTransceiverTransmitShow.value = true;
}

function disableTransceiverTransmitYes() {
    performDisableTransceiverTransmit();
}

function disableTransceiverTransmitNo() {
  dialogDisableTransceiverTransmitShow.value = false;
}

function performDisableTransceiverTransmit() {
    performTransceiverUpdate(localSelectedTransceiver.ncSelectedTransceiver.enabledReceive, false, dialogDisableTransceiverTransmitShow)
}

function deleteTransceiver() {
    dialogDeleteTransceiverShow.value = true;
}

function deleteTransceiverYes() {
    performDeleteTransceiver();
}

function deleteTransceiverNo() {
    dialogDeleteTransceiverShow.value = false;
}

function performDeleteTransceiver() {
    const requestOptions = {
      method: "DELETE",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: null
    };
    fetch(buildNetCentralUrl("/registeredTransceivers/"+localSelectedTransceiver.ncSelectedTransceiver.id), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogDeleteTransceiverShow.value = false;
            forceTransceiverRefresh();
        } else {
          errorMessageRef.value = "An error occurred deleting transceiver information.";
        }
        return "";
      })
      .then(data => {
      })
      .catch(error => { console.error('Error deleting transceiver information:', error); })   
}
</script>

<template>
  <!-- dialogs -->
    <div v-if="dialogEnableTransceiverReceiveShow.value">
      <teleport to="#modals">
        <dialog :open="dialogEnableTransceiverReceiveShow.value" ref="dialogEnableTransceiverReceive" @close="dialogEnableTransceiverReceiveShow.value = false" class="topz">  
          <form v-if="dialogEnableTransceiverReceiveShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you want to enable transceiver "{{ localSelectedTransceiver.ncSelectedTransceiver.name }}" for providing receiving APRS data?
            <br>
            <button class="boxButton" v-on:click.native="enableTransceiverReceiveYes">Yes</button>
            <button class="boxButton" v-on:click.native="enableTransceiverReceiveNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogDisableTransceiverReceiveShow.value">
      <teleport to="#modals">
        <dialog :open="dialogDisableTransceiverReceiveShow.value" ref="dialogDisableTransceiverReceive" @close="dialogDisableTransceiverReceiveShow.value = false" class="topz">  
          <form v-if="dialogDisableTransceiverReceiveShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you want to disable transceiver "{{ localSelectedTransceiver.ncSelectedTransceiver.name }}" from providing APRS data?
            <br>
            <button class="boxButton" v-on:click.native="disableTransceiverReceiveYes">Yes</button>
            <button class="boxButton" v-on:click.native="disableTransceiverReceiveNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogEnableTransceiverTransmitShow.value">
      <teleport to="#modals">
        <dialog :open="dialogEnableTransceiverTransmitShow.value" ref="dialogEnableTransceiverTransmit" @close="dialogEnableTransceiverTransmitShow.value = false" class="topz">  
          <form v-if="dialogEnableTransceiverTransmitShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you want to enable transceiver "{{ localSelectedTransceiver.ncSelectedTransceiver.name }}" for transmitting APRS data?
            <br>
            <button class="boxButton" v-on:click.native="enableTransceiverTransmitYes">Yes</button>
            <button class="boxButton" v-on:click.native="enableTransceiverTransmitNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogDisableTransceiverTransmitShow.value">
      <teleport to="#modals">
        <dialog :open="dialogDisableTransceiverTransmitShow.value" ref="dialogDisableTransceiverTransmit" @close="dialogDisableTransceiverTransmitShow.value = false" class="topz">  
          <form v-if="dialogDisableTransceiverTransmitShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you want to disable transceiver "{{ localSelectedTransceiver.ncSelectedTransceiver.name }}" from transmitting APRS data?
            <br>
            <button class="boxButton" v-on:click.native="disableTransceiverTransmitYes">Yes</button>
            <button class="boxButton" v-on:click.native="disableTransceiverTransmitNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogDeleteTransceiverShow.value">
      <teleport to="#modals">
        <dialog :open="dialogDeleteTransceiverShow.value" ref="dialogDeleteTransceiver" @close="dialogDeleteTransceiverShow.value = false" class="topz">  
          <form v-if="dialogDeleteTransceiverShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you want to delete transceiver "{{ localSelectedTransceiver.ncSelectedTransceiver.name }}" ?
            <br>
            <button class="boxButton" v-on:click.native="deleteTransceiverYes">Yes</button>
            <button class="boxButton" v-on:click.native="deleteTransceiverNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>

  <!-- main window -->
    <div v-if="(localSelectedTransceiver != null) && (localSelectedTransceiver.ncSelectedTransceiver != null) && (localSelectedTransceiver.ncSelectedTransceiver.id != null)">
        <Tabs>
          <Tab value="Details">
            <br>Name: {{ localSelectedTransceiver.ncSelectedTransceiver.name }}
            <br>Description: {{ localSelectedTransceiver.ncSelectedTransceiver.description }}
            <br>Type: {{ localSelectedTransceiver.ncSelectedTransceiver.type }}
            <br>URL: http://{{ localSelectedTransceiver.ncSelectedTransceiver.fqdName }}:{{ localSelectedTransceiver.ncSelectedTransceiver.port }}
            <br>Receive Enabled: {{ localSelectedTransceiver.ncSelectedTransceiver.enabledReceive }}
            <br>Transmit Enabled: {{ localSelectedTransceiver.ncSelectedTransceiver.enabledTransmit }}
          </Tab>
          <Tab value="Actions" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))">
            <div class="grid-container-actions">
              <div v-if="(localSelectedTransceiver.ncSelectedTransceiver.enabledReceive)" class="grid-item">
                <button class="boxButton" v-on:click.native="disableTransceiverReceive">Disable Receive</button>
              </div>
              <div v-else>
                <button class="boxButton" v-on:click.native="enableTransceiverReceive">Enable Receive</button>
              </div>
              <div v-if="(localSelectedTransceiver.ncSelectedTransceiver.enabledReceive)" class="grid-item">
                Disable the transceiver from providing received APRS information.
              </div>
              <div v-else>
                Enable the transceiver to provide received APRS information.
              </div>

              <div v-if="(localSelectedTransceiver.ncSelectedTransceiver.enabledTransmit)" class="grid-item">
                <button class="boxButton" v-on:click.native="disableTransceiverTransmit">Disable Transmit</button>
              </div>
              <div v-else>
                <button class="boxButton" v-on:click.native="enableTransceiverTransmit">Enable Transmit</button>
              </div>
              <div v-if="(localSelectedTransceiver.ncSelectedTransceiver.enabledTransmit)" class="grid-item">
                Disable the transceiver from transmitting APRS information.
              </div>
              <div v-else>
                Enable the transceiver to transmit APRS information.
              </div>
              <div class="grid-item">
                <button class="boxButton" v-on:click.native="deleteTransceiver">Delete</button>
              </div>
              <div class="grid-item">
                Delete the transceiver from being registered with this Net Central server.
              </div>
            </div>
          </Tab>
        </Tabs>
    </div>
    <div v-else>
    </div>
</template>

<style scoped>
.grid-container-actions {
  display: grid;
  grid-template-columns: 30% 70%;
  margin: 5px;
  gap: 5px;
}
</style>
