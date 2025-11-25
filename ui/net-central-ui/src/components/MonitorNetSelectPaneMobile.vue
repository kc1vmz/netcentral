<script setup>
import { selectedNet , updateSelectedNet, nudgeUpdateNetObject, nudgeUpdateNet, nudgeRemoveNetObject, nudgeRemoveNet, forceNetRefresh, netRefresh } from "@/SelectedNet.js";
import { ref, watch, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateNet, updateNetEvent, updateScheduledNet, updateScheduledNetEvent } from "@/UpdateEvents.js";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";

const { socket } = useSocketIO();
socket.on("updateNet", (data) => {
  updateNet(data)
});
socket.on("updateScheduledNet", (data) => {
  updateScheduledNet(data)
});

watch(updateNetEvent, (newValue, oldValue) => {
  processUpdateNetEvent(newValue);
});
watch(updateScheduledNetEvent, (newValue, oldValue) => {
  processUpdateNetEvent(newValue);
});

watch(selectedNet, (newValue, oldValue) => {
  updateLocalSelectedNet(newValue.ncSelectedNet);
});


function processUpdateNetEvent(newValue) {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if (newValue.value.action == "Create") {
    var found = false;
      if (netsRef.value != null) {
        netsRef.value.forEach(function(net){
          if ((!found) && (newValue.value.objectNow != null) && (net.completedNetId == newValue.value.objectNow.completedNetId)) {
            found = true;
          } else if ((!found) && (newValue.value.objectScheduled != null) && (net.callsign == newValue.value.objectScheduled.callsign)) {
            found = true;
          }
        });
        if (found) {
          return;
        }
      } else {
        netsRef.value = [];
      }
    if (newValue.value.objectNow != null) {
      netsRef.value.push(newValue.value.objectNow);
    } else if (newValue.value.objectScheduled != null) {
      netsRef.value.push(newValue.value.objectScheduled);
    }
    var index = getSelectedCallsignIndex();
    updateIndexes(index);
  } else if (newValue.value.action == "Delete") {
    const indexToRemove = netsRef.value.findIndex(obj => ((obj.callsign == newValue.value.callsign) || (obj.completedNetId == newValue.value.id)));
    if (indexToRemove !== -1) {
      if ((localSelectedNet.ncSelectedNet != null) && ((localSelectedNet.ncSelectedNet.callsign === newValue.value.callsign) || (localSelectedNet.ncSelectedNet.completedNetId === newValue.value.id))) {
        localSelectedNet.ncSelectedNet = null;
        updateLocalSelectedNet(null); 
      }
      netsRef.value.splice(indexToRemove, 1);
    }
    if (netsRef.value.length > 0) {
      var index = getSelectedCallsignIndex();
      updateIndexes(index);
    } else {
      netsRef.value = null;
        localSelectedNet.ncSelectedNet = null;
        updateLocalSelectedNet(null); 
    }
  } else if (newValue.value.action == "Update") {
    if (netsRef.value != null) {
      netsRef.value.forEach(function(net){
        if ((net.callsign == newValue.value.callsign) || (net.completedNetId == newValue.value.id)) {
          if (newValue.value.objectNow != null) {
            net.name = newValue.value.objectNow.name;
            net.description = newValue.value.objectNow.description;
            net.voiceFrequency = newValue.value.objectNow.voiceFrequency;
            net.participantCount = newValue.value.objectNow.participantCount;
          } else if (newValue.value.objectScheduled != null) {
            net.name = newValue.value.objectScheduled.name;
            net.description = newValue.value.objectScheduled.description;
            net.voiceFrequency = newValue.value.objectScheduled.voiceFrequency;
            net.participantCount = newValue.value.objectScheduled.participantCount;
          }
          if ((localSelectedNet.ncSelectedNet != null) && ((localSelectedNet.ncSelectedNet.callsign === newValue.value.callsign) || (localSelectedNet.ncSelectedNet.completedNetId === newValue.value.id))) {
            // if selected, force all the other ref updates
            updateLocalSelectedNet(net); 
          }
        }
      });
    }
  }
}

const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
const netsRef = reactive({value : []});

function getGetRequestOptions() {
  return {
    method: "GET",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: null
  };
}

const netCallsignRef = reactive({ value : ''});
const netTypeRef = reactive({ value : ''});
const netNameRef = reactive({ value : ''});
const netDescriptionRef = reactive({ value : ''});
const netCheckinReminderRef = reactive({ value : 'true'});
const netCreatedByRef = reactive({ value : ''});
const netLatitudeRef = reactive({ value : ''});
const netLongitudeRef = reactive({ value : ''});
const netDurationRef = reactive({ value : ''});
const netDayStartRef = reactive({ value : ''});
const netActiveRef = reactive({ value : ''});
const netStartTimeRef = reactive({ value : ''});
const netTimeStartStrRef = reactive({ value : ''});
const netVoiceFrequencyRef = reactive({ value : ''});
const netAnnouncedRef = reactive({ value : ''});
const netAnnouncedMessageRef = reactive({ value : ''});

const selectedNetIndexRef = reactive({ value : 1});
const previousNetIndexRef = reactive({ value : 0});
const nextNetIndexRef = reactive({ value : 2});
const nextNetCallsignRef = reactive({ value : ''});
const previousNetCallsignRef = reactive({ value : ''});

const accesstokenRef = reactive({ value : ''});
const localLoggedInUserRef = reactive({ value : {}});
const errorMessageRef = reactive({ value : ''});
const netCheckinMessageRef = reactive({ value : ''});


const dialogCloseNetRef = ref(null);
const dialogDeleteNetRef = ref(null);
const dialogEditNetRef = ref(null);

const dialogCloseNetShowRef = reactive({ value : ''});
const dialogDeleteNetShowRef = reactive({ value : ''});
const dialogEditNetShowRef = reactive({ value : ''});

const netName2Ref = reactive({ value : ''});
const netDescription2Ref = reactive({ value : ''});
const netCheckinMessage2Ref = reactive({ value : ''});
const netCallsign2Ref = reactive({ value : ''});
const netStartTime2Ref = reactive({ value : ''});
const netAnnounced2Ref = reactive({ value : ''});
const netVoiceFrequency2Ref = reactive({ value : ''});
const netActive2Ref = reactive({ value : ''});
const netCreatedBy2Ref = reactive({ value : ''});
const netLatitude2Ref = reactive({ value : ''});
const netLongitude2Ref = reactive({ value : ''});
const netType2Ref = reactive({ value : ''});
const netDayStart2Ref = reactive({ value : ''});
const netDuration2Ref = reactive({ value : ''});
const netTimeStartStr2Ref = reactive({ value : ''});

function updateLocalSelectedNet(net) {
  updateSelectedNet(net);
  localSelectedNet.ncSelectedNet = net;
  if ((net != null) && (net.callsign != null)) {
    netCallsignRef.value = net.callsign;
    netNameRef.value = net.name;
    netDescriptionRef.value = net.description;    
    netCheckinMessageRef.value = net.checkinMessage;    
    netCheckinReminderRef.value = net.checkinReminder,
    netCreatedByRef.value = net.creatorName;
    netLatitudeRef.value = net.lat;
    netLongitudeRef.value = net.lon
    netDurationRef.value = net.duration;
    netDayStartRef.value = convertDay(net.dayStart);

    netTypeRef.value = convertType(net.type);
    if (net.type == null) {
      netActiveRef.value = true;
      netStartTimeRef.value = net.prettyStartTime;
    } else {
      // scheduled
      netActiveRef.value = false;
      netStartTimeRef.value = net.prettyNextStartTime;
      if (netTypeRef.value == '5') {
        netTimeStartStrRef.value = net.prettyNextStartTime;
        if (netTimeStartStrRef.value.length > 16) {
            netTimeStartStrRef.value =  netTimeStartStrRef.value.substring(0, 16);
        }
      } else {
        netTimeStartStrRef.value = convertToTime(net.timeStart);
      }
    }
    netVoiceFrequencyRef.value = net.voiceFrequency;
    if (net.announce) { 
      netAnnouncedRef.value = "true";
      if (netActiveRef.value) {
        netAnnouncedMessageRef.value = "Announced";
      } else {
        netAnnouncedMessageRef.value = "To be announced";
      }
    } else {
      netAnnouncedRef.value = "false";
      if (netActiveRef.value) {
        netAnnouncedMessageRef.value = "Not announced";
      } else {
        netAnnouncedMessageRef.value = "Will not be announced";
      }
    }
  }
}

function convertToTime(val) {
    var hoursStr = "";
    var minutesStr = "";
    var hours = ((val/100)*100)/100;
    var minutes = val - (hours*100);
    if (hours < 10) {
      hoursStr = "0"+hours;
    } else {
      hoursStr = ""+hours;
    }
    if (minutes < 10) {
      minutesStr = "0"+minutes;
    } else {
      minutesStr = ""+minutes;
    }
    return hoursStr+":"+minutesStr;
}

function convertDay(val) {
    if (val == null) return null;
    if (val == "SUNDAY") return "1";
    if (val == "MONDAY") return "2";
    if (val == "TUESDAY") return "3";
    if (val == "WEDNESDAY") return "4";
    if (val == "THURSDAY") return "5";
    if (val == "FRIDAY") return "6";
    if (val == "SATURDAY") return "7";
    return "0";
}

function convertType(val) {
    // UNKNOWN, DAILY, WEEKLY, MONTHLY_DAY, MONTHLY_RELATIVE, ONE_TIME_ONLY
    if (val == null) return null;
    if (val == "DAILY") return "1";
    if (val == "WEEKLY") return "2";
    if (val == "MONTHLY_DAY") return "3";
    if (val == "MONTHLY_RELATIVE") return "4";
    if (val == "ONE_TIME_ONLY") return "5";
    return "0";
}

function getNets() {
    var activeNets = [];
    var scheduledNets = [];
    fetch(buildNetCentralUrl('/nets'), getGetRequestOptions())
      .then(response => response.json())
      .then(data => {
          activeNets = data;

          fetch(buildNetCentralUrl('/scheduledNets'), getGetRequestOptions())
            .then(response => response.json())
            .then(data => {
                scheduledNets = data;

                // combine active and scheduled
                netsRef.value = [] ;
                Array.prototype.push.apply(netsRef.value, activeNets); 
                Array.prototype.push.apply(netsRef.value, scheduledNets); 
            })
          .catch(error => { console.error('Error getting nets from server:', error); })

        })
      .catch(error => { console.error('Error getting nets from server:', error); })
}

watch(nudgeUpdateNetObject, (newValue, oldValue) => {
  if (newValue != null) {
    // find the localObject
    if (netsRef.value != null) {
        var found = null;
        netsRef.value.forEach(function(net){
            if (net.callsign == newValue.value.callsign) {
              if (found == null) {
                found = net;
              }
            }
        });
        if (found != null) {
          found.description = newValue.value.description;
          found.voiceFrequency = newValue.value.voiceFrequency;
          updateSelectedNet(found);
        }
    }
  }
});

watch(netRefresh, (newValue, oldValue) => {
  if (newValue != null) {
    getNets();
  }
});

onMounted(async () => {
    accesstokenRef.value = getToken();
    localLoggedInUserRef.value = getUser();
    getData();
})

function getData() {
    updateLocalSelectedNet({callsign:null});
    selectedNetIndexRef.value = -1;
    previousNetIndexRef.value = -1;
    nextNetIndexRef.value = -1;
    previousNetCallsignRef.value = '';
    nextNetCallsignRef.value = '';
    netCreatedByRef.value = '';
    netAnnouncedRef.value ='';
    netVoiceFrequencyRef.value = '';
    netActiveRef.value = true;
    errorMessageRef.value = null;
    getNets();
}

function closeNet() {
    dialogCloseNetShowRef.value = true;
}

function closeNetYes() {
    dialogCloseNetShowRef.value = true;
    performCloseNet();
}

function closeNetNo() {
    dialogCloseNetShowRef.value = false;
}

function performCloseNet() {
    const requestOptions = {
      method: "DELETE",
      headers: { "Content-Type": "application/json",
                  "SessionID" : getToken()
        },
      body: null
    };
    fetch(buildNetCentralUrl("/nets/"+netCallsignRef.value), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogCloseNetShowRef.value = false;
            // refetch the net list
            getNets();
        }
        return response;
      })
      .then(data => {
      })
      .catch(error => { console.error('Error closing net:', error); })
}

function deleteNet() {
    dialogDeleteNetShowRef.value = true;
}

function deleteNetYes() {
    dialogDeleteNetShowRef.value = false;

    // perform the delete
    performDeleteNet();
}

function deleteNetNo() {
    dialogDeleteNetShowRef.value = false;
}

function performDeleteNet() {
    // only scheduled nets get deleted
    const requestOptions = {
      method: "DELETE",
      headers: { "Content-Type": "application/json",
                  "SessionID" : getToken()
        },
      body: null
    };
    fetch(buildNetCentralUrl("/scheduledNets/"+netCallsignRef.value), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogDeleteNetShowRef.value = false;
            // refetch the net list
            getNets();
        }
        return response;
      })
      .then(data => {
      })
      .catch(error => { console.error('Error deleting scheduled net:', error); })
}

function editNet() {
    dialogEditNetShowRef.value = true;
    netName2Ref.value = netNameRef.value;
    netActive2Ref.value = netActiveRef.value;
    netDescription2Ref.value = netDescriptionRef.value;
    netCheckinMessage2Ref.value = netCheckinMessageRef.value;
    netCheckinReminderRef.value = netCheckinReminderRef.value,
    netCallsign2Ref.value  = netCallsignRef.value;
    netAnnounced2Ref.value = netAnnouncedRef.value;
    netVoiceFrequency2Ref.value = netVoiceFrequencyRef.value;
    netStartTime2Ref.value = netStartTimeRef.value;
    netCreatedBy2Ref.value = netCreatedByRef.value;
    netLatitude2Ref.value = netLatitudeRef.value;
    netLongitude2Ref.value = netLongitudeRef.value;
    netType2Ref.value = netTypeRef.value;
    netDayStart2Ref.value = netDayStartRef.value;
    netDuration2Ref.value = netDurationRef.value;
    netTimeStartStr2Ref.value = netTimeStartStrRef.value;
}

function editNetYes() {
    dialogEditNetShowRef.value = false;
    // perform the edit
    performEditNet();
}

function editNetNo() {
    dialogEditNetShowRef.value = false;
}

function performEditNetActive() {
    var bodyObject = { callsign : netCallsignRef.value, name : localSelectedNet.ncSelectedNet.name, description: netDescription2Ref.value, 
                        startTime : localSelectedNet.ncSelectedNet.startTime, completedNetId : localSelectedNet.ncSelectedNet.completedNetId,
                        voiceFrequency : netVoiceFrequency2Ref.value, lat : localSelectedNet.ncSelectedNet.lat, lon : localSelectedNet.ncSelectedNet.lon, 
                        announce : localSelectedNet.ncSelectedNet.announce, checkinReminder: localSelectedNet.ncSelectedNet.checkinReminder, 
                        creatorName : localSelectedNet.ncSelectedNet.creatorName, checkinMessage: netCheckinMessage2Ref.value };
    // active net
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : getToken()
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/nets/"+netCallsignRef.value), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogEditNetShowRef.value = false;
            // refetch the net list
            localSelectedNet.ncSelectedNet.description = netDescription2Ref.value;
            localSelectedNet.ncSelectedNet.voiceFrequency = netVoiceFrequency2Ref.value;
            netDescriptionRef.value = netDescription2Ref.value;
            netVoiceFrequencyRef.value = netVoiceFrequency2Ref.value;
            netCheckinMessageRef.value = netCheckinMessage2Ref.value;
            nudgeUpdateNet(localSelectedNet.ncSelectedNet);
        } else {
          errorMessageRef.value = "An error occurred modifying the net.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error modifying net:', error); })   
}

function performEditNetScheduled() {
    var bodyObject = { callsign : netCallsignRef.value, name : localSelectedNet.ncSelectedNet.name, description: netDescription2Ref.value, 
                        voiceFrequency : netVoiceFrequency2Ref.value, lat : localSelectedNet.ncSelectedNet.lat, lon : localSelectedNet.ncSelectedNet.lon, 
                        announce : localSelectedNet.ncSelectedNet.announce, creatorName : localSelectedNet.ncSelectedNet.creatorName,
                        type: localSelectedNet.ncSelectedNet.type,  dayStart: localSelectedNet.ncSelectedNet.dayStart, 
                        timeStartStr: localSelectedNet.ncSelectedNet.timeStartStr, duration: localSelectedNet.ncSelectedNet.duration,
                        checkinReminder: localSelectedNet.ncSelectedNet.checkinReminder, lastStartTime: localSelectedNet.ncSelectedNet.lastStartTime,
                        nextStartTime : localSelectedNet.ncSelectedNet.nextStartTime, checkinMessage : netCheckinMessage2Ref.value};
    // scheduled net
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : getToken()
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/scheduledNets/"+ netCallsignRef.value), requestOptions)
      .then(response => {
        if (response.status == 200) {
            dialogEditNetShowRef.value = false;
            // refetch the net list
            localSelectedNet.ncSelectedNet.description = netDescription2Ref.value;
            localSelectedNet.ncSelectedNet.voiceFrequency = netVoiceFrequencyRef.value;
            netDescriptionRef.value = netDescription2Ref.value;
            netCheckinMessageRef.value = netCheckinMessage2Ref.value;
            netVoiceFrequencyRef.value = netVoiceFrequency2Ref.value;
            nudgeUpdateNet(localSelectedNet.ncSelectedNet);
        } else {
          errorMessageRef.value = "An error occurred modifying the scheduled net.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error modifying net:', error); })   
}
function performEditNet() {
    if (netActive2Ref.value) {
      performEditNetActive();
    } else {
      performEditNetScheduled();
    }
}

</script>

<template>
<div>
    <!-- main page -->
    <div v-if="((netsRef.value != null) && (netsRef.value.length > 0))">
    <!-- combo box -->
    <select v-model="selectedNet.ncSelectedNet">
        <option v-for="item in netsRef.value" :key="item.callsign" :value="item">
          {{ item.callsign }}
        </option>
      </select>
        <br>
        <br><div class="mobilepagesubheader grid-item">Net {{netCallsignRef.value}}</div>
        <br><b>Name:</b>
        <br>{{netNameRef.value}}
        <br><b>Description:</b>
        <br>{{ netDescriptionRef.value }}
        <br><b>Voice:</b>
        <br>{{ netVoiceFrequencyRef.value }}
        <br><b>Created by:</b>
        <br>{{ netCreatedByRef.value }}
        <div v-if="(netActiveRef.value)">
          <br><b>Started:</b>
          <br>{{ netStartTimeRef.value }}
        </div>
        <div v-else>
          <br><b>Scheduled:</b>
          <br>{{ netStartTimeRef.value }}
        </div>
    </div>
    <div v-else>
      <br>
      <br>
      <div class="mobilepagesubheader">Currently no active or scheduled nets.</div>
    </div>
</div>
</template>

<style scoped>
.grid-container {
  display: grid;
  grid-template-columns: 40% 20% 40%;
}
</style>
