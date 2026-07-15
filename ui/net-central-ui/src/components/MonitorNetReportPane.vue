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
import { isMobileClient } from "@/composables/MobileLibrary";

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
    } else if ((localCallsignObject.value != null) && (localCallsignObject.value.callsign === newValue.value.callsign)) {
      localCallsignObject.value.name = newValue.value.object.name;
      localCallsignObject.value.country = newValue.value.object.country;
      localCallsignObject.value.state = newValue.value.object.state;
      localCallsignObject.value.license = newValue.value.object.license;
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
const localLoggedInUserRef = reactive({ value : null });
const errorMessageRef = reactive({value : ''});

const tacticalCallsignRef = ref('');
const electricalPowerTypeRef = ref('');
const backupElectricalPowerTypeRef = ref('');
const radioStyleRef = ref('');
const transmitPowerRef = ref('');
const voiceFrequencyRef = ref('');
const statusRef = ref('');

var dialogIdentify = ref('');
var dialogIdentifyShow = reactive({value : false});
var dialogSendCallsignMessage = ref('');
var dialogSendCallsignMessageShow = reactive({value : false});
var dialogEditParticipant = ref('');
var dialogEditParticipantShow = reactive({value : false});
var messageRef = ref('');

onMounted(() => {
    accesstokenRef.value = getToken();
    localLoggedInUserRef.value = getUser();
    selectedObject.value = null;
});

function updateLocalSelectedNet(newObject) {
  if (newObject == null) {
    localSelectedNet.ncSelectedNet = null;
  } else {
    localSelectedNet.ncSelectedNet = newObject.ncSelectedNet;
  }
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

function editParticipant() {
    electricalPowerTypeRef.value  = convertElectricalPowerTypeToNumber(localSelectedObject.ncSelectedObject.electricalPowerType);
    backupElectricalPowerTypeRef.value  = convertElectricalPowerTypeToNumber(localSelectedObject.ncSelectedObject.backupElectricalPowerType);
    radioStyleRef.value  = convertRadioStyleToNumber(localSelectedObject.ncSelectedObject.radioStyle);
    transmitPowerRef.value = localSelectedObject.ncSelectedObject.transmitPower;
    tacticalCallsignRef.value = localSelectedObject.ncSelectedObject.tacticalCallsign;
    voiceFrequencyRef.value = localSelectedObject.ncSelectedObject.voiceFrequency;
    statusRef.value = localSelectedObject.ncSelectedObject.status;
    callsignRef.value = localSelectedObject.ncSelectedObject.callsign;
    dialogEditParticipantShow.value = true;
}

function convertElectricalPowerTypeToNumber(val) {
    if (val == null) return null;
    if (val == "COMMERCIAL") return "1";
    if (val == "GENERATOR") return "2";
    if (val == "SOLAR") return "3";
    if (val == "BATTERY") return "4";
    if (val == "OTHER") return "5";
    if (val == "NONE") return "6";
    return "0";
}

function convertRadioStyleToNumber(val) {
    if (val == null) return null;
    if (val == "HANDHELD") return "1";
    if (val == "MOBILE") return "2";
    if (val == "BASE") return "3";
    if (val == "APPLIANCE") return "4";
    if (val == "INTERNET") return "5";
    if (val == "OTHER") return "6";
    return "0";
}

function convertNumberToElectricalPowerType(val) {
    if (val == null) return null;
    if (val == "1") return "COMMERCIAL";
    if (val == "2") return "GENERATOR";
    if (val == "3") return "SOLAR";
    if (val == "4") return "BATTERY";
    if (val == "5") return "OTHER";
    if (val == "6") return "NONE";
    return "UNKNOWN";
}

function convertNumberToRadioStyle(val) {
    if (val == null) return null;
    if (val == "1") return "HANDHELD";
    if (val == "2") return "MOBILE";
    if (val == "3") return "BASE";
    if (val == "4") return "APPLIANCE";
    if (val == "5") return "INTERNET";
    if (val == "6") return "OTHER";
    return "UNKNOWN";
}

function editParticipantYes() {
    dialogEditParticipantShow.value = false;
    // perform the edit participant
    performEditParticipant();
}

function editParticipantNo() {
    dialogEditParticipantShow.value = false;
}

function performEditParticipant() {
    localSelectedObject.ncSelectedObject.electricalPowerType = convertNumberToElectricalPowerType(electricalPowerTypeRef.value);
    localSelectedObject.ncSelectedObject.backupElectricalPowerType = convertNumberToElectricalPowerType(backupElectricalPowerTypeRef.value);
    localSelectedObject.ncSelectedObject.radioStyle = convertNumberToRadioStyle(radioStyleRef.value);
    localSelectedObject.ncSelectedObject.transmitPower = transmitPowerRef.value;
    localSelectedObject.ncSelectedObject.tacticalCallsign = tacticalCallsignRef.value;
    localSelectedObject.ncSelectedObject.voiceFrequency = voiceFrequencyRef.value;
    localSelectedObject.ncSelectedObject.status = statusRef.value;

    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(localSelectedObject.ncSelectedObject)
    };
    fetch(buildNetCentralUrl("/nets/"+localSelectedNet.ncSelectedNet.callsign+"/participants/"+localSelectedObject.ncSelectedObject.callsign), requestOptions)
      .then(response => {
        return response.json();
      })
      .then(data => {
            dialogEditParticipantShow.value = false;
      })
      .catch(error => { console.error('Error editing participant info:', error); })
}

</script>

<template>
  <!-- dialog -->
    <div v-if="dialogEditParticipantShow.value">
      <teleport to="#modals">
        <dialog :open="dialogEditParticipantShow.value" ref="dialogEditParticipant" @close="dialogEditParticipantShow.value = false" class="topz">  
          <form v-if="dialogEditParticipantShow.value" method="dialog">
            <div class="pagesubheader">Edit Participant Information for {{ callsignRef.value }}</div>
            <div class="line"><hr/></div>

              <div>
                <label for="tacticalCallsignField">Tactical callsign:</label>
                <input type="text" id="tacticalCallsignField" v-model="tacticalCallsignRef" />
              </div>
              <div>
                <label for="statusField">Status:</label>
                <input type="text" id="statusField" v-model="statusRef" />
              </div>
              <div>
                <label for="voiceFrequencyField">Voice frequency:</label>
                <input type="text" id="voiceFrequencyField" v-model="voiceFrequencyRef" />
              </div>
              <div>
                <label for="transmitPowerField">Transmit power (watts):</label>
                <input type="number" id="transmitPowerField" v-model="transmitPowerRef" />
              </div>
              <div>
                  <label for="electricalPowerTypeField">Electrical power:</label>
                  <select name="electricalPowerTypeField" id="electricalPowerType" v-model="electricalPowerTypeRef" style="display: inline;">
                    <div v-if="(electricalPowerTypeRef == '0')">
                      <option value="0" selected>Unknown</option>
                    </div>
                    <div v-else>
                      <option value="0">Unknown</option>
                    </div>
                    <div v-if="(electricalPowerTypeRef == '1')">
                      <option value="1" selected>Commercial</option>
                    </div>
                    <div v-else>
                      <option value="1">Commercial</option>
                    </div>
                    <div v-if="(electricalPowerTypeRef == '2')">
                      <option value="2" selected>Generator</option>
                    </div>
                    <div v-else>
                      <option value="2">Generator</option>
                    </div>
                    <div v-if="(electricalPowerTypeRef == '3')">
                      <option value="3" selected>Solar</option>
                    </div>
                    <div v-else>
                      <option value="3">Solar</option>
                    </div>
                    <div v-if="(electricalPowerTypeRef == '4')">
                      <option value="4" selected>Battery</option>
                    </div>
                    <div v-else>
                      <option value="4">Battery</option>
                    </div>
                    <div v-if="(electricalPowerTypeRef == '5')">
                      <option value="5" selected>Other</option>
                    </div>
                    <div v-else>
                      <option value="5">Other</option>
                    </div>
                    <div v-if="(electricalPowerTypeRef == '6')">
                      <option value="6" selected>None</option>
                    </div>
                    <div v-else>
                      <option value="6">None</option>
                    </div>
                  </select>
              </div>
              <div>
                  <label for="backupElectricalPowerTypeField">Backup electrical power:</label>
                  <select name="backupElectricalPowerTypeField" id="backupElectricalPowerType" v-model="backupElectricalPowerTypeRef" style="display: inline;">
                    <div v-if="(backupElectricalPowerTypeRef == '0')">
                      <option value="0" selected>Unknown</option>
                    </div>
                    <div v-else>
                      <option value="0">Unknown</option>
                    </div>
                    <div v-if="(backupElectricalPowerTypeRef == '1')">
                      <option value="1" selected>Commercial</option>
                    </div>
                    <div v-else>
                      <option value="1">Commercial</option>
                    </div>
                    <div v-if="(backupElectricalPowerTypeRef == '2')">
                      <option value="2" selected>Generator</option>
                    </div>
                    <div v-else>
                      <option value="2">Generator</option>
                    </div>
                    <div v-if="(backupElectricalPowerTypeRef == '3')">
                      <option value="3" selected>Solar</option>
                    </div>
                    <div v-else>
                      <option value="3">Solar</option>
                    </div>
                    <div v-if="(backupElectricalPowerTypeRef == '4')">
                      <option value="4" selected>Battery</option>
                    </div>
                    <div v-else>
                      <option value="4">Battery</option>
                    </div>
                    <div v-if="(backupElectricalPowerTypeRef == '5')">
                      <option value="5" selected>Other</option>
                    </div>
                    <div v-else>
                      <option value="5">Other</option>
                    </div>
                    <div v-if="(backupElectricalPowerTypeRef == '6')">
                      <option value="6" selected>None</option>
                    </div>
                    <div v-else>
                      <option value="6">None</option>
                    </div>
                  </select>
              </div>
              <div>
                  <label for="radioStyleField">Radio style:</label>
                  <select name="radioStyleField" id="radioStyle" v-model="radioStyleRef" style="display: inline;">
                    <div v-if="(radioStyleRef == '0')">
                      <option value="0" selected>Unknown</option>
                    </div>
                    <div v-else>
                      <option value="0">Unknown</option>
                    </div>
                    <div v-if="(radioStyleRef == '1')">
                      <option value="1" selected>Handheld</option>
                    </div>
                    <div v-else>
                      <option value="1">Handheld</option>
                    </div>
                    <div v-if="(radioStyleRef == '2')">
                      <option value="2" selected>Mobile</option>
                    </div>
                    <div v-else>
                      <option value="2">Mobile</option>
                    </div>
                    <div v-if="(radioStyleRef == '3')">
                      <option value="3" selected>Base</option>
                    </div>
                    <div v-else>
                      <option value="3">Base</option>
                    </div>
                    <div v-if="(radioStyleRef == '4')">
                      <option value="4" selected>Appliance</option>
                    </div>
                    <div v-else>
                      <option value="4">Appliance</option>
                    </div>
                    <div v-if="(radioStyleRef == '5')">
                      <option value="5" selected>Internet</option>
                    </div>
                    <div v-else>
                      <option value="5">Internet</option>
                    </div>
                    <div v-if="(radioStyleRef == '6')">
                      <option value="6" selected>Other</option>
                    </div>
                    <div v-else>
                      <option value="6">Other</option>
                    </div>
                  </select>
              </div>

            <br>
            <button class="boxButton" v-on:click.native="editParticipantYes">Update</button>
            <button class="boxButton" v-on:click.native="editParticipantNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
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
    <div v-if="(isMobileClient())">
      <div v-if="((localSelectedNet == null) || (localSelectedNet.ncSelectedNet == null) || (localSelectedNet.ncSelectedNet.callsign == null))">
        <!-- no nets -->
      </div>
      <div v-else-if="((localSelectedNet != null) || (localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.type == null))">
        <div class="mobilepagesubheader">Participant Information</div>
        <div class="line"><hr/></div>
        <div v-if="((localSelectedNet != null) && (localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.type == null))">
          <div v-if="((localSelectedObject != null) && (localSelectedObject.ncSelectedObject != null) && (localSelectedObject.ncSelectedObject.callsign != null))">

              <br><b>Callsign:</b><br>{{ localSelectedObject.ncSelectedObject.callsign }}
              <br><b>Tactical Callsign:</b> <br>{{ localSelectedObject.ncSelectedObject.tacticalCallsign }}
              <br><b>Start time:</b> <br>{{ localSelectedObject.ncSelectedObject.prettyStartTime }}
              <br><b>Status:</b> <br>{{ localSelectedObject.ncSelectedObject.status }}
              <br><b>Location:</b> <br>{{ localSelectedObject.ncSelectedObject.lat }} / {{ localSelectedObject.ncSelectedObject.lon }}
              <br><b>Voice Frequency:</b> <br>{{ localSelectedObject.ncSelectedObject.voiceFrequency }}
              <br><b>Radio Type:</b> <br>{{ localSelectedObject.ncSelectedObject.radioStyle }}
              <br><b>Transmit Power:</b> <br>{{ localSelectedObject.ncSelectedObject.transmitPower }}
              <br><b>Electrical Power:</b> <br>{{ localSelectedObject.ncSelectedObject.electricalPowerType }}
              <br><b>Backup Electrical Power:</b> <br>{{ localSelectedObject.ncSelectedObject.backupElectricalPowerType }}
              <br><b>Last heard:</b> <br>{{ localSelectedObject.ncSelectedObject.prettyLastHeardTime }}

              <div  v-if="((localCallsignObject != null) && (localCallsignObject.value != null))">
                <br><b>Name:</b> <br>{{ localCallsignObject.value.name }}
                <br><b>Country:</b> <br>{{ localCallsignObject.value.country }}
                <br><b>License:</b> <br>{{ localCallsignObject.value.license }}
                <br><br>
              </div>
              <div v-else>
                <i>No callsign information known.</i>
              </div>

              <div v-if="((accesstokenRef.value != null) && (!localSelectedObject.ncSelectedObject.trackingActive))">
                <button class="boxButton" v-on:click.native="sendMessage">Send Participant Message</button>
              </div>
              <div v-if="accesstokenRef.value != null">
                <button class="boxButton" v-on:click.native="identify">Identify</button>
              </div>
          </div>
          <div v-else>
            <br>
            <br>
            <div style="text-align: center;"><i>Click on a participant above for more information.</i></div>
          </div>
        </div>
      </div>
    </div>
    <div v-else>
      <div v-if="((localSelectedNet == null) || (localSelectedNet.ncSelectedNet == null) || (localSelectedNet.ncSelectedNet.callsign == null))">
        <!-- no nets -->
      </div>
      <div v-else-if="((localSelectedNet != null) && (localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.type == null))">
        <div v-if="!isMobileClient()" class="pagesubheader">Participant Information</div>
        <div v-else class="mobilepagesubheader">Participant Information</div>
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
                <br>Voice Frequency: {{ localSelectedObject.ncSelectedObject.voiceFrequency }}
                <br>Radio Type: {{ localSelectedObject.ncSelectedObject.radioStyle }}
                <br>Transmit Power: {{ localSelectedObject.ncSelectedObject.transmitPower }}
                <br>Electrical Power: {{ localSelectedObject.ncSelectedObject.electricalPowerType }}
                <br>Backup Electrical Power: {{ localSelectedObject.ncSelectedObject.backupElectricalPowerType }}
                <br>Last heard: {{ localSelectedObject.ncSelectedObject.prettyLastHeardTime }}
              </Tab>
              <Tab value="Callsign info">
                <div  v-if="((localCallsignObject != null) && (localCallsignObject.value != null))">
                  <br>Name: {{ localCallsignObject.value.name }}
                  <br>Location: {{ localCallsignObject.value.country }} / {{ localCallsignObject.value.state }}
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
                  <div v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))" class="grid-item">
                    <button class="boxButton" v-on:click.native="editParticipant">Edit</button>
                  </div>
                  <div v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))" class="grid-item">
                    Edit participant's tactical net data, including tactical call sign, power and electrical systems usage.
                  </div>
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
input {
    border: 1px solid #000;
}
</style>
