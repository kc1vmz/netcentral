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
import { getToken, getUser } from "@/LoginInformation.js";
import { reactive, ref, onMounted } from 'vue';
import { buildNetCentralOSMPCUrl } from "@/netCentralOSMPCServerConfig.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

onMounted(() => {
  accesstoken.value = getToken();
  loggedInUserLocalRef.value = getUser();
  getSettings();
  getServerSettings();
});

const dialogConfirmCacheClear = ref(null);
const dialogConfirmCacheClearShow = reactive({ value : false });

var accesstoken = ref('');
const loggedInUserLocalRef = reactive({value : null});
var modeRef = reactive({value : null});
var settingsRef = reactive({value : null});
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
    headers: { "Content-Type": "application/json"
    },
    body: null
  };
  fetch(buildNetCentralOSMPCUrl('/tiles/modes'), requestOptions)
    .then(response => response.json())
    .then(data => {
        modeRef.value = data.mode;
    })
    .catch(error => { console.error('Error getting settings from OSMPC server:', error); })
}

function getServerSettings() {
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
    var requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json"
      },
    };
    fetch(buildNetCentralOSMPCUrl('/tiles/modes/'+modeRef.value), requestOptions)
      .then(response => response.json())
      .then(data => {
          modeRef.value = data.mode;
      })
      .catch(error => { console.error('Error updating settings from OSMPC server:', error); })
}


function updateServerSettings() {
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
      federated: federatedRef.value == 'true',
      federatedPushUdp: federatedPushUdpRef.value,
      federatedPushMessage: federatedPushMessageRef.value,
      federatedInterrogate: federatedInterrogateRef.value,
      configSet: settingsRef.value.configSet,
      netMgrEnabled: aprsNetManagerEnabledRef.value,
      netMgrCallsign: aprsNetManagerCallsignRef.value,
      netMgrLon: aprsNetManagerLonRef.value,
      netMgrLat: aprsNetManagerLatRef.value,
      osmPreCache: (osmPreCacheRef.value == 'true') ? true : false
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
    federatedRef.value = settingsRef.value.federated;
    federatedPushUdpRef.value = settingsRef.value.federatedPushUdp;
    federatedPushMessageRef.value = settingsRef.value.federatedPushMessage;
    federatedInterrogateRef.value = settingsRef.value.federatedInterrogate;
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
    osmPreCacheRef.value = (settingsRef.value.osmPreCache) ? "true" : "false";
}

function refresh() {
  getSettings();
  getServerSettings();
}

function update() {
  updateSettings();
  updateServerSettings();
}

function clearCache() {
    dialogConfirmCacheClearShow.value = true;
}

function clearCacheYes() {
    // perform the cache clear
    performClearCache();
}

function clearCacheNo() {
    dialogConfirmCacheClearShow.value = false;
}

function performClearCache() {
    const requestOptions = {
      method: "DELETE",
      body:null
    };
    fetch(buildNetCentralOSMPCUrl('/tiles'), requestOptions)
      .then(response => {
        if (response.status == 200) {
          dialogConfirmCacheClearShow.value = false;
        }
        return response;
      })
      .then(data => {
      })
      .catch(error => { console.error('Error clearing map cache:', error); })
}

</script>

<template>
  <!-- dialogs -->
  <div v-if="dialogConfirmCacheClearShow.value">
    <teleport to="#modals">
      <dialog :open="dialogConfirmCacheClearShow.value" ref="dialogConfirmCacheClear" @close="dialogConfirmCacheClearShow.value = false" class="topz">  
        <form v-if="dialogConfirmCacheClearShow.value" method="dialog">
          <div class="pagesubheader">Confirm</div>
          <div class="line"><hr/></div>
          Do you wish to clear the map cache ?
          <br>
          <button class="boxButton" v-on:click.native="clearCacheYes">Yes</button>
          <button class="boxButton" v-on:click.native="clearCacheNo">No</button>
        </form>
      </dialog>
    </teleport>
  </div>
  <!-- main page -->
  <div v-if="(modeRef.value != null)">
    <br>
    <div>
      Net Central retrieves map information from an OpenStreetMap server.  It can be configured as a proxy, a cache, or both.
      <br> To use in offline situations, configure as a cache only.  
      <br> Map information must have been previously viewed and cached.
    </div>
    <br>
      <div>
        <label for="osmServerMode">OSM server mode:</label>
        <select name="osmServerMode" id="modeRef" v-model="modeRef.value" style="display: inline;">
          <div v-if="(modeRef.value == 'proxy')">
            <option value="proxy" selected>Proxy-only</option>
          </div>
          <div v-else>
            <option value="proxy">Proxy-only</option>
          </div>
          <div v-if="(modeRef.value == 'cache')">
            <option value="cache" selected>Cache-only</option>
          </div>
          <div v-else>
            <option value="cache">Cache-only</option>
          </div>
          <div v-if="(modeRef.value == 'proxycache')">
            <option value="proxycache" selected>Proxy and cache</option>
          </div>
          <div v-else>
            <option value="proxycache">Proxy and cache</option>
          </div>
        </select>
        </div>
        <div>
          <label for="osmPreCache">Map pre-cache mode:</label>
          <select name="osmPreCache" id="osmPreCache" v-model="osmPreCacheRef.value" style="display: inline;">
            <div v-if="(osmPreCacheRef.value == 'true')">
              <option value="true" selected>Enabled</option>
            </div>
            <div v-else>
              <option value="false">Disabled</option>
            </div>
            <div v-if="(osmPreCacheRef.value == 'false')">
              <option value="true">Enabled</option>
            </div>
            <div v-else>
              <option value="false" selected>Disabled</option>
            </div>
          </select>
        </div>
    <div>
      <br>
      <button class="boxButton" v-on:click.native="refresh">Refresh</button>
      <button class="boxButton" v-on:click.native="update">Update</button>

      <div v-if="((accesstoken != null) && (loggedInUserLocalRef != null) && (loggedInUserLocalRef.value != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')))">
        <br>Administrators are able to clear the cache and allow Net Central to retrieve new map information.
        <br><button class="boxButton" v-on:click.native="clearCache">Clear Map Cache</button>
      </div>
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