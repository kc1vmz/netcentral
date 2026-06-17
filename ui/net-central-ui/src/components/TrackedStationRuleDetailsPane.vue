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
import { selectedRule, updateSelectedRule, ruleRefresh, forceRuleRefresh } from "@/SelectedRule.js";
import { reactive, ref, onMounted, watch } from 'vue';
import 'vue3-easy-data-table/dist/style.css';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateRuleEvent } from "@/UpdateEvents";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";
import { Tabs, Tab } from 'super-vue3-tabs';

const localSelectedRule = reactive({ value : { id : null }});
const accesstokenRef = reactive({value : ''});
const loggedInUserLocalRef = reactive({value : null});

const dialogEditRule = ref(null);
const dialogEditRuleShow = reactive({ value : false });

const dialogDeleteRule = ref(null);
const dialogDeleteRuleShow = reactive({ value : false });
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

// Watch for changes in the selected Rule ref
watch(selectedRule, (newSelectedRule, oldSelectedRule) => {
  updateLocalSelectedRule(newSelectedRule);
});

function deleteRule() {
    dialogDeleteRuleShow.value = true;
}

function deleteRuleYes() {
    // perform the delete
    performDeleteRule();
}

function deleteRuleNo() {
    dialogDeleteRuleShow.value = false;
}

function performDeleteRule() {
    var id = localSelectedRule.value.id;
    const requestOptions = {
      method: "DELETE",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body:null
    };
    fetch(buildNetCentralUrl("/trackedStationTypeRules/"+id), requestOptions)
      .then(response => {
        if (response.status == 200) {
          updateLocalSelectedRule({localSelectedRule : { localSelectedRule: null }});
          forceRuleRefresh();
          dialogDeleteRuleShow.value = false;
        }
        return response;
      })
      .then(data => {
      })
      .catch(error => { console.error('Error deleting rule:', error); })
}

function editRule() {
    ruleSourceRef.value = localSelectedRule.value.ruleTarget;
    ruleActionRef.value = localSelectedRule.value.ruleType;
    ruleValueRef.value = localSelectedRule.value.value;
    ruleResourceTypeRef.value = localSelectedRule.value.trackedStationType;
    dialogEditRuleShow.value = true;
}

function editRuleYes() {
    // perform the edit
    performEditRule();
}

function editRuleNo() {
    dialogEditRuleShow.value = false;
}

function performEditRule() {
    var bodyObject = { ruleType : ruleActionRef.value, 
                        value : ruleValueRef.value,
                        trackedStationType: ruleResourceTypeRef.value, 
                        ruleTarget: ruleSourceRef.value
                      };
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/trackedStationTypeRules/"+localSelectedRule.value.id+"/requests"), requestOptions)
      .then(response => {
        if (response.status == 200) {
            forceRuleRefresh();
            dialogEditRuleShow.value = false;
        } else {
          errorMessageRef.value = "An error occurred modifying the rule.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error modifying rule:', error); })   
}
</script>

<template>
  <!-- dialogs -->
    <div v-if="dialogDeleteRuleShow.value">
      <teleport to="#modals">
        <dialog :open="dialogDeleteRuleShow.value" ref="dialogDeleteRule" @close="dialogDeleteRuleShow.value = false" class="topz">  
          <form v-if="dialogDeleteRuleShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to delete rule {{ localSelectedRule.value.ruleTarget }} {{ localSelectedRule.value.ruleType }} {{ localSelectedRule.value.value }} = {{ localSelectedRule.value.trackedStationType }}?
            <br>
            <button class="boxButton" v-on:click.native="deleteRuleYes">Yes</button>
            <button class="boxButton" v-on:click.native="deleteRuleNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>

    <div v-if="dialogEditRuleShow.value">
      <teleport to="#modals">    
        <dialog :open="dialogEditRuleShow.value" ref="dialogEditRule" @close="dialogEditRuleShow.value = false" class="topz">  
          <form v-if="dialogEditRuleShow.value" method="dialog">
            <div class="pagesubheader">Edit Rule {{ localSelectedRule.value.ruleTarget }} {{ localSelectedRule.value.ruleType }} {{ localSelectedRule.value.value }} = {{ localSelectedRule.value.trackedStationType }}</div>
            <div class="line"><hr/></div>
            Modify the rule information.
            <br>
              <div>
                <label for="ruleSourceField">Source:</label>
                <select name="ruleSourceField" id="ruleSourceField" v-model="ruleSourceRef.value" >
                  <div v-if="(ruleSourceRef.value == 'STATUS')">
                    <option value="STATUS" selected>Status</option>
                    <option value="COMMENT">Comment</option>
                  </div>
                  <div v-else>
                    <option value="STATUS">Status</option>
                    <option value="COMMENT" selected>Comment</option>
                  </div>
                </select>
              </div>
              <div>
                <label for="ruleActionField">Action:</label>
                <select name="ruleActionField" id="ruleActionField" v-model="ruleActionRef.value" >
                  <option value="CONTAINS" selected>Contains</option>
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
            <button class="boxButton" v-on:click.native="editRuleYes">Modify</button>
            <button class="boxButton" v-on:click.native="editRuleNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

    <!-- main page -->
    <div v-if="(localSelectedRule != null) && (localSelectedRule.value != null) && (localSelectedRule.value.id != null)">
      <Tabs>
        <Tab value="Details">
          <br>Source: {{ localSelectedRule.value.ruleTarget }}
          <br>Action: {{ localSelectedRule.value.ruleType }}
          <br>Value: {{ localSelectedRule.value.value }}
          <br>Resource type: {{ localSelectedRule.value.trackedStationType }}

        </Tab>
        <Tab value="Actions">
            <div class="grid-container-actions">
                  <!-- begin pair logged in user or admin -->
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedRule.value != null) && (loggedInUserLocalRef.value != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')))">
                    <button class="boxButton" v-on:click.native="editRule">Edit</button>
                  </div>
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedRule.value != null) && (loggedInUserLocalRef.value != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')))">
                    Edit rule information.
                  </div>
                  <!-- end pair -->

                  <!-- begin pair -->
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedRule.value != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')))">
                    <button class="boxButton" v-on:click.native="deleteRule">Delete</button>
                  </div>
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedRule.value != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')))">
                    Delete the rule from Net Central.
                  </div>
                  <!-- end pair -->
            </div>
        </Tab>
      </Tabs>
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