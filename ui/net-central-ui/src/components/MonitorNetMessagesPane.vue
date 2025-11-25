<script setup>
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue, forceNetMessageRefresh, netMessageRefresh } from "@/SelectedNet.js";
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { ref, watch, reactive, onMounted } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateNetMessageEvent, updateNetMessage } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";
import { isMobileClient } from "@/composables/MobileLibrary";


const { socket } = useSocketIO();
socket.on("updateNetMessage", (data) => {
  updateNetMessage(data)
});

watch(updateNetMessageEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((localSelectedNet.ncSelectedNet == null) || ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.completedNetId !== newValue.value.callsign))) {  // callsign really completedNetId
    // not this net
    return;
  }
  if (newValue.value.action == "Create") {
    var found = false;
    if (netMessages.value != null) {
      netMessages.value.forEach(function(message){
        if ((!found) && (message.id == newValue.value.id)) {
          found = true;
        }
      });
      if (found) {
        return;
      }
    } else {
        netMessages.value = [];
    }
    netMessages.value.push(newValue.value.object);
  }
});

const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});

function updateLocalSelectedNet(newObject) {
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

watch(netMessageRefresh, (newSelectedNet, oldSelectedNet) => {
  getMessages(); 
});

const netMessages = reactive({ value : [] });
var messageRef = ref('');
var enableButtonRef = ref(false);

const callsignRef = reactive({value : ''});
const errorMessageRef = reactive({value : ''});
const dialogSendMessageRef = reactive({value : null});
const dialogSendMessageShowRef = reactive({value : false});
const accesstokenRef = reactive({ value : ''});
const localLoggedInUserRef = reactive({ value : {}});


const headers = [
        { text: "Time", value: "prettyReceivedTime", sortable: true },
        { text: "From", value: "callsignFrom", sortable: true},
        { text: "Recipient", value: "recipient", sortable: true},
        { text: "Message", value: "message", sortable: true}];

watch(messageRef, (newValue, oldValue) => {
  if (newValue != '') {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});

watch( localSelectedNet, async () => { getMessages(); }, { immediate: true });

function getMessages() {
  if ((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign != null) && (localSelectedNet.ncSelectedNet.type == null)){
      var requestOptions = {
        method: "GET",
        headers: { "Content-Type": "application/json",
                    "SessionID" : getToken()
          },
        body: null
      };
      fetch(buildNetCentralUrl('/netMessages?completedNetId='+localSelectedNet.ncSelectedNet.completedNetId), requestOptions)
        .then(response => response.json())
        .then(data => {
            netMessages.value = data;
        })
        .catch(error => { console.error('Error getting nets from server:', error); })
    }
}


onMounted(async () => {
    accesstokenRef.value = getToken();
    localLoggedInUserRef.value = getUser();
})

function sendMessage() {
    dialogSendMessageShowRef.value = true;
    callsignRef.value = localSelectedNet.ncSelectedNet.callsign;
    messageRef.value = '';
}

function sendMessageYes() {
      // perform the send message
    performSendMessage();
}

function sendMessageNo() {
    dialogSendMessageShowRef.value = false;
}

function performSendMessage() {
  // message
  const requestOptions = {
    method: "POST",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: messageRef.value
  };
  fetch(buildNetCentralUrl("/nets/"+callsignRef.value+"/messages"), requestOptions)
    .then(response => {
      if (response.status == 200) {
          dialogSendMessageShowRef.value = false;
          forceNetMessageRefresh();
      } else {
        errorMessageRef.value = "An error occurred sending the message.";
      }
      return response.json();
    })
    .then(data => {
    })
    .catch(error => { console.error('Error sending message:', error); })   
}

</script>

<template>
  <!-- dialog -->
  <div v-if="dialogSendMessageShowRef.value">
    <teleport to="#modals">    
      <dialog :open="dialogSendMessageShowRef.value" ref="dialogSendMessageRef" @close="dialogSendMessageShowRef.value = false" class="topz">  
        <form v-if="dialogSendMessageShowRef.value" method="dialog">
          <div class="pagesubheader">Send Message to Net</div>
          <div class="line"><hr/></div>
          Send a message to all net participants.
          <br>
            <div>
              <label for="messageField">Message:</label>
              <input type="text" id="messageField" v-model="messageRef" />
            </div>
            <div>
              <b>{{ errorMessageRef.value }}</b>
            </div>
          <br>
          <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Send</button>
          <button class="boxButton" v-if="enableButtonRef" v-on:click.native="sendMessageYes">Send</button>
          <button class="boxButton" v-on:click.native="sendMessageNo">Cancel</button>
        </form>
      </dialog>
    </teleport>
  </div>

  <!-- main page-->
  <div v-if="((localSelectedNet.ncSelectedNet != null) && (localSelectedNet.ncSelectedNet.callsign != null) && (localSelectedNet.ncSelectedNet.type == null))">
    <div>
      <div v-if="!isMobileClient()" class="pagesubheader">Messages</div>
      <div v-else class="mobilepagesubheader">Messages</div><div v-if="!isMobileClient()"><button class="boxButton" v-on:click.native="sendMessage">Send Message</button></div>
      <div class="line"><hr/></div>
    </div> 
    <div v-if="((netMessages.value == null) || (netMessages.value.length == 0))">
      <br>
      <div style="text-align: center;"><i>No messages.</i></div>
    </div>
    <div v-else>
      <EasyDataTable :headers="headers" :items="netMessages.value" :rows-per-page="10" buttons-pagination/>
      <br><br>
      <div v-if="isMobileClient()"><button class="boxButton" v-on:click.native="sendMessage">Send Message</button></div>
    </div>
  </div>
</template>
