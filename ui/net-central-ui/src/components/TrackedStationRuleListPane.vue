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
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser } from "@/LoginInformation.js";
import { updateSelectedRule, ruleRefresh, forceRuleRefresh } from "@/SelectedRule.js";
import { reactive, ref, onMounted, watch } from 'vue';
import 'vue3-easy-data-table/dist/style.css';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateRuleEvent } from "@/UpdateEvents";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";

const { socket } = useSocketIO();
socket.on("updateRule", (data) => {
  updateRule(data)
});

function updateRule(data) {
    updateRuleEvent.value = JSON.parse(data);
}

onMounted(() => {
  accesstoken.value = getToken()
  selectedItem.value = null;
  updateLocalSelectedRule({value : { id : null }});
  getRules();
});

const localSelectedRule = reactive({value : { id : null }});
const selectedItem = ref(null);

var rulesRef = reactive({value : []});
var accesstoken = ref('');

watch(ruleRefresh, (newValue, oldValue) => {
  getRules();
});

function updateLocalSelectedRule(newRule) {
    localSelectedRule.value = newRule.value;
    updateSelectedRule(newRule);
}

watch(updateRuleEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((newValue.value.action == "Create") || (newValue.value.action == "Delete") || (newValue.value.action == "Update")) {
    getRules();
  }
});


function getRules() {
  var requestOptions = {
    method: "GET",
    headers: { "Content-Type": "application/json",
                "SessionID" : accesstoken.value
    },
    body: null
  };
  fetch(buildNetCentralUrl('/trackedStationTypeRules'), requestOptions)
    .then(response => response.json())
    .then(data => {
        rulesRef.value = data;

        // find the previously selected list item and update it for everyone else
        if (rulesRef.value != null) {
          rulesRef.value.forEach(function(rule){
              if (rule.id == localSelectedRule.value.id) {
                updateSelectedRule({ value : rule});
              }
          });
        }
    })
    .catch(error => { console.error('Error getting rules from server:', error); })
}

function showRow(item) {
    selectedItem.value = item;
    updateLocalSelectedRule({ value : item }); 
}

function getBodyRowClass(item, rowNumber) {
    if ((selectedItem.value != null) && (selectedItem.value.id === item.id)) {
      return 'bold-row';
    }
    return '';
}

const headers = [
              { text: "Source", value: "ruleTarget", sortable: true },
              { text: "Action", value: "ruleType", sortable: true},
              { text: "Value", value: "value", sortable: true},
              { text: "Station Type", value: "trackedStationType", sortable: true},
            ];

</script>

<template>
    <br>
    <EasyDataTable :headers="headers" :items="rulesRef.value" 
    :rows-per-page="10"
    @click-row="showRow"
    :body-row-class-name="getBodyRowClass" buttons-pagination
    />
</template>
