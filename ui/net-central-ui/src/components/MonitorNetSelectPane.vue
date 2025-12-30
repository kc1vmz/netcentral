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
const netOpenRef = reactive({ value : true});
const netParticipantInviteAllowedRef = reactive({ value : true});
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
const netExpectedCallsignsRef = reactive({ value : ''});
const netExpectedCallsigns2Ref = reactive({ value : ''});
const netRemoteRef = reactive({ value : false});

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
  netExpectedCallsignsRef.value = '';
  if ((net != null) && (net.callsign != null)) {
    getExpectedParticipants(net.callsign);
    netCallsignRef.value = net.callsign;
    netNameRef.value = net.name;
    netDescriptionRef.value = net.description;    
    netCheckinMessageRef.value = net.checkinMessage;    
    netCheckinReminderRef.value = net.checkinReminder;
    netOpenRef.value = net.open;
    netParticipantInviteAllowedRef.value = net.participantInviteAllowed;
    netCreatedByRef.value = net.creatorName;
    netLatitudeRef.value = net.lat;
    netLongitudeRef.value = net.lon
    netDurationRef.value = net.duration;
    netDayStartRef.value = convertDay(net.dayStart);
    netRemoteRef.value = net.remote;

    netTypeRef.value = convertType(net.type);
    if (net.type == null) {
      netActiveRef.value = true;
      netStartTimeRef.value = net.prettyStartTime;
    } else {
      // scheduled
      netActiveRef.value = false;
      netRemoteRef.value = false;
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
    if (net.remote) {
        netAnnouncedMessageRef.value = "Remote Net";
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

                if (netsRef.value.length > 0) {
                  var index = getSelectedCallsignIndex();
                  updateIndexes(index);
                }
            })
          .catch(error => { console.error('Error getting nets from server:', error); })

        })
      .catch(error => { console.error('Error getting nets from server:', error); })
}

function getExpectedParticipants(netCallsign) {
    var expectedParticipants = [];
    var callsigns = '';
    fetch(buildNetCentralUrl('/nets/'+netCallsign+'/expectedParticipants'), getGetRequestOptions())
      .then(response => response.json())
      .then(data => {
          expectedParticipants = data;
          if (expectedParticipants) {
            expectedParticipants.forEach(function(expectedParticipant){
              if (callsigns.length > 0) {
                callsigns += ',';
              }
              callsigns += expectedParticipant.callsign;
            });
          }
          netExpectedCallsignsRef.value = callsigns;
      })
      .catch(error => { console.error('Error getting expected net participants from server:', error); })
}

function getSelectedCallsignIndex() {
  var ret = 0;
  var index = 0;
  const callsign = localStorage.getItem('NetCentral-net-selected-callsign');
  if (callsign) {
    netsRef.value.forEach(function(net){
        if (net.callsign == callsign) {
          ret = index;
        }
        index++;
    });
  }

  return ret;
}

function updateIndexes(value) {
    updateLocalSelectedNet(netsRef.value[value]);
    selectedNetIndexRef.value = value;
    previousNetIndexRef.value = getPreviousNetIndex();
    nextNetIndexRef.value = getNextNetIndex();
    previousNetCallsignRef.value = netsRef.value[previousNetIndexRef.value].callsign;
    nextNetCallsignRef.value = netsRef.value[nextNetIndexRef.value].callsign;
}

function getNextNetIndex() {
    var len = netsRef.value.length;
    var ret = selectedNetIndexRef.value+1;
    if (ret >= len) {
      ret = 0;
    }
    return ret;
}

function getPreviousNetIndex() {
    var len = netsRef.value.length;
    var ret = selectedNetIndexRef.value-1;
    if (ret < 0) {
      ret = (len - 1);
    }
    return ret;
}

function goNetPrevious() {
    selectedNetIndexRef.value = previousNetIndexRef.value;
    updateLocalSelectedNet(netsRef.value[selectedNetIndexRef.value]);
    previousNetIndexRef.value = getPreviousNetIndex();
    nextNetIndexRef.value = getNextNetIndex();
    previousNetCallsignRef.value = netsRef.value[previousNetIndexRef.value].callsign;
    nextNetCallsignRef.value = netsRef.value[nextNetIndexRef.value].callsign;
    localStorage.setItem('NetCentral-net-selected-callsign', netsRef.value[selectedNetIndexRef.value].callsign);
}

function goNetNext() {
    selectedNetIndexRef.value = nextNetIndexRef.value;
    updateLocalSelectedNet(netsRef.value[selectedNetIndexRef.value]);
    previousNetIndexRef.value = getPreviousNetIndex();
    nextNetIndexRef.value = getNextNetIndex();
    previousNetCallsignRef.value = netsRef.value[previousNetIndexRef.value].callsign;
    nextNetCallsignRef.value = netsRef.value[nextNetIndexRef.value].callsign;
    localStorage.setItem('NetCentral-net-selected-callsign', netsRef.value[selectedNetIndexRef.value].callsign);
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
    netRemoteRef.value = false;
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
    if (netRemoteRef.value) {
      return;
    }

    dialogEditNetShowRef.value = true;
    netName2Ref.value = netNameRef.value;
    netActive2Ref.value = netActiveRef.value;
    netDescription2Ref.value = netDescriptionRef.value;
    netCheckinMessage2Ref.value = netCheckinMessageRef.value;
//    netOpenRef.value = net.open;
    netExpectedCallsigns2Ref.value = netExpectedCallsignsRef.value;
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
                        creatorName : localSelectedNet.ncSelectedNet.creatorName, checkinMessage: netCheckinMessage2Ref.value,
                        open: localSelectedNet.ncSelectedNet.open, participantInviteAllowed: localSelectedNet.ncSelectedNet.participantInviteAllowed  };
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
                        nextStartTime : localSelectedNet.ncSelectedNet.nextStartTime, checkinMessage : netCheckinMessage2Ref.value,
                        open: localSelectedNet.ncSelectedNet.open, participantInviteAllowed: localSelectedNet.ncSelectedNet.participantInviteAllowed };
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
      if (netExpectedCallsigns2Ref.value != netExpectedCallsignsRef.value) {
        performEditNetExpectedParticipantsActive();
      }

    } else {
      performEditNetScheduled();
      if (netExpectedCallsigns2Ref.value != netExpectedCallsignsRef.value) {
        performEditNetExpectedParticipantsScheduled();
      }
    }
}

function performEditNetExpectedParticipants(res) {
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json",
                  "SessionID" : getToken()
        },
      body: JSON.stringify(netExpectedCallsigns2Ref.value)
    };
    fetch(buildNetCentralUrl("/"+res+"/"+netCallsignRef.value+"/expectedParticipants/requests"), requestOptions)
      .then(response => {
        if (response.status == 200) {
          netExpectedCallsignsRef.value = netExpectedCallsigns2Ref.value;
          var net = localSelectedNet.ncSelectedNet;
          updateSelectedNet(null);
          updateSelectedNet(net);
        } else {
          errorMessageRef.value = "An error occurred modifying the expected participants.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error modifying the expected participants:', error); })   

}
function performEditNetExpectedParticipantsActive() {
  performEditNetExpectedParticipants("nets");
}

function performEditNetExpectedParticipantsScheduled() {
  performEditNetExpectedParticipants("scheduledNets");
}

</script>

<template>
<div>
    <!-- dialogs -->
    <div v-if="dialogCloseNetShowRef.value">
      <teleport to="#modals">    
        <dialog :open="dialogCloseNetShowRef.value" ref="dialogCloseNetRef" @close="dialogCloseNetShowRef.value = false" class="topz">  
          <form v-if="dialogCloseNetShowRef.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to secure net {{netCallsignRef.value}}?
            <br>
            <button class="boxButton" v-on:click.native="closeNetYes">Yes</button>
            <button class="boxButton" v-on:click.native="closeNetNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogDeleteNetShowRef.value">
      <teleport to="#modals">    
        <dialog :open="dialogDeleteNetShowRef.value" ref="dialogDeleteNetRef" @close="dialogDeleteNetShowRef.value = false" class="topz">
          <form v-if="dialogDeleteNetShowRef.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to delete scheduled net {{netCallsignRef.value}}?
            <br>
            <button class="boxButton" v-on:click.native="deleteNetYes">Yes</button>
            <button class="boxButton" v-on:click.native="deleteNetNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogEditNetShowRef.value">
      <teleport to="#modals">    
        <dialog :open="dialogEditNetShowRef.value" ref="dialogEditNetRef" @close="dialogEditNetShowRef.value = false" class="topz">  
          <form v-if="dialogEditNetShowRef.value" method="dialog">
            <div class="pagesubheader">Edit Net {{ netCallsignRef.value }}</div>
            <div class="line"><hr/></div>
            Modify the net as needed. Some values cannot be changed. 
            <br>
              <div>
                <label for="callsignField">Callsign:</label>
                <input type="text" id="callsignField" v-model="netCallsign2Ref.value" readonly/>
              </div>
              <div>
                <label for="nameField">Name:</label>
                <input type="text" id="nameField" v-model="netName2Ref.value" readonly/>
              </div>
              <div>
                <label for="descriptionField">Description:</label>
                <input type="text" id="descriptionField" v-model="netDescription2Ref.value" />
              </div>
              <div>
                <label for="checkinMessageField">Check-in message:</label>
                <input type="text" id="checkinMessageField" v-model="netCheckinMessage2Ref.value" maxlength="50" />
              </div>
              <div>
                <label for="voiceFrequencyField">Voice Frequency:</label>
                <input type="text" id="voiceFrequencyField" v-model="netVoiceFrequency2Ref.value" />
              </div>
              <div>
                <label for="latitudeField">Latitude:</label>
                <input type="text" id="latitudeField" v-model="netLatitude2Ref.value" readonly />
              </div>
              <div>
                <label for="longitudeField">Longitude:</label>
                <input type="text" id="longitudeField" v-model="netLongitude2Ref.value" readonly />
              </div>
              <!-- announced -->
              <div>
                  <div v-if="(netAnnounced2Ref.value == 'true')">
                    Announce? Yes
                  </div>
                  <div v-else>
                    Announce? No
                  </div>
              </div>
              <div>
                  <div v-if="(netCheckinReminderRef.value == 'true')">
                    Remind participants to check out? Yes
                  </div>
                  <div v-else>
                    Remind participants to check out? No
                  </div>
              </div>
              <div>
                  <div v-if="(netOpenRef.value == true)">
                    Open participation? Yes
                  </div>
                  <div v-else>
                    Open participation? No
                  </div>
              </div>
              <div v-if="(netOpenRef.value == false)">
                  <div v-if="(netParticipantInviteAllowedRef.value == true)">
                    Participants can invite? Yes
                  </div>
                  <div v-else>
                    Participants can invite? No
                  </div>
              </div>
              <div>
                <label for="expectedCallsignsField">Expected participant callsigns:</label>
                <input type="text" id="expectedCallsignsField" v-model="netExpectedCallsigns2Ref.value"/>
              </div>
              <!-- scheduled vs now  -->
              <div>
              <div>
                <div v-if="netActive2Ref.value">
                  When to start: (in progress)
                </div>
                <div v-else>
                  When to start: later
                </div>
              </div>
              </div>
              <div v-if="(netActive2Ref.value)">
                <!-- nothing else -->
              </div>
              <div v-else>
                <div>
                  <div v-if="(netType2Ref.value == '0')">
                    How often: Unknown
                  </div>
                  <div v-if="(netType2Ref.value == '1')">
                    How often: Daily
                  </div>
                  <div v-if="(netType2Ref.value == '2')">
                    How often: Weekly
                  </div>
                  <div v-if="(netType2Ref.value == '3')">
                    How often: Monthly specific date
                  </div>
                  <div v-if="(netType2Ref.value == '4')">
                    How often: Monthly relative date
                  </div>
                  <div v-if="(netType2Ref.value == '5')">
                    How often: One time only
                  </div>
                </div>
                <div>
                  <div v-if="(netType2Ref.value != '5')">
                      <div v-if="(netDayStart2Ref.value == '0')">
                        Day start: Unknown
                      </div>
                      <div v-if="(netDayStart2Ref.value == '1')">
                        Day start: Sunday
                      </div>
                      <div v-if="(netDayStart2Ref.value == '2')">
                        Day start: Monday
                      </div>
                      <div v-if="(netDayStart2Ref.value == '3')">
                        Day start: Tuesday
                      </div>
                      <div v-if="(netDayStart2Ref.value == '4')">
                        Day start: Wednesday
                      </div>
                      <div v-if="(netDayStart2Ref.value == '5')">
                        Day start: Thursday
                      </div>
                      <div v-if="(netDayStart2Ref.value == '6')">
                        Day start: Friday
                      </div>
                      <div v-if="(netDayStart2Ref.value == '7')">
                        Day start: Saturday
                      </div>
                  </div>
                </div>
                <div>
                  <label for="durationField">Duration (hours):</label>
                  <input type="text" id="durationField" v-model="netDuration2Ref.value" maxlength="3"  readonly />
                </div>
              <div v-if="(netType2Ref.value == '5')">
                <div>
                  <label for="timeStartField">Start time (YYYY-MM-DD HH:MM):</label>
                  <input type="text" id="timeStartField" v-model="netTimeStartStr2Ref.value" maxlength="16"  readonly />
                </div>
              </div>
              <div v-else>
                <div>
                  <label for="timeStartField">Start time (HH:MM):</label>
                  <input type="text" id="timeStartField" v-model="netTimeStartStr2Ref.value" maxlength="5"  readonly />
                </div>
              </div>
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="editNetYes">Modify</button>
            <button class="boxButton" v-on:click.native="editNetNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

    <!-- main page -->
    <div v-if="((netsRef.value != null) && (netsRef.value.length > 0))">
      <div class="grid-container">
        <div v-if="netOpenRef.value" class="pagesubheader grid-item"> {{netCallsignRef.value}}</div>
        <div v-else class="pagesubheader grid-item"> {{netCallsignRef.value}} <i class="fa-solid fa-lock"></i></div>
        <div class="grid-item">
        </div>
          <div v-if="(!(netRemoteRef.value))" class="grid-item">
            <div v-if="(netActiveRef.value)" class="grid-item">
              <button class="boxButton" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))" v-on:click.native="editNet">Edit Net</button>
              <button class="boxButton" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))" v-on:click.native="closeNet">Secure Net</button>
            </div>
            <div v-else>
              <button class="boxButton" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))" v-on:click.native="editNet">Edit Net</button>
              <button class="boxButton" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'ADMIN') || (localLoggedInUserRef.value.role == 'SYSADMIN'))" v-on:click.native="deleteNet">Delete Net</button>
            </div>
          </div>
      </div>
        <div class="line"><hr/></div>
        <b>{{netNameRef.value}}</b> - <i>{{ netDescriptionRef.value }}</i><br>
        Voice: {{ netVoiceFrequencyRef.value }}<br>
        <i>Created by: {{ netCreatedByRef.value }}</i><br>
        <div v-if="(netActiveRef.value)">
          <i>Started: {{ netStartTimeRef.value }} - {{ netAnnouncedMessageRef.value }}</i>
        </div>
        <div v-else>
          <i>Scheduled: {{ netStartTimeRef.value }} - {{ netAnnouncedMessageRef.value }}</i>
        </div>
        <div v-if="netsRef.value.length > 1">
          <div class="line"><hr/></div>
          <div> {{ previousNetCallsignRef.value }} <button class="boxButton" @click="goNetPrevious"> <<< </button> <button class="boxButton" @click="goNetNext"> >>> </button> {{  nextNetCallsignRef.value }}</div>
        </div>
        <div v-else>
        </div>
    </div>
    <div v-else>
      <br>
      <br>
      <div class="pagesubheader">Currently no active or scheduled nets.</div>
    </div>
</div>
</template>

<style scoped>
.grid-container {
  display: grid;
  grid-template-columns: 40% 20% 40%;
}
</style>
