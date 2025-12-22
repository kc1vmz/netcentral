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
import MonitorNetSelectPane from '@/components/MonitorNetSelectPane.vue'
import { ref, reactive, watch, onMounted } from 'vue';
import { configShowInfrastructure, configShowObjects, configShowTrackedStationsOnly, configShowPriorityObjects, updateShowInfrastructure, updateShowObjects, updateShowTrackedStationsOnly, updateShowPriorityObjects } from "@/ConfigurationDisplay.js";
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { selectedNet, forceNetRefresh } from "@/SelectedNet.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const showInfrastructureLabel = reactive({ value : "Show Infrastructure" });
const showTrackedStationsLabel = reactive({ value : "Show Tracked Stations Only" });
const showObjectsLabel = reactive({ value : "Show Objects" });
const showPriorityObjectsLabel = reactive({ value : "Show Priority Objects" });
const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
var netNameRef = ref('');
var netCallsignRef = ref('');
var enableButtonRef = ref(false);
var dialogCreateNet = ref(null);
var dialogCreateNetShow = reactive({ value : false });
const accesstokenRef = reactive({ value : '' });
const localLoggedInUserRef = reactive({ value : null });
const netCheckinMessageRef = reactive({ value : '' });
const netExpectedCallsignsRef = reactive({ value : '' });
const netOpenRef = reactive({value : 'true'});
const netParticipantInviteAllowedRef = reactive({value : true});
const netDescriptionRef = reactive({ value : '' });
const netCreatedByRef = reactive({ value : '' });
const netLatitudeRef = reactive({ value : '' });
const netLongitudeRef = reactive({ value : '' });
const netAnnouncedRef = reactive({ value : 'true' });
const netCheckinReminderRef = reactive({ value : 'true' });
const netActiveRef = reactive({ value : 'true' });
const netVoiceFrequencyRef = reactive({ value : '' });
const netTypeRef = reactive({ value : '0' });
const netDayStartRef = reactive({ value : '0' });
const netDurationRef = reactive({ value : '' });
const netTimeStartStrRef = reactive({ value : '' });
const dialogCreateNetShowRef = reactive({ value : false });
const errorMessageRef = reactive({ value : '' });

onMounted(() => {
    accesstokenRef.value = getToken();
    localLoggedInUserRef.value = getUser();
})

function updateLocalSelectedNet(newObject) {
  localSelectedNet.ncSelectedNet = newObject.ncSelectedNet;
}

function toggleShowInfrastructure() {
  updateShowInfrastructure(!(configShowInfrastructure.value));
  if (configShowInfrastructure.value) {
    showInfrastructureLabel.value = "Hide Infrastructure"
  } else {
    showInfrastructureLabel.value = "Show Infrastructure"
  }
}

function toggleShowTrackedStations() {
  updateShowTrackedStationsOnly(!(configShowTrackedStationsOnly.value));
  if (configShowTrackedStationsOnly.value) {
    showTrackedStationsLabel.value = "Show All Stations"
  } else {
    showTrackedStationsLabel.value = "Show Tracked Stations Only"
  }
}

function toggleShowObjects() {
  updateShowObjects(!(configShowObjects.value));
  if (configShowObjects.value) {
    showObjectsLabel.value = "Hide Objects"
  } else {
    showObjectsLabel.value = "Show Objects"
  }
}

function toggleShowPriorityObjects() {
  updateShowPriorityObjects(!(configShowPriorityObjects.value));
  if (configShowPriorityObjects.value) {
    showPriorityObjectsLabel.value = "Hide Priority Objects"
  } else {
    showPriorityObjectsLabel.value = "Show Priority Objects"
  }
}

// Watch for changes in the selected object ref
watch(selectedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
});

watch(netNameRef, (newValue, oldValue) => {
  if ((newValue != '') && (netCallsignRef.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});
watch(netCallsignRef, (newValue, oldValue) => {
  if ((newValue != '') && (netNameRef.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});


function createNet() {
    netCallsignRef.value = '';
    netNameRef.value = '';
    netActiveRef.value = 'true';
    netDescriptionRef.value = '';
    netCheckinMessageRef.value = '';
    netExpectedCallsignsRef.value = '';
    netOpenRef.value = 'true';
    netParticipantInviteAllowedRef.value = 'true';
    netLatitudeRef.value = '';
    netLongitudeRef.value = '';
    netAnnouncedRef.value = 'true';
    netCheckinReminderRef.value = 'true';
    netVoiceFrequencyRef.value = '';
    netTypeRef.value = '0';
    netDayStartRef.value = '0';
    netDurationRef.value = '';
    netTimeStartStrRef.value = '';
    errorMessageRef.value = '';
    dialogCreateNetShow.value = true;
}

function createNetYes() {
    dialogCreateNetShow.value = false;
    // perform the create
    performCreateNet();
}

function createNetNo() {
    dialogCreateNetShow.value = false;
}

function performCreateNetActive() {
    var announce = false;
    if (netAnnouncedRef.value == 'true') {
      announce = true;
    }
    var checkinReminder = false;
    if (netCheckinReminderRef.value == 'true') {
      checkinReminder = true;
    }
    var open = false;
    if (netOpenRef.value == 'true') {
      open = true;
    }
    var inviteAllowed = false;
    if (netParticipantInviteAllowedRef.value == 'true') {
      inviteAllowed = true;
    }
    var bodyObject = { callsign : netCallsignRef.value, name : netNameRef.value, description: netDescriptionRef.value, 
                        voiceFrequency : netVoiceFrequencyRef.value, lat : netLatitudeRef.value, lon : netLongitudeRef.value, 
                        announce : announce , creatorName : localLoggedInUserRef.value.emailAddress, checkinReminder : checkinReminder, checkinMessage: netCheckinMessageRef.value,
                        open: open, participantInviteAllowed: inviteAllowed, expectedCallsigns: netExpectedCallsignsRef.value };
    // active net
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/nets/requests"), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogCreateNetShow.value = false;
            // refetch the net list - poke it
            forceNetRefresh();
        } else {
          errorMessageRef.value = "An error occurred creating the net.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error creating net:', error); })   
}

function performCreateNetScheduled() {
    var announce = false;
    if (netAnnouncedRef.value == 'true') {
      announce = true;
    }
    var checkinReminder = false;
    if (netCheckinReminderRef.value == 'true') {
      checkinReminder = true;
    }
    var open = false;
    if (netOpenRef.value == 'true') {
      open = true;
    }
    var inviteAllowed = false;
    if (netParticipantInviteAllowedRef.value == 'true') {
      inviteAllowed = true;
    }
    var bodyObject = { callsign : netCallsignRef.value, name : netNameRef.value, description: netDescriptionRef.value, 
                        voiceFrequency : netVoiceFrequencyRef.value, lat : netLatitudeRef.value, lon : netLongitudeRef.value, 
                        announce : announce , creatorName : localLoggedInUserRef.value.emailAddress,
                        type: netTypeRef.value,  dayStart: netDayStartRef.value, timeStartStr: netTimeStartStrRef.value, duration: netDurationRef.value, checkinReminder : checkinReminder,
                        checkinMessage: netCheckinMessageRef.value, open: open, participantInviteAllowed: inviteAllowed, expectedCallsigns: netExpectedCallsignsRef.value };

    // scheduled net
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/scheduledNets/requests"), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogCreateNetShow.value = false;
            // refetch the net list - poke it
            forceNetRefresh();
        } else {
          errorMessageRef.value = "An error occurred creating the scheduled net.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error creating net:', error); })   
}

function performCreateNet() {
    if (netActiveRef.value == 'true') {
      performCreateNetActive();
    } else {
      performCreateNetScheduled();
    }
}
</script>

<template>
    <!-- dialogs -->
    <div v-if="dialogCreateNetShow.value">
      <teleport to="#modals">    
        <dialog :open="dialogCreateNetShow.value" ref="dialogCreateNet" @close="dialogCreateNetShow.value = false" class="topz">  
          <form v-if="dialogCreateNetShow.value" method="dialog">
            <div class="pagesubheader">Create Net</div>
            <div class="line"><hr/></div>
            A net is identified on APRS with a callsign and a name.  It can be started now, or scheduled to start at a future time. 
            <br>
              <div>
                <label for="callsignField">Callsign:</label>
                <input type="text" id="callsignField" v-model="netCallsignRef"  maxlength="10"/>
              </div>
              <div>
                <label for="nameField">Name:</label>
                <input type="text" id="nameField" v-model="netNameRef" maxlength="30" />
              </div>
              <div>
                <label for="descriptionField">Description:</label>
                <input type="text" id="descriptionField" v-model="netDescriptionRef.value" maxlength="50" />
              </div>
              <div>
                <label for="checkinMessageField">Check-in message:</label>
                <input type="text" id="checkinMessageField" v-model="netCheckinMessageRef.value" maxlength="50" />
              </div>
              <div>
                <label for="voiceFrequencyField">Voice Frequency:</label>
                <input type="text" id="voiceFrequencyField" v-model="netVoiceFrequencyRef.value" maxlength="20"/>
              </div>
              <div>
                <label for="latitudeField">Latitude:</label>
                <input type="text" id="latitudeField" v-model="netLatitudeRef.value" maxlength="20"/>
              </div>
              <div>
                <label for="longitudeField">Longitude:</label>
                <input type="text" id="longitudeField" v-model="netLongitudeRef.value" maxlength="20"/>
              </div>
              <!-- announced -->
              <div>
                  <label for="announcedField">Announce?</label>
                  <select name="announcedField" id="netAnnounced" v-model="netAnnouncedRef.value" style="display: inline;">
                    <option value="true" selected>Yes</option>
                    <option value="false">No</option>
                  </select>
              </div>
              <div>
                  <label for="reminderCheckoutField">Remind participants to check out?</label>
                  <select name="reminderCheckoutField" id="netCheckinReminder" v-model="netCheckinReminderRef.value" style="display: inline;">
                    <option value="true" selected>Yes</option>
                    <option value="false">No</option>
                  </select>
              </div>
              <div>
                  <label for="openField">Open or closed participation?</label>
                  <select name="openField" id="netOpenRef" v-model="netOpenRef.value" style="display: inline;">
                    <option value="true" selected>Open</option>
                    <option value="false">Closed</option>
                  </select>
              </div>
              <div v-if="netOpenRef.value === 'false'">
                  <label for="participantInviteAllowedField">Allow participant invites?</label>
                  <select name="participantInviteAllowedField" id="netParticipantInviteAllowedRef" v-model="netParticipantInviteAllowedRef.value" style="display: inline;">
                    <option value="true" selected>Yes</option>
                    <option value="false">No</option>
                  </select>
              </div>
              <div>
                <label for="expectedCallsignsField">Expected participant callsigns:</label>
                <input type="text" id="expectedCallsignsField" v-model="netExpectedCallsignsRef.value"/>
              </div>
              <!-- scheduled vs now  -->
              <div>
              <div>
                  <label for="scheduledField">When to start:</label>
                  <select name="scheduledField" id="netActive" v-model="netActiveRef.value" style="display: inline;">
                    <option value="true" selected>Now</option>
                    <option value="false">Later</option>
                  </select>
              </div>
              </div>
              <div v-if="(netActiveRef.value == 'true')">
                <!-- nothing else -->
              </div>
              <div v-else>
                <div>
                    <label for="scheduledTypeField">How often:</label>
                    <select name="scheduledTypeField" id="netType" v-model="netTypeRef.value" style="display: inline;">
                      <option value="1" selected>Daily</option>
                      <option value="2">Weekly</option>
                      <option value="3">Monthly specific date</option>
                      <option value="4">Monthly relative date</option>
                      <option value="5">One time only</option>
                    </select>
                </div>
                <div>
                  <div v-if="(netTypeRef.value != '5')">
                    <label for="dayStartField">Day start:</label>
                    <select name="dayStartField" id="netDayStart" v-model="netDayStartRef.value" style="display: inline;">
                      <option value="1" selected>Sunday</option>
                      <option value="2">Monday</option>
                      <option value="3">Tuesday</option>
                      <option value="4">Wednesday</option>
                      <option value="5">Thursday</option>
                      <option value="6">Friday</option>
                      <option value="7">Saturday</option>
                    </select>
                  </div>
                </div>
                <div>
                  <label for="durationField">Duration (hours):</label>
                  <input type="text" id="durationField" v-model="netDurationRef.value" maxlength="3" />
                </div>
              <div v-if="(netTypeRef.value == '5')">
                <div>
                  <label for="timeStartField">Start time (YYYY-MM-DD HH:MM):</label>
                  <input type="text" id="timeStartField" v-model="netTimeStartStrRef.value" maxlength="16" />
                </div>
              </div>
              <div v-else>
                <div>
                  <label for="timeStartField">Start time (HH:MM):</label>
                  <input type="text" id="timeStartField" v-model="netTimeStartStrRef.value" maxlength="5" />
                </div>
              </div>
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Create new net</button>
            <button class="boxButton" v-if="enableButtonRef" v-on:click.native="createNetYes">Create new net</button>
            <button class="boxButton" v-on:click.native="createNetNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

  <!-- main page -->
  <div class="grid-container">
    <div class="grid-item">
      <div class="pageheader">Net Monitor</div>
    </div>
    <div class="grid-item"></div>
    <div class="grid-item"><MonitorNetSelectPane/></div>
    <div class="grid-item"></div>
    <div class="grid-item">
      <button class="boxButton" v-if="(accesstokenRef.value != null) && (localLoggedInUserRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))" v-on:click.native="createNet">Create New Net</button><br>
    </div>
    <div class="grid-item" v-if="((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign != null) && (localSelectedNet.ncSelectedNet.callsign != ''))">
      <label class="switch">
        <input type="checkbox" v-on:click.native="toggleShowInfrastructure">
        <span class="slider round"></span>
      </label>Show infrastructure
      <br>
      <label class="switch">
        <input type="checkbox" v-on:click.native="toggleShowTrackedStations">
        <span class="slider round"></span>
      </label>Show Tracked Stations
      <br>
      <label class="switch">
        <input type="checkbox" v-on:click.native="toggleShowPriorityObjects">
        <span class="slider round"></span>
      </label>Show Priority Objects
      <br>
      <label class="switch">
        <input type="checkbox" v-on:click.native="toggleShowObjects">
        <span class="slider round"></span>
      </label>Show objects
      <br>
    </div>
  </div>
  <i>Information about APRS nets, participants, messages, questions, answers and related APRS resources.</i>
  <div class="line"><hr/></div>
</template>

<style scoped>
.grid-container {
  display: grid;
  grid-template-columns: 25% 10% 25% 5% 15% 15%;
  margin: 10px;
  gap: 10px;
}

/* The switch - the box around the slider */
.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
  margin: 0px;
  gap: 0px;
}

/* Hide default HTML checkbox */
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

/* The slider */
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}
</style>
