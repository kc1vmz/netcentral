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
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue, forceNetQuestionRefresh, netQuestionRefresh, updateSelectedNetQuestion } from "@/SelectedNet.js";
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { ref, watch, reactive, onMounted } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateNetQuestionEvent, updateNetQuestion } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";
import { isMobileClient } from "@/composables/MobileLibrary";
import { selectedCompletedNet, selectedCompletedNetQuestion, updateSelectedCompletedNetQuestion } from "@/SelectedCompletedNet.js";

const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
const localSelectedCompletedNetQuestion = reactive({value : null});

const netQuestions = reactive({ value : [] });
var questionTextRef = ref('');
var reminderRef = ref(60);
var enableButtonRef = ref(false);
const dialogAskQuestionShowRef = reactive({value : false});
const accesstokenRef = reactive({ value : ''});
const localLoggedInUserRef = reactive({ value : {}});


const headers = [
        { text: "Number", value: "number", sortable: true },
        { text: "Question", value: "questionText", sortable: true},
        { text: "Asked Time", value: "prettyAskedTime", sortable: true},
        { text: "Active", value: "active", sortable: true}];

function updateLocalSelectedNet(newObject) {
  localSelectedCompletedNetQuestion.value = null;
  updateSelectedNetQuestion(null);
  if (newObject == null) {
    localSelectedNet.ncSelectedNet = null;
  } else {
    localSelectedNet.ncSelectedNet = newObject.value;
  }
  getQuestions();
}

// Watch for changes in the selected object ref
watch(selectedCompletedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
  updateSelectedCompletedNetQuestion(null);
});

watch(localSelectedCompletedNetQuestion, (newValue, oldValue) => {
  updateSelectedCompletedNetQuestion(localSelectedCompletedNetQuestion);
});


watch(netQuestionRefresh, (newValue, oldValue) => {
  getQuestions(); 
});


function getQuestions() {
  localSelectedCompletedNetQuestion.value = null;
  updateSelectedNetQuestion(null);
  if ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.completedNetId != null)) {
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      fetch(buildNetCentralUrl('/completedNets/'+localSelectedNet.ncSelectedNet.completedNetId+'/questions'), requestOptions)
        .then(response => response.json())
        .then(data => {
            netQuestions.value = data;
        })
        .catch(error => { console.error('Error getting questions from server:', error); })
    }
}

onMounted(async () => {
    accesstokenRef.value = getToken();
    localLoggedInUserRef.value = getUser();
})

function showRow(item) {
    localSelectedCompletedNetQuestion.value = item;
    updateSelectedNetQuestion(item);
}

function getBodyRowClass(item, rowNumber) {
    if ((localSelectedCompletedNetQuestion.value != null) && (localSelectedCompletedNetQuestion.value.netQuestionId === item.netQuestionId)) {
      return 'bold-row';
    }
    return '';
}

</script>

<template>
  <!-- main page-->
  <div v-if="((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.completedNetId != null))">
    <div v-if="((netQuestions.value == null) || (netQuestions.value.length == 0))">
      <br>
      <div><i>No questions during this net.</i></div>
    </div>
    <div v-else>
      <div v-if="!isMobileClient()" class="pagesubheader">Questions</div>
      <div v-else class="mobilepagesubheader">Questions</div><div v-if="!isMobileClient()"></div>
      <div class="line"><hr/></div>
      <EasyDataTable :headers="headers" :items="netQuestions.value" :rows-per-page="10"
        :body-row-class-name="getBodyRowClass"
        @click-row="showRow" 
        buttons-pagination/>
      <br><br>
    </div>
  </div>
</template>
