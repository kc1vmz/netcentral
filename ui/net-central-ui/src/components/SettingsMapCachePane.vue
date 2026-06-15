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
import { reactive, ref, onMounted } from 'vue';
import { buildNetCentralOSMPCUrl } from "@/netCentralOSMPCServerConfig.js";

onMounted(() => {
  getSettings();
});

var modeRef = reactive({value : null});

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

function refresh() {
  getSettings();
}

function update() {
  updateSettings();
}
</script>

<template>
  <div v-if="(modeRef.value != null)">
    <br>
    <div>
      Net Central retrieves map information from an OpenStreetMap server.  It can be configured as a proxy, a cache, or both.
      <br> To use in offline situations, configure as a cache only.  
      <br> Map information must have been previously viewed and cached.
    </div>
    <br>
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