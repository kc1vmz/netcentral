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
import { forceRuleRefresh } from "@/SelectedRule.js";
import { reactive, ref, onMounted, watch } from 'vue';
import 'vue3-easy-data-table/dist/style.css';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateRuleEvent } from "@/UpdateEvents";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";

const localSelectedRule = reactive({ value : { id : null }});
const accesstokenRef = reactive({value : ''});
const loggedInUserLocalRef = reactive({value : null});

const dialogCreateRule = ref(null);
const dialogCreateRuleShow = reactive({ value : false });

const errorMessageRef = reactive({ value : '' });
const ruleSourceRef = reactive({ value : false });
const ruleActionRef = reactive({ value : false });
const ruleValueRef = reactive({ value : false });
const ruleResourceTypeRef = reactive({ value : false });

onMounted(() => {
    accesstokenRef.value = getToken();
    loggedInUserLocalRef.value = getUser();
});

function updateLocalSelectedRule(newRule) {
  localSelectedRule.value = newRule.value;
}

function createRule() {
    ruleSourceRef.value = "";
    ruleActionRef.value = "";
    ruleValueRef.value = "";
    ruleResourceTypeRef.value = "";
    dialogCreateRuleShow.value = true;
}

function createRuleYes() {
    // perform the create
    performCreateRule();
}

function createRuleNo() {
    dialogCreateRuleShow.value = false;
}

function performCreateRule() {
    var bodyObject = { ruleType : ruleActionRef.value, 
                        value : ruleValueRef.value,
                        trackedStationType: ruleResourceTypeRef.value, 
                        ruleTarget: ruleSourceRef.value
                      };
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/trackedStationTypeRules/requests"), requestOptions)
      .then(response => {
        if (response.status == 200) {
            forceRuleRefresh();
            dialogCreateRuleShow.value = false;
        } else {
          errorMessageRef.value = "An error occurred creating the rule.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error creating rule:', error); })   
}
</script>


<template>
    <!-- dialogs -->
    <div v-if="dialogCreateRuleShow.value">
      <teleport to="#modals">    
        <dialog :open="dialogCreateRuleShow.value" ref="dialogCreateRule" @close="dialogCreateRuleShow.value = false" class="topz">  
          <form v-if="dialogCreateRuleShow.value" method="dialog">
            <div class="pagesubheader">Create Rule</div>
            <div class="line"><hr/></div>
            Create a resource assignment rule.
            <br>
              <div>
                <label for="ruleSourceField">Source:</label>
                <select name="ruleSourceField" id="ruleSourceField" v-model="ruleSourceRef.value" >
                  <div v-if="(ruleSourceRef.value == 'CALLSIGN')">
                    <option value="CALLSIGN" selected>Callsign</option>
                  </div>
                  <div v-else>
                    <option value="CALLSIGN">Callsign</option>
                  </div>
                  <div v-if="(ruleSourceRef.value == 'COMMENT')">
                    <option value="COMMENT" selected>Comment</option>
                  </div>
                  <div v-else>
                    <option value="COMMENT">Comment</option>
                  </div>
                  <div v-if="(ruleSourceRef.value == 'STATUS')">
                    <option value="STATUS" selected>Status</option>
                  </div>
                  <div v-else>
                    <option value="STATUS">Status</option>
                  </div>
                  <div v-if="(ruleSourceRef.value == 'SYMBOL')">
                    <option value="SYMBOL" selected>Symbol</option>
                  </div>
                  <div v-else>
                    <option value="SYMBOL">Symbol</option>
                  </div>
                </select>
              </div>
              <div>
                <label for="ruleActionField">Action:</label>
                <select name="ruleActionField" id="ruleActionField" v-model="ruleActionRef.value" >
                  <div v-if="(ruleActionRef.value == 'CONTAINS')">
                    <option value="CONTAINS" selected>Contains</option>
                  </div>
                  <div v-else>
                    <option value="CONTAINS">Contains</option>
                  </div>
                  <div v-if="(ruleActionRef.value == 'ENDS_WITH')">
                    <option value="ENDS_WITH" selected>Ends with</option>
                  </div>
                  <div v-else>
                    <option value="ENDS_WITH">Ends with</option>
                  </div>
                  <div v-if="(ruleActionRef.value == 'EQUALS')">
                    <option value="EQUALS" selected>Equals</option>
                  </div>
                  <div v-else>
                    <option value="EQUALS">Equals</option>
                  </div>
                  <div v-if="(ruleActionRef.value == 'STARTS_WITH')">
                    <option value="STARTS_WITH" selected>Starts with</option>
                  </div>
                  <div v-else>
                    <option value="STARTS_WITH">Starts with</option>
                  </div>
                </select>
              </div>
              <div>
                <label for="ruleValueField">Value:</label>
                <input type="text" id="ruleValueField" v-model="ruleValueRef.value" />
              </div>
              <label for="ruleResourceTypeField">Resource type to assign:</label>
              <select name="ruleResourceTypeField" id="ruleResourceTypeField" v-model="ruleResourceTypeRef.value" >
                <div v-if="(ruleResourceTypeRef.value == 'BBS')">
                  <option value="BBS" selected>Bulletin Board</option>
                </div>
                <div v-else>
                  <option value="BBS">Bulletin Board</option>
                </div>
                <div v-if="(ruleResourceTypeRef.value == 'DSTAR')">
                  <option value="DSTAR" selected>DSTAR</option>
                </div>
                <div v-else>
                  <option value="DSTAR">DSTAR</option>
                </div>
                <div v-if="(ruleResourceTypeRef.value == 'IGATE')">
                  <option value="IGATE" selected>iGate</option>
                </div>
                <div v-else>
                  <option value="IGATE">iGate</option>
                </div>
                <div v-if="(ruleResourceTypeRef.value == 'MMDVM')">
                  <option value="MMDVM" selected>MMDVM</option>
                </div>
                <div v-else>
                  <option value="MMDVM">MMDVM</option>
                </div>
                <div v-if="(ruleResourceTypeRef.value == 'REPEATER')">
                  <option value="REPEATER" selected>Repeater</option>
                </div>
                <div v-else>
                  <option value="REPEATER">Repeater</option>
                </div>
                <div v-if="(ruleResourceTypeRef.value == 'STATION')">
                  <option value="STATION" selected>Station</option>
                </div>
                <div v-else>
                  <option value="STATION">Station</option>
                </div>
                <div v-if="(ruleResourceTypeRef.value == 'WEATHER')">
                  <option value="WEATHER" selected>Weather Station</option>
                </div>
                <div v-else>
                  <option value="WEATHER">Weather Station</option>
                </div>
                <div v-if="(ruleResourceTypeRef.value == 'WINLINK_GATEWAY')">
                  <option value="WINLINK_GATEWAY" selected>Winlink Gateway</option>
                </div>
                <div v-else>
                  <option value="WINLINK_GATEWAY">Winlink Gateway</option>
                </div>
              </select>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="createRuleYes">Create</button>
            <button class="boxButton" v-on:click.native="createRuleNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

    <!-- main page -->
    <br>
    <div>
      Net Central server evaluates APRS data to assign resource types to heard stations.  You can configure those rules here.
      <button class="boxButton" v-if="(accesstokenRef.value != null) && (loggedInUserLocalRef.value != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN'))" v-on:click.native="createRule">Create New Rule</button><br>
    </div>
</template>

<style scoped>
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