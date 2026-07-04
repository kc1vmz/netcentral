<!--
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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
import { getToken } from "@/LoginInformation.js";
import { reactive, ref, onMounted } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

onMounted(() => {
  accesstoken.value = getToken()
  getSettings();
});

var settingsRef = reactive({value : null});
var accesstoken = ref('');
var objectBeaconMinutesRef = reactive({value : null});
var objectCleanupMinutesRef = reactive({value : null});
var reportCleanupMinutesRef = reactive({value : null});
var scheduledNetCheckMinutesRef = reactive({value : null});
var netParticipantReminderMinutesRef = reactive({value : null});
var netReportMinutesRef = reactive({value : null});
var bulletinAnnounceRef = reactive({value : null});
var mapDefaultLatitudeMinRef = reactive({value : null});
var mapDefaultLongitudeMinRef = reactive({value : null});
var mapDefaultLatitudeMaxRef = reactive({value : null});
var mapDefaultLongitudeMaxRef = reactive({value : null});
var federatedRef = reactive({value : null});
var federatedPushUdpRef = reactive({value : null});
var federatedPushMessageRef = reactive({value : null});
var federatedInterrogateRef = reactive({value : null});
var aprsNetManagerEnabledRef = reactive({value : null});
var aprsNetManagerCallsignRef = reactive({value : null});
var aprsNetManagerLonRef = reactive({value : null});
var aprsNetManagerLatRef = reactive({value : null});
var osmPreCacheRef = reactive({value : null});


function getSettings() {
  var requestOptions = {
    method: "GET",
    headers: { "Content-Type": "application/json",
                "SessionID" : accesstoken.value
    },
    body: null
  };
  fetch(buildNetCentralUrl('/configurations'), requestOptions)
    .then(response => response.json())
    .then(data => {
        settingsRef.value = data;
        updateRefs();
    })
    .catch(error => { console.error('Error getting settings from server:', error); })
}

function updateSettings() {
    var bodyObject = { 
      objectCleanupMinutes: objectCleanupMinutesRef.value,
      objectBeaconMinutes: objectBeaconMinutesRef.value,
      reportCleanupMinutes: reportCleanupMinutesRef.value,
      scheduledNetCheckMinutes: scheduledNetCheckMinutesRef.value,
      netParticipantReminderMinutes: netParticipantReminderMinutesRef.value,
      netReportMinutes: netReportMinutesRef.value,
      bulletinAnnounce: bulletinAnnounceRef.value,
      mapDefaultLatitudeMin: mapDefaultLatitudeMinRef.value,
      mapDefaultLongitudeMin: mapDefaultLongitudeMinRef.value,
      mapDefaultLatitudeMax: mapDefaultLatitudeMaxRef.value,
      mapDefaultLongitudeMax: mapDefaultLongitudeMaxRef.value,
      federated: (federatedRef.value == 'true') ? true : false,
      federatedPushUdp: (federatedPushUdpRef.value == 'true') ? true : false,
      federatedPushMessage: (federatedPushMessageRef.value == 'true') ? true : false,
      federatedInterrogate: (federatedInterrogateRef.value == 'true') ? true : false,
      configSet: settingsRef.value.configSet,
      netMgrEnabled: aprsNetManagerEnabledRef.value,
      netMgrCallsign: aprsNetManagerCallsignRef.value,
      netMgrLon: aprsNetManagerLonRef.value,
      netMgrLat: aprsNetManagerLatRef.value,
      osmPreCache: osmPreCacheRef.value
    };
    var requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstoken.value
      },
        body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl('/configurations'), requestOptions)
      .then(response => response.json())
      .then(data => {
          settingsRef.value = data;
          updateRefs();
      })
      .catch(error => { console.error('Error updating settings from server:', error); })
}

function updateRefs() {
    bulletinAnnounceRef.value = settingsRef.value.bulletinAnnounce;
    federatedRef.value = (settingsRef.value.federated) ? "true" : "false";
    federatedPushUdpRef.value = (settingsRef.value.federatedPushUdp) ? "true" : "false";
    federatedPushMessageRef.value = (settingsRef.value.federatedPushMessage) ? "true" : "false";
    federatedInterrogateRef.value = (settingsRef.value.federatedInterrogate) ? "true" : "false";
    mapDefaultLatitudeMinRef.value = settingsRef.value.mapDefaultLatitudeMin;
    mapDefaultLongitudeMinRef.value = settingsRef.value.mapDefaultLongitudeMin;
    mapDefaultLatitudeMaxRef.value = settingsRef.value.mapDefaultLatitudeMax;
    mapDefaultLongitudeMaxRef.value = settingsRef.value.mapDefaultLongitudeMax;
    objectBeaconMinutesRef.value = settingsRef.value.objectBeaconMinutes;
    objectCleanupMinutesRef.value = settingsRef.value.objectCleanupMinutes;
    reportCleanupMinutesRef.value = settingsRef.value.reportCleanupMinutes;
    scheduledNetCheckMinutesRef.value = settingsRef.value.scheduledNetCheckMinutes;
    netParticipantReminderMinutesRef.value = settingsRef.value.netParticipantReminderMinutes;
    netReportMinutesRef.value = settingsRef.value.netReportMinutes;
    aprsNetManagerEnabledRef.value = settingsRef.value.netMgrEnabled;
    aprsNetManagerCallsignRef.value = settingsRef.value.netMgrCallsign;
    aprsNetManagerLonRef.value = settingsRef.value.netMgrLon;
    aprsNetManagerLatRef.value = settingsRef.value.netMgrLat;
    osmPreCacheRef.value = settingsRef.value.osmPreCache;
}

function refresh() {
  getSettings();
}

function update() {
  updateSettings();
}
</script>

<template>
  <div v-if="(settingsRef.value != null)">
    <br>
    <div>
      Net Central can be configured for share information with other Net Central instances in a federated way.  Those settings can be changed here.
    </div>
    <br>
    <div class="field-group">
        <label for="federated">Enabled?</label>
        <select name="federated" id="federated" v-model="federatedRef.value" >
          <div v-if="(federatedRef.value == 'true')">
            <option value="true" selected>Yes</option>
            <option value="false">No</option>
          </div>
          <div v-else>
            <option value="true">Yes</option>
            <option value="false" selected>No</option>
          </div>
        </select>
    </div>
    <div v-if="(federatedRef.value == 'true')" class="field-group">
        <label for="federatedPushMessage">Send messages?</label>
        <select name="federatedPushMessage" id="federatedPushMessage" v-model="federatedPushMessageRef.value" >
          <div v-if="(federatedPushMessageRef.value == 'true')">
            <option value="true" selected>Yes</option>
            <option value="false">No</option>
          </div>
          <div v-else>
            <option value="true">Yes</option>
            <option value="false" selected>No</option>
          </div>
        </select>
    </div>
    <div v-if="(federatedRef.value == 'true')" class="field-group">
        <label for="federatedPushUdp">Send user-defined packets?</label>
        <select name="federatedPushUdp" id="federatedPushUdp" v-model="federatedPushUdpRef.value" >
          <div v-if="(federatedPushUdpRef.value == 'true')">
            <option value="true" selected>Yes</option>
            <option value="false">No</option>
          </div>
          <div v-else>
            <option value="true">Yes</option>
            <option value="false" selected>No</option>
          </div>
        </select>
    </div>
    <div v-if="(federatedRef.value == 'true')" class="field-group">
        <label for="federatedInterrogate">Interrogate objects?</label>
        <select name="federatedInterrogate" id="federatedInterrogate" v-model="federatedInterrogateRef.value" >
          <div v-if="(federatedInterrogateRef.value == 'true')">
            <option value="true" selected>Yes</option>
            <option value="false">No</option>
          </div>
          <div v-else>
            <option value="true">Yes</option>
            <option value="false" selected>No</option>
          </div>
        </select>
    </div>
    <div>
      <br>
      <button class="boxButton" v-on:click.native="refresh">Refresh</button>
      <button class="boxButton" v-on:click.native="update">Update</button>
    </div>
</div>
</template>

<style>
.field-group {
  margin-bottom: 15px; /* Adds space between different form groups */
}

.field-group label {
  margin-bottom: 5px; /* Adds a small margin between the label and the input field */
  width: 30%; /* Makes the input field fill the width of its container */
}

.field-group input {
  width: 10%; /* Makes the input field fill the width of its container */
  box-sizing: border-box; /* Ensures padding/border doesn't add to the width */
}

input {
    border: 1px solid #000;
}

</style>