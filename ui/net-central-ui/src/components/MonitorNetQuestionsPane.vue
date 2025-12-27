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

const { socket } = useSocketIO();
socket.on("updateNetQuestion", (data) => {
  updateNetQuestion(data)
});

watch(updateNetQuestionEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedNet.ncSelectedNet == null) || ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.completedNetId !== newValue.value.callsign))) {  // callsign really completedNetId
    // not this net
    return;
  }
  if (newValue.value.action == "Create") {
    var found = false;
    if (netQuestions.value != null) {
      netQuestions.value.forEach(function(question){
        if ((!found) && (question.netQuestionId == newValue.value.object.netQuestionId)) {
          found = true;
        }
      });
      if (found) {
        return;
      }
    } else {
        netQuestions.value = [];
    }
    netQuestions.value.push(newValue.value.object);
  }
});

function updateLocalSelectedNet(newObject) {
  selectedItem.value = null;
  updateSelectedNetQuestion(null);
  if (newObject == null) {
    localSelectedNet.ncSelectedNet = null;
  } else {
    localSelectedNet.ncSelectedNet = newObject.ncSelectedNet;
  }
}

// Watch for changes in the selected object ref
watch(selectedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
});

watch(netQuestionRefresh, (newValue, oldValue) => {
  getQuestions(); 
});

const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
const selectedItem = ref(null);

const netQuestions = reactive({ value : [] });
var questionTextRef = ref('');
var reminderRef = ref(60);
var enableButtonRef = ref(false);
const errorMessageRef = reactive({value : ''});
const dialogAskQuestionShowRef = reactive({value : false});
const accesstokenRef = reactive({ value : ''});
const localLoggedInUserRef = reactive({ value : {}});


const headers = [
        { text: "Number", value: "number", sortable: true },
        { text: "Question", value: "questionText", sortable: true},
        { text: "Asked Time", value: "prettyAskedTime", sortable: true},
        { text: "Active", value: "active", sortable: true}];

watch(questionTextRef, (newValue, oldValue) => {
  if (newValue != '') {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});

watch( localSelectedNet, async () => { getQuestions(); }, { immediate: true });

function getQuestions() {
  selectedItem.value = null;
  updateSelectedNetQuestion(null);
  if ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign != null) && (localSelectedNet.ncSelectedNet.type == null)){
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      fetch(buildNetCentralUrl('/nets/'+localSelectedNet.ncSelectedNet.callsign+'/questions'), requestOptions)
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

function askQuestion() {
    dialogAskQuestionShowRef.value = true;
    questionTextRef.value = '';
    reminderRef.value = 60;
}

function askQuestionYes() {
      // perform the ask question
    performAskQuestion();
}

function askQuestionNo() {
    dialogAskQuestionShowRef.value = false;
}

function performAskQuestion() {
  var bodyObject = { 
                      completedNetId : localSelectedNet.ncSelectedNet.completedNetId,
                      active : true,
                      reminderMinutes : reminderRef.value,
                      questionText : questionTextRef.value
                  };
  // question create
  const requestOptions = {
    method: "POST",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: JSON.stringify(bodyObject)
  };
  fetch(buildNetCentralUrl('/nets/'+localSelectedNet.ncSelectedNet.callsign+'/questions'), requestOptions)
    .then(response => {
      if (response.status == 200) {
          dialogAskQuestionShowRef.value = false;
          forceNetQuestionRefresh();
      } else {
        errorMessageRef.value = "An error occurred asking the question.";
      }
      return response.json();
    })
    .then(data => {
    })
    .catch(error => { console.error('Error asking question:', error); })   
}

function showRow(item) {
    selectedItem.value = item;
    updateSelectedNetQuestion(item);
}

function getBodyRowClass(item, rowNumber) {
    if ((selectedItem.value != null) && (selectedItem.value.netQuestionId === item.netQuestionId)) {
      return 'bold-row';
    }
    return '';
}

</script>

<template>
  <!-- dialog -->
  <div v-if="dialogAskQuestionShowRef.value">
    <teleport to="#modals">    
      <dialog :open="dialogAskQuestionShowRef.value" ref="dialogAskQuestionShow" @close="dialogAskQuestionShowRef.value = false" class="topz">  
        <form v-if="dialogAskQuestionShowRef.value" method="dialog">
          <div class="pagesubheader">Ask Question</div>
          <div class="line"><hr/></div>
          Ask a question of all net participants.
          <br>
            <div>
              <label for="questionField">Question:</label>
              <input type="text" id="questionField" v-model="questionTextRef" />
            </div>
            <div>
              <label for="reminderField">Reminder (minutes):</label>
              <input type="number" id="reminderField" v-model="reminderRef" min="10"/>
            </div>
            <div>
              <b>{{ errorMessageRef.value }}</b>
            </div>
          <br>
          <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Send</button>
          <button class="boxButton" v-if="enableButtonRef" v-on:click.native="askQuestionYes">Send</button>
          <button class="boxButton" v-on:click.native="askQuestionNo">Cancel</button>
        </form>
      </dialog>
    </teleport>
  </div>

  <!-- main page-->
  <div v-if="((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign != null) && (localSelectedNet.ncSelectedNet.type == null))">
    <div>
      <div v-if="!isMobileClient()" class="pagesubheader">Questions</div>
      <div v-else class="mobilepagesubheader">Questions</div><div v-if="!isMobileClient() && (!localSelectedNet.ncSelectedNet.remote)"><button class="boxButton" v-on:click.native="askQuestion">Ask Question</button></div>
      <div class="line"><hr/></div>
    </div> 
    <div v-if="((netQuestions.value == null) || (netQuestions.value.length == 0))">
      <br>
      <div style="text-align: center;"><i>No questions.</i></div>
    </div>
    <div v-else>
      <EasyDataTable :headers="headers" :items="netQuestions.value" :rows-per-page="10"
        :body-row-class-name="getBodyRowClass"
        @click-row="showRow" 
        buttons-pagination/>
      <br><br>
    </div>
    <div v-if="isMobileClient() && (!localSelectedNet.ncSelectedNet.remote)"><button class="boxButton" v-on:click.native="askQuestion">Ask Question</button></div>
  </div>
</template>
