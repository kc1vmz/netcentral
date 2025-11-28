<script setup>
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue, forceNetQuestionRefresh, netQuestionRefresh, selectedNetQuestion } from "@/SelectedNet.js";
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { ref, watch, reactive, onMounted } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateNetQuestionAnswerEvent, updateNetQuestionAnswer } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";
import { isMobileClient } from "@/composables/MobileLibrary";
import { selectedCompletedNet, selectedCompletedNetQuestion } from "@/SelectedCompletedNet.js";


const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
const localSelectedCompletedNetQuestion = reactive({ value : null });
const netQuestionAnswers = reactive({ value : [] });
const accesstokenRef = reactive({ value : ''});
const localLoggedInUserRef = reactive({ value : {}});

const headers = [
        { text: "Callsign", value: "callsign", sortable: true },
        { text: "Answer", value: "answerText", sortable: true},
        { text: "Answered Time", value: "prettyAnsweredTime", sortable: true}];


watch( localSelectedCompletedNetQuestion, async () => { getAnswers(); }, { immediate: true });

function updateLocalSelectedNet(newObject) {
  if (newObject == null) {
    localSelectedNet.ncSelectedNet = null;
  } else {
    localSelectedNet.ncSelectedNet = newObject.value;
  }
  netQuestionAnswers.value = null;
  localSelectedCompletedNetQuestion.value = null;
}

// Watch for changes in the selected object ref
watch(selectedCompletedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
});
watch(selectedCompletedNetQuestion, (newValue, oldValue) => {
  localSelectedCompletedNetQuestion.value = newValue.value;
});

function getAnswers() {
  if (localSelectedCompletedNetQuestion.value != null) {
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      fetch(buildNetCentralUrl('/completedNets/'+localSelectedNet.ncSelectedNet.completedNetId+'/questions/'+localSelectedCompletedNetQuestion.value.netQuestionId+"/answers"), requestOptions)
        .then(response => response.json())
        .then(data => {
            netQuestionAnswers.value = data;
        })
        .catch(error => { console.error('Error getting answers from server:', error); })
    }
}

onMounted(async () => {
    accesstokenRef.value = getToken();
    localLoggedInUserRef.value = getUser();
})

</script>

<template>
  <!-- main page-->
  <div v-if="((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.completedNetId != null) && (localSelectedNet.ncSelectedNet.type == null))">
    <div v-if="((netQuestionAnswers.value == null) || (netQuestionAnswers.value.length == 0))">
      <div v-if="(localSelectedCompletedNetQuestion.value != null)">
        <br>
        <div><i>No answers to this question.</i></div>
      </div>
    </div>
    <div v-else>
      <div v-if="!isMobileClient()" class="pagesubheader">Answers</div>
      <div v-else class="mobilepagesubheader">Answers</div><div v-if="!isMobileClient()"></div>
      <div class="line"><hr/></div>
      <EasyDataTable :headers="headers" :items="netQuestionAnswers.value" :rows-per-page="10" buttons-pagination/>
      <br><br>
      <div v-if="isMobileClient()"></div>
    </div>
  </div>
</template>
