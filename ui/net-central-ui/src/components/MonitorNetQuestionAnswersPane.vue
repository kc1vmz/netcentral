<script setup>
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue, forceNetQuestionRefresh, netQuestionRefresh, selectedNetQuestion } from "@/SelectedNet.js";
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { ref, watch, reactive, onMounted } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateNetQuestionAnswerEvent, updateNetQuestionAnswer } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";
import { isMobileClient } from "@/composables/MobileLibrary";

const { socket } = useSocketIO();
socket.on("updateNetQuestionAnswer", (data) => {
  updateNetQuestionAnswer(data)
});

watch(selectedNetQuestion, (newValue, oldValue) => {
  localSelectedNetQuestion.value = newValue.value;
});


watch(updateNetQuestionAnswerEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedNetQuestion.value != null) && (localSelectedNetQuestion.value.netQuestionId !== newValue.value.id)) {
    // not this question
    return;
  }
  if (newValue.value.action == "Create") {
    var found = false;
    if (netQuestionAnswers.value != null) {
      netQuestionAnswers.value.forEach(function(question){
        if ((!found) && (question.netQuestionAnswerId == newValue.value.netQuestionAnswerId)) {
          found = true;
        }
      });
      if (found) {
        return;
      }
    } else {
        netQuestionAnswers.value = [];
    }
    netQuestionAnswers.value.push(newValue.value.object);
  }
});

function updateLocalSelectedNet(newObject) {
  if (newObject == null) {
    localSelectedNet.ncSelectedNet = null;
  } else {
    localSelectedNet.ncSelectedNet = newObject.ncSelectedNet;
  }
  netQuestionAnswers.value = null;
  localSelectedNetQuestion.value = null;
}

// Watch for changes in the selected object ref
watch(selectedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
});

const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
const localSelectedNetQuestion = reactive({ value : null });
const netQuestionAnswers = reactive({ value : [] });
const accesstokenRef = reactive({ value : ''});
const localLoggedInUserRef = reactive({ value : {}});

const headers = [
        { text: "Callsign", value: "callsign", sortable: true },
        { text: "Answer", value: "answerText", sortable: true},
        { text: "Answered Time", value: "prettyAnsweredTime", sortable: true}];


watch( localSelectedNetQuestion, async () => { getAnswers(); }, { immediate: true });

function getAnswers() {
  if (localSelectedNetQuestion.value != null) {
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      fetch(buildNetCentralUrl('/nets/'+localSelectedNet.ncSelectedNet.callsign+'/questions/'+localSelectedNetQuestion.value.netQuestionId+"/answers"), requestOptions)
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
  <div v-if="((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign != null) && (localSelectedNet.ncSelectedNet.type == null))">
    <div>
      <div v-if="!isMobileClient()" class="pagesubheader">Answers</div>
      <div v-else class="mobilepagesubheader">Answers</div><div v-if="!isMobileClient()"></div>
      <div class="line"><hr/></div>
    </div> 
    <div v-if="((netQuestionAnswers.value == null) || (netQuestionAnswers.value.length == 0))">
      <br>
      <div style="text-align: center;"><i>No answers.</i></div>
    </div>
    <div v-else>
      <EasyDataTable :headers="headers" :items="netQuestionAnswers.value" :rows-per-page="10" buttons-pagination/>
      <br><br>
      <div v-if="isMobileClient()"></div>
    </div>
  </div>
</template>
