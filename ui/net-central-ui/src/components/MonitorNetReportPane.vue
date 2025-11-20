<script setup>
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { ref, watch, reactive, onMounted } from 'vue';
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue } from "@/SelectedNet.js";
import { selectedCallsign, updateSelectedCallsign } from "@/SelectedCallsign.js";
import { selectedObject, updateSelectedObject } from "@/SelectedObject.js";
import { nudge } from "@/nudgeObject.js";
import { Tabs, Tab } from 'super-vue3-tabs';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateNetParticipantEvent, updateNetParticipant, updateCallsign, updateCallsignEvent } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";

const { socket } = useSocketIO();
socket.on("updateNetParticipant", (data) => {
  updateNetParticipant(data)
});

socket.on("updateCallsign", (data) => {
  updateCallsign(data)
});

watch(updateCallsignEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  // create would never be selected
  if (newValue.value.action == "Delete") {
      if ((localCallsign.value != null) && (localCallsign.value.callsign === newValue.value.callsign)) {
        localCallsign.value = null
        nudge(localCallsign.value);
      }
  } else if (newValue.value.action == "Update") {
    if ((localCallsign.value != null) && (localCallsign.value.callsign === newValue.value.callsign)) {
      localCallsign.value.name = newValue.value.object.name;
      localCallsign.value.country = newValue.value.object.country;
      localCallsign.value.state = newValue.value.object.state;
      localCallsign.value.license = newValue.value.object.license;
    }
  }
});


watch(updateNetParticipantEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedNet.ncSelectedNet == null) || ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign !== newValue.value.callsign))) {
    // not this net
    return;
  }
  // create would never be selected
  if (newValue.value.action == "Delete") {
      if ((localSelectedObject.ncSelectedObject != null) && (localSelectedObject.ncSelectedObject.callsign === newValue.value.object.callsign)) {
        var temp = { ncSelectedObject : null} ;
        updateLocalObject(temp); 
      }
  } else if (newValue.value.action == "Update") {
    if ((localSelectedObject.ncSelectedObject != null) && (localSelectedObject.ncSelectedObject.callsign === newValue.value.object.callsign)) {
      localSelectedObject.ncSelectedObject.voiceFrequency = newValue.value.object.voiceFrequency;
      localSelectedObject.ncSelectedObject.prettyStartTime = newValue.value.object.prettyStartTime;
      localSelectedObject.ncSelectedObject.lat = newValue.value.object.lat;
      localSelectedObject.ncSelectedObject.lon = newValue.value.object.lon;
      localSelectedObject.ncSelectedObject.electricalPowerType = newValue.value.object.electricalPowerType;
      localSelectedObject.ncSelectedObject.backupElectricalPowerType = newValue.value.object.backupElectricalPowerType;
      localSelectedObject.ncSelectedObject.radioStyle = newValue.value.object.radioStyle;
      localSelectedObject.ncSelectedObject.transmitPower = newValue.value.object.transmitPower;
      localSelectedObject.ncSelectedObject.tacticalCallsign = newValue.value.object.tacticalCallsign;
      localSelectedObject.ncSelectedObject.prettyLastHeardTime = newValue.value.object.prettyLastHeardTime;
    }
  }
});

const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
const localSelectedObject = reactive({ncSelectedObject : { callsign : null }});
const localCallsign = reactive({value : null});
const localCallsignObject = reactive({value : null});

const callsignRef = reactive({value : ''});
const rootCallsignRef = reactive({value : ''});
const netCallsignRef = reactive({value : ''});
const accesstokenRef = reactive({value : ''});
const errorMessageRef = reactive({value : ''});

var dialogIdentify = ref('');
var dialogIdentifyShow = reactive({value : false});
var dialogSendCallsignMessage = ref('');
var dialogSendCallsignMessageShow = reactive({value : false});
var messageRef = ref('');

onMounted(() => {
    accesstokenRef.value = getToken();
    selectedObject.value = null;
});

function updateLocalSelectedNet(newObject) {
  localSelectedNet.ncSelectedNet = newObject.ncSelectedNet;
}

function updateLocalCallsign(newObject) {
  localCallsign.value = newObject.value;
  nudge(newObject.value);
}

function updateLocalObject(newObject) {
  localSelectedObject.ncSelectedObject = newObject.ncSelectedObject;
  localCallsignObject.value = null;

  if ((localSelectedObject.ncSelectedObject != null) && (localSelectedObject.ncSelectedObject.callsign != null)) {
    var requestOptions = {
      method: "GET",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: null
    };
    fetch(buildNetCentralUrl('/callsigns/'+localSelectedObject.ncSelectedObject.callsign), requestOptions)
      .then(response => response.json())
      .then(data => {
          localCallsignObject.value = data;
      })
      .catch(error => { console.error('Error getting callsign info from server:', error); })
  }

}

// Watch for changes in the selected object ref
watch(selectedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
});
watch(selectedCallsign, (newSelectedCallsign, oldSelectedCallsign) => {
  updateLocalCallsign(newSelectedCallsign);
});
watch(selectedObject, (newSelectedObject, oldSelectedobject) => {
  updateLocalObject(newSelectedObject);
});

var enableButtonRef = ref(false);
watch(messageRef, (newValue, oldValue) => {
  if (newValue != '') {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});

function identify() {
    rootCallsignRef.value = localSelectedObject.ncSelectedObject.callsign;
    if (rootCallsignRef.value.includes("-")) {
      rootCallsignRef.value = rootCallsignRef.value.substring(0, rootCallsignRef.value.indexOf("-"));
    }
    dialogIdentifyShow.value = true;
}

function identifyYes() {
    dialogIdentifyShow.value = false;
    // perform the identify
    performIdentify();
}

function identifyNo() {
    dialogIdentifyShow.value = false;
}

function performIdentify() {
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: null
    };
    fetch(buildNetCentralUrl("/callsigns/"+rootCallsignRef.value+"/identifyRequests"), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
            dialogIdentifyShow.value = false;
      })
      .catch(error => { console.error('Error identifying callsign:', error); })
}
  
function sendMessage() {
      dialogSendCallsignMessageShow.value = true;
      netCallsignRef.value = localSelectedNet.ncSelectedNet.callsign;
      callsignRef.value = localSelectedObject.ncSelectedObject.callsign;
      messageRef.value = '';
}

function sendMessageYes() {
      // perform the send message
      performSendMessage();
}

function sendMessageNo() {
    dialogSendCallsignMessageShow.value = false;
}

function performSendMessage() {
    // message
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : getToken()
        },
      body: messageRef.value
    };
    fetch(buildNetCentralUrl("/callsigns/"+callsignRef.value+"/messages/"+netCallsignRef.value), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogSendCallsignMessageShow.value = false;
        } else {
          errorMessageRef.value = "An error occurred sending the message.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error sending message:', error); })   
}
</script>

<template>
  <!-- dialog -->
    <div v-if="dialogIdentifyShow.value">
      <teleport to="#modals">
        <dialog :open="dialogIdentifyShow.value" ref="dialogIdentify" @close="dialogIdentifyShow.value = false" class="topz">  
          <form v-if="dialogIdentifyShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to attempt to identify {{ rootCallsignRef.value }} with WHO-15?
            <br>
            <button class="boxButton" v-on:click.native="identifyYes">Yes</button>
            <button class="boxButton" v-on:click.native="identifyNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogSendCallsignMessageShow.value">
      <teleport to="#modals">    
        <dialog :open="dialogSendCallsignMessageShow.value" ref="dialogSendCallsignMessage" @close="dialogSendCallsignMessageShow.value = false" class="topz">  
          <form v-if="dialogSendCallsignMessageShow.value" method="dialog">
            <div class="pagesubheader">Send Message to {{ callsignRef.value }} from {{ netCallsignRef.value }}</div>
            <div class="line"><hr/></div>
            Send a message from the net to a single callsign.
            <br>
              <div>
                <label for="messageField">Message:</label>
                <input type="text" id="messageField" v-model="messageRef" />
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Send</button>
            <button class="boxButton" v-if="enableButtonRef" v-on:click.native="sendMessageYes">Send</button>
            <button class="boxButton" v-on:click.native="sendMessageNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

    <!-- main page-->
    <div v-if="((localSelectedNet != null) && (localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign == null))">
      <!-- no nets -->
    </div>
    <div v-else-if="localSelectedNet.ncSelectedNet.type == null">
      <div class="pagesubheader">Participant Information</div>
      <div class="line"><hr/></div>
      <div v-if="((localSelectedNet != null) && (localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.type == null))">
        <div v-if="((localSelectedObject != null) && (localSelectedObject.ncSelectedObject != null) && (localSelectedObject.ncSelectedObject.callsign != null))">
          <Tabs>
            <Tab value="Details">
              <br>Callsign: {{ localSelectedObject.ncSelectedObject.callsign }}
              <br>Tactical Callsign: {{ localSelectedObject.ncSelectedObject.tacticalCallsign }}
              <br>Start time: {{ localSelectedObject.ncSelectedObject.prettyStartTime }}
              <br>Status: {{ localSelectedObject.ncSelectedObject.status }}
              <br>Location: {{ localSelectedObject.ncSelectedObject.lat }} / {{ localSelectedObject.ncSelectedObject.lon }}
              <br>Radio Type: {{ localSelectedObject.ncSelectedObject.radioStyle }}
              <br>Transmit Power: {{ localSelectedObject.ncSelectedObject.transmitPower }}
              <br>Electrical Power: {{ localSelectedObject.ncSelectedObject.electricalPowerType }}
              <br>Backup Electrical Power: {{ localSelectedObject.ncSelectedObject.backupElectricalPowerType }}
              <br>Last heard: {{ localSelectedObject.ncSelectedObject.prettyLastHeardTime }}
            </Tab>
            <Tab value="Callsign info">
              <div  v-if="((localCallsignObject != null) && (localCallsignObject.value != null))">
                <br>Name: {{ localCallsignObject.value.name }}
                <br>Country: {{ localCallsignObject.value.country }}
                <br>License: {{ localCallsignObject.value.license }}
              </div>
              <div v-else>
                <br>
                <br>
                <i>No callsign information known.</i>
              </div>
            </Tab>
            <Tab value="Actions">
              <div class="grid-container-actions">
                <div v-if="((accesstokenRef.value != null) && (!localSelectedObject.ncSelectedObject.trackingActive))" class="grid-item">
                  <button class="boxButton" v-on:click.native="sendMessage">Send Message</button>
                </div>
                <div v-if="((accesstokenRef.value != null) && (!localSelectedObject.ncSelectedObject.trackingActive))" class="grid-item">
                  Send an APRS message to just this participant.
                </div>
                <div v-if="accesstokenRef.value != null" class="grid-item">
                  <button class="boxButton" v-on:click.native="identify">Identify</button>
                </div>
                <div v-if="accesstokenRef.value != null" class="grid-item">
                  Contact WHO-15 to determine the name, location and license for the callsign.
                </div>
              </div>
            </Tab>
          </Tabs>
        </div>
        <div v-else>
          <br>
          <br>
          <div style="text-align: center;"><i>Click on a participant above for more information.</i></div>
        </div>
      </div>
    </div>
</template>

<style scoped>
.box {
  font-size: 1rem;
  font-weight: 400;

  border: 2px solid black;
  width: 100px;
  height: 30px;
  text-align: center;
  vertical-align: middle;
}
.grid-container-actions {
  display: grid;
  grid-template-columns: 30% 70%;
  margin: 5px;
  gap: 5px;
}
</style>
