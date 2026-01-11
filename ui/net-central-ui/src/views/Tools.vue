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

<template>
    <!-- dialogs -->
    <div v-if="dialogEnableRawPacketLoggingShow.value">
      <teleport to="#modals">
        <dialog :open="dialogEnableRawPacketLoggingShow.value" ref="dialogEnableRawPacketLogging" @close="dialogEnableRawPacketLoggingShow.value = false" class="topz">  
          <form v-if="dialogEnableRawPacketLoggingShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to enable raw packet logging?
            <br>
            <div>
              <b>{{ errorMessage }}</b>
            </div>
            <br>
            <button class="boxButton" v-on:click.native="enableRawPacketLoggingYes">Yes</button>
            <button class="boxButton" v-on:click.native="enableRawPacketLoggingNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogDisableRawPacketLoggingShow.value">
      <teleport to="#modals">
        <dialog :open="dialogDisableRawPacketLoggingShow.value" ref="dialogDisableRawPacketLogging" @close="dialogDisableRawPacketLoggingShow.value = false" class="topz">  
          <form v-if="dialogDisableRawPacketLoggingShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to disable raw packet logging?
            <br>
            <div>
              <b>{{ errorMessage }}</b>
            </div>
            <br>
            <button class="boxButton" v-on:click.native="disableRawPacketLoggingYes">Yes</button>
            <button class="boxButton" v-on:click.native="disableRawPacketLoggingNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogExportRawPacketsShow.value">
      <teleport to="#modals">
        <dialog :open="dialogExportRawPacketsShow.value" ref="dialogExportRawPackets" @close="dialogExportRawPacketsShow.value = false" class="topz">  
          <form v-if="dialogExportRawPacketsShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to download all heard raw packet data from Net Central ?
            <br>
            <div>
              <b>{{ errorMessage }}</b>
            </div>
            <br>
            <button class="boxButton" v-on:click.native="exportRawPacketsYes">Yes</button>
            <button class="boxButton" v-on:click.native="exportRawPacketsNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogConfirmDeleteAllNetCentralShow.value">
      <teleport to="#modals">
        <dialog :open="dialogConfirmDeleteAllNetCentralShow.value" ref="dialogConfirmDeleteAllNetCentral" @close="dialogConfirmDeleteAllNetCentralShow.value = false" class="topz">  
          <form v-if="dialogConfirmDeleteAllNetCentralShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to delete all Net Central data ?
            <br>
            <div>
              <b>{{ errorMessage }}</b>
            </div>
            <br>
            <button class="boxButton" v-on:click.native="deleteAllNetCentralDataYes">Yes</button>
            <button class="boxButton" v-on:click.native="deleteAllNetCentralDataNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogConfirmDeleteAllShow.value">
      <teleport to="#modals">
        <dialog :open="dialogConfirmDeleteAllShow.value" ref="dialogConfirmDeleteAll" @close="dialogConfirmDeleteAllShow.value = false" class="topz">  
          <form v-if="dialogConfirmDeleteAllShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to delete all heard APRS data ?
            <br>
            <div>
              <b>{{ errorMessage }}</b>
            </div>
            <br>
            <button class="boxButton" v-on:click.native="deleteAllDataYes">Yes</button>
            <button class="boxButton" v-on:click.native="deleteAllDataNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogSendNTSShow.value">
      <teleport to="#modals">
        <dialog :open="dialogSendNTSShow.value" ref="dialogSendNTS" @close="dialogSendNTSShow.value = false" class="topz">  
          <form v-if="dialogSendNTSShow.value" method="dialog">
            <div class="pagesubheader">Send NTS Radiogram</div>
            <div class="line"><hr/></div>
            <div class="grid-container-nts">
              <div class="grid-item" style="display: flex; flex-direction: column;">
                <label for="ntsNumber" style="width: 50px;">Number</label>
                <input style="padding: 5px; width: 50px; border-width: thin;" type="text" id="ntsNumber" v-model="ntsNumber"/>
              </div>
              <div class="grid-item" style="display: flex; flex-direction: column;">
                <label for="ntsPrecedence" style="width: 50px;">Precedence</label>
                <input style="padding: 5px; width: 50px; border-width: thin;" type="text" id="ntsPrecedence" v-model="ntsPrecedence"/>
              </div>
              <div class="grid-item" style="display: flex; flex-direction: column;">
                <label for="ntsHX" style="width: 50px;">HX</label>
                <input style="padding: 5px; width: 50px; border-width: thin;" type="text" id="ntsHX" v-model="ntsHX"/>
              </div>
              <div class="grid-item" style="display: flex; flex-direction: column;">
                <label for="ntsCallsignOrigin">Station of origin</label>
                <input style="padding: 5px; border-width: thin;" type="text" id="ntsCallsignOrigin" v-model="ntsCallsignOrigin"/>
              </div>
              <div class="grid-item" style="display: flex; flex-direction: column;">
                <label for="ntsLocationOrigin">Place of origin</label>
                <input style="padding: 5px; border-width: thin;" type="text" id="ntsLocationOrigin" v-model="ntsLocationOrigin"/>
              </div>
              <div class="grid-item" style="display: flex; flex-direction: column;">
                <label for="ntsCheck" style="width: 50px;">Check</label>
                <input style="padding: 5px; width: 50px; border-width: thin;" type="text" id="ntsCheck" v-model="ntsCheck"/>
              </div>
              <div class="grid-item" style="display: flex; flex-direction: column;">
                <label for="ntsTimeFiled">Time filed</label>
                <input style="padding: 5px; width: 60px; border-width: thin;" type="text" id="ntsTimeFiled" v-model="ntsTimeFiled"/>
              </div>
              <div class="grid-item" style="display: flex; flex-direction: column;">
                <label for="ntsDateFiled">Date filed</label>
                <input style="padding: 5px; width: 60px; border-width: thin;" type="text" id="ntsDateFiled" v-model="ntsDateFiled"/>
              </div>
            </div>
            <div class="line"><hr/></div>
            <div>
              <label for="ntsToName">To:</label>
              <input style="padding: 5px;" type="text" id="ntsToName" v-model="ntsToName"/>
            </div>
            <div>
              <label for="ntsToAddress">Street Address:</label>
              <input style="padding: 5px;" type="text" id="ntsToAddress" v-model="ntsToAddress"/>
            </div>
            <div>
              <label for="ntsToCityState">City, State:</label>
              <input style="padding: 5px;" type="text" id="ntsToCityState" v-model="ntsToCityState"/>
            </div>
            <div>
              <label for="ntsPhoneNumber">Phone number:</label>
              <input style="padding: 5px;" type="text" id="ntsPhoneNumber" v-model="ntsPhoneNumber"/>
            </div>
            <div>
              <label for="ntsEmailAddress">Email address:</label>
              <input style="padding: 5px;" type="text" id="ntsEmailAddress" v-model="ntsEmailAddress"/>
            </div>
            <div class="line"><hr/></div>
            <div>
              Message:
              <br>
              <input style="padding: 5px;" type="text" id="ntsMessage1" v-model="ntsMessage1"/>
              <input style="padding: 5px;" type="text" id="ntsMessage2" v-model="ntsMessage2"/>
              <input style="padding: 5px;" type="text" id="ntsMessage3" v-model="ntsMessage3"/>
              <input style="padding: 5px;" type="text" id="ntsMessage4" v-model="ntsMessage4"/>
              <input style="padding: 5px;" type="text" id="ntsMessage5" v-model="ntsMessage5"/>
              <br>
              <input style="padding: 5px;" type="text" id="ntsMessage6" v-model="ntsMessage6"/>
              <input style="padding: 5px;" type="text" id="ntsMessage7" v-model="ntsMessage7"/>
              <input style="padding: 5px;" type="text" id="ntsMessage8" v-model="ntsMessage8"/>
              <input style="padding: 5px;" type="text" id="ntsMessage9" v-model="ntsMessage9"/>
              <input style="padding: 5px;" type="text" id="ntsMessage10" v-model="ntsMessage10"/>
              <br>
              <input style="padding: 5px;" type="text" id="ntsMessage11" v-model="ntsMessage11"/>
              <input style="padding: 5px;" type="text" id="ntsMessage12" v-model="ntsMessage12"/>
              <input style="padding: 5px;" type="text" id="ntsMessage13" v-model="ntsMessage13"/>
              <input style="padding: 5px;" type="text" id="ntsMessage14" v-model="ntsMessage14"/>
              <input style="padding: 5px;" type="text" id="ntsMessage15" v-model="ntsMessage15"/>
              <br>
              <input style="padding: 5px;" type="text" id="ntsMessage16" v-model="ntsMessage16"/>
              <input style="padding: 5px;" type="text" id="ntsMessage17" v-model="ntsMessage17"/>
              <input style="padding: 5px;" type="text" id="ntsMessage18" v-model="ntsMessage18"/>
              <input style="padding: 5px;" type="text" id="ntsMessage19" v-model="ntsMessage19"/>
              <input style="padding: 5px;" type="text" id="ntsMessage20" v-model="ntsMessage20"/>
              <br>
              <input style="padding: 5px;" type="text" id="ntsMessage21" v-model="ntsMessage21"/>
              <input style="padding: 5px;" type="text" id="ntsMessage22" v-model="ntsMessage22"/>
              <input style="padding: 5px;" type="text" id="ntsMessage23" v-model="ntsMessage23"/>
              <input style="padding: 5px;" type="text" id="ntsMessage24" v-model="ntsMessage24"/>
              <input style="padding: 5px;" type="text" id="ntsMessage25" v-model="ntsMessage25"/>
            </div>
            <div class="line"><hr/></div>
            <div>
              <label for="ntsSignature">Signature:</label>
              <input style="padding: 5px;" type="text" id="ntsSignature" v-model="ntsSignature"/>
            </div>
            <div>
              <label for="ntsOperatorNote">Operator note:</label>
              <input style="padding: 5px;" type="text" id="ntsOperatorNote" v-model="ntsOperatorNote"/>
            </div>
             <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Send</button>
            <button class="boxButton" v-if="enableButtonRef" v-on:click.native="sendNTSYes">Send</button>
            <button class="boxButton" v-on:click.native="sendNTSNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogSendWinlinkShow.value">
      <teleport to="#modals">
        <dialog :open="dialogSendWinlinkShow.value" ref="dialogSendWinlink" @close="dialogSendWinlinkShow.value = false" class="topz">  
          <form v-if="dialogSendWinlinkShow.value" method="dialog">
            <div class="pagesubheader">Send Winlink Message</div>
            <div class="line"><hr/></div>
            <br>
            <div style="display: flex; flex-direction: column;">
              <label for="winlinkCallsign">Your Winlink callsign:</label>
              <input style="padding: 5px; border-width: thin;" type="text" id="winlinkCallsign" v-model="winlinkCallsign"/>
            </div>
            <div style="display: flex; flex-direction: column;">
              <label for="winlinkPassword">Your Winlink password:</label>
              <input style="padding: 5px; border-width: thin;" type="password" id="winlinkPassword" v-model="winlinkPassword"/>
            </div>
            <div style="display: flex; flex-direction: column;">
              <label for="winlinkRecipient">Recipient:</label>
              <input style="padding: 5px; border-width: thin;" type="text" id="winlinkRecipient" v-model="winlinkRecipient"/>
            </div>
            <div style="display: flex; flex-direction: column;">
              <label for="winlinkSubject">Subject:</label>
              <input style="padding: 5px; border-width: thin;" type="text" id="winlinkSubject" v-model="winlinkSubject"/>
            </div>
            <div style="display: flex; flex-direction: column;">
              <label for="winlinkMessage">Message:</label>
              <textarea id="winlinkMessage" name="winlinkMessage" rows="5" cols="50" placeholder="Enter your message here..." v-model="winlinkMessage"></textarea>
            </div>
            <div>
              <b>{{ errorMessage }}</b>
            </div>
            <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Send</button>
            <button class="boxButton" v-if="enableButtonRef" v-on:click.native="sendWinlinkYes">Send</button>
            <button class="boxButton" v-on:click.native="sendWinlinkNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

    <!-- main page -->
    <div>
        <div class="pageheader">Tools</div>
        <br>Net Central provides a set of integrated APRS tools that you can use independently from nets.
        <br>
        <div class="line" style="margin:0px"><hr/></div>
        <br>
    </div>
    <div class="grid-container">
      <div class="grid-item">
        <button class="bigBoxButton" v-on:click.native="sendNTS"><b>Send NTS Radiogram</b></button>
      </div>
      <div class="grid-item">
        Send an NTS Radiogram via APRS to NTS Gateway NTSGTE.
      </div>
    </div>
    <div class="grid-container">
      <div class="grid-item">
        <button class="bigBoxButton" v-on:click.native="sendWinlink"><b>Send Winlink Email</b></button>
      </div>
      <div class="grid-item">
        Send a Winlink email via APRS to Winlink Gateway WLNK-1.
      </div>
    </div>
    <div class="grid-container" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'SYSADMIN'))">
      <div v-if="(!rawPacketLogging.value)" class="grid-item">
        <button class="bigBoxButton" v-on:click.native="enableRawPacketLogging"><b>Enable raw packet logging</b></button>
      </div>
      <div v-if="(!rawPacketLogging.value)" class="grid-item">
        Enable raw logging of all heard packets for later download.
      </div>
      <div v-if="(rawPacketLogging.value)" class="grid-item">
        <button class="bigBoxButton" v-on:click.native="disableRawPacketLogging"><b>Disable raw packet logging</b></button>
      </div>
      <div v-if="(rawPacketLogging.value)" class="grid-item">
        Disable raw logging of all heard packets.
      </div>
    </div>
    <div class="grid-container">
      <div class="grid-item">
        <button class="bigBoxButton" v-on:click.native="exportRawPackets"><b>Download raw packets</b></button>
      </div>
      <div class="grid-item">
        Download all heard APRS packets to a file.
      </div>
    </div>
    <div class="grid-container" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'SYSADMIN'))">
      <div class="grid-item">
        <button class="bigBoxButton" v-on:click.native="deleteAllData"><b>Delete all APRS data</b></button>
      </div>
      <div class="grid-item">
        Delete all heard APRS data so you can start fresh.
      </div>
    </div>
    <div class="grid-container" v-if="(accesstokenRef.value != null) && ((localLoggedInUserRef.value.role == 'SYSADMIN'))">
      <div class="grid-item">
        <button class="bigBoxButton" v-on:click.native="deleteAllNetCentralData"><b>Delete all Net Central data</b></button>
      </div>
      <div class="grid-item">
        Delete all Net Central data (nets, archives, etc.) so you can start fresh.
      </div>
    </div>
</template>

<script setup>
import { ref, watch, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const accesstokenRef = reactive({value : null});
const localLoggedInUserRef = reactive({value : null});

var dialogSendNTS = ref(null);
var dialogSendNTSShow = reactive({ value : false });

var dialogSendWinlink = ref(null);
var dialogSendWinlinkShow = reactive({ value : false });

var dialogExportRawPacketsShow = reactive({ value : false });
var dialogExportRawPackets = ref(null);

var dialogEnableRawPacketLoggingShow = reactive({ value : false });
var dialogEnableRawPacketLogging = ref(null);
var dialogDisableRawPacketLoggingShow = reactive({ value : false });
var dialogDisableRawPacketLogging = ref(null);

var dialogConfirmDeleteAll = ref(null);
var dialogConfirmDeleteAllShow = reactive({ value : false });
var dialogConfirmDeleteAllNetCentral = ref(null);
var dialogConfirmDeleteAllNetCentralShow = reactive({ value : false });

var rawPacketLogging = reactive({ value : false });

var errorMessage = ref('');

var ntsNumber = ref('');
var ntsPrecedence = ref('');
var ntsHX = ref('');
var ntsCallsignOrigin = ref('');
var ntsLocationOrigin = ref('');
var ntsTimeFiled = ref('');
var ntsDateFiled = ref('');
var ntsToName = ref('');
var ntsToAddress = ref('');
var ntsToCityState = ref('');
var ntsPhoneNumber = ref('');
var ntsEmailAddress = ref('');
    var ntsSignature = ref('');
var ntsOperatorNote = ref('');
var ntsMessage1 = ref('');
var ntsMessage2 = ref('');
var ntsMessage3 = ref('');
var ntsMessage4 = ref('');
var ntsMessage5 = ref('');
var ntsMessage6 = ref('');
var ntsMessage7 = ref('');
var ntsMessage8 = ref('');
var ntsMessage9 = ref('');
var ntsMessage10 = ref('');
var ntsMessage11 = ref('');
var ntsMessage12 = ref('');
var ntsMessage13 = ref('');
var ntsMessage14 = ref('');
var ntsMessage15 = ref('');
var ntsMessage16 = ref('');
var ntsMessage17 = ref('');
var ntsMessage18 = ref('');
var ntsMessage19 = ref('');
var ntsMessage20 = ref('');
var ntsMessage21 = ref('');
var ntsMessage22 = ref('');
var ntsMessage23 = ref('');
var ntsMessage24 = ref('');
var ntsMessage25 = ref('');
var ntsCheck = ref('');
var router = useRouter();

onMounted(() => {
  redirect(getToken(), "Tools", router);
  accesstokenRef.value = getToken();
  localLoggedInUserRef.value = getUser();
  getRawPacketLoggingState();
});

watch(ntsMessage1, (newValue, oldValue) => {
  if ((newValue != '') && (ntsCallsignOrigin.value != '')  && (ntsToName.value != '')  && (ntsLocationOrigin.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});
watch(ntsCallsignOrigin, (newValue, oldValue) => {
  if ((newValue != '') && (ntsMessage1.value != '')  && (ntsToName.value != '')  && (ntsLocationOrigin.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});
watch(ntsToName, (newValue, oldValue) => {
  if ((newValue != '') && (ntsCallsignOrigin.value != '')  && (ntsMessage1.value != '')  && (ntsLocationOrigin.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});
watch(ntsLocationOrigin, (newValue, oldValue) => {
  if ((newValue != '') && (ntsCallsignOrigin.value != '')  && (ntsToName.value != '')  && (ntsMessage1.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});


var winlinkCallsign = ref('');
var winlinkPassword = ref('');
var winlinkRecipient = ref('');
var winlinkMessage = ref('');
var winlinkSubject = ref('');
var enableButtonRef = ref(false);

watch(winlinkCallsign, (newValue, oldValue) => {
  if ((newValue != '') && (winlinkPassword.value != '')  && (winlinkRecipient.value != '')  && (winlinkMessage.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});
watch(winlinkPassword, (newValue, oldValue) => {
  if ((newValue != '') && (winlinkCallsign.value != '')  && (winlinkRecipient.value != '')  && (winlinkMessage.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});
watch(winlinkRecipient, (newValue, oldValue) => {
  if ((newValue != '') && (winlinkCallsign.value != '')  && (winlinkPassword.value != '')  && (winlinkMessage.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});
watch(winlinkMessage, (newValue, oldValue) => {
  if ((newValue != '') && (winlinkCallsign.value != '')  && (winlinkPassword.value != '')  && (winlinkRecipient.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});

function sendNTS() {
  ntsNumber.value = '';
  ntsPrecedence.value = '';
  ntsHX.value = '';
  ntsCallsignOrigin.value = '';
  ntsLocationOrigin.value = '';
  ntsTimeFiled.value = '';
  ntsDateFiled.value = '';
  ntsToName.value = '';
  ntsToAddress.value = '';
  ntsToCityState.value = '';
  ntsPhoneNumber.value = '';
  ntsEmailAddress.value = '';
  ntsSignature.value = '';
  ntsOperatorNote.value = '';
  ntsMessage1.value = '';
  ntsMessage2.value = '';
  ntsMessage3.value = '';
  ntsMessage4.value = '';
  ntsMessage5.value = '';
  ntsMessage6.value = '';
  ntsMessage7.value = '';
  ntsMessage8.value = '';
  ntsMessage9.value = '';
  ntsMessage10.value = '';
  ntsMessage11.value = '';
  ntsMessage12.value = '';
  ntsMessage13.value = '';
  ntsMessage14.value = '';
  ntsMessage15.value = '';
  ntsMessage16.value = '';
  ntsMessage17.value = '';
  ntsMessage18.value = '';
  ntsMessage19.value = '';
  ntsMessage20.value = '';
  ntsMessage21.value = '';
  ntsMessage22.value = '';
  ntsMessage23.value = '';
  ntsMessage24.value = '';
  ntsMessage25.value = '';
  ntsCheck.value = '';

  dialogSendNTSShow.value = true;
}
function sendNTSYes() {
  // send this to the server
  var url = buildNetCentralUrl('/tools/ntsradiogram/requests');
  var bodyObject = {
    number : ntsNumber.value,
    hx : ntsHX.value,
    precedence: ntsPrecedence.value,
    callsignOrigin :  ntsCallsignOrigin.value,
    locationOrigin : ntsLocationOrigin.value,
    timeFiled : ntsTimeFiled.value,
    dateFiled : ntsDateFiled.value,
    name : ntsToName.value,
    address : ntsToAddress.value,
    cityState : ntsToCityState.value,
    phoneNumber : ntsPhoneNumber.value,
    emailAddress : ntsEmailAddress.value,
    signature : ntsSignature.value,
    operatorNote : ntsOperatorNote.value,
    check: ntsCheck.value,
    message1 : ntsMessage1.value,
    message2 : ntsMessage2.value,
    message3 : ntsMessage3.value,
    message4 : ntsMessage4.value,
    message5 : ntsMessage5.value,
    message6 : ntsMessage6.value,
    message7 : ntsMessage7.value,
    message8 : ntsMessage8.value,
    message9 : ntsMessage9.value,
    message10 : ntsMessage10.value,
    message11 : ntsMessage11.value,
    message12 : ntsMessage12.value,
    message13 : ntsMessage13.value,
    message14 : ntsMessage14.value,
    message15 : ntsMessage15.value,
    message16 : ntsMessage16.value,
    message17 : ntsMessage17.value,
    message18 : ntsMessage18.value,
    message19 : ntsMessage19.value,
    message20 : ntsMessage20.value,
    message21 : ntsMessage21.value,
    message22 : ntsMessage22.value,
    message23 : ntsMessage23.value,
    message24 : ntsMessage24.value,
    message25 : ntsMessage25.value
  };
  var requestOptions = {
    method: "POST",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: JSON.stringify(bodyObject)
  };

  fetch(url, requestOptions)
    .then(response => {
      if (response.status != 200) {
        errorMessage.value = "Error sending NTS Radiogram - "+response.status;
      } else {
        dialogSendNTSShow.value = false;
      }
      return response.json()
    })
    .then(data => {
    })
    .catch(error => { console.error('Error sending NTS Radiogram:', error); })
}
function sendNTSNo() {
  dialogSendNTSShow.value = false;
}

function deleteAllData() {
  dialogConfirmDeleteAllShow.value = true;
}
function deleteAllDataYes() {
  // send this to the server
  var url = buildNetCentralUrl('/APRSObjects/all/now');
  var requestOptions = {
    method: "DELETE",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: null
  };

  fetch(url, requestOptions)
    .then(response => {
      if (response.status != 200) {
        errorMessage.value = "Error deleting all APRS data - "+response.status;
      } else {
        dialogConfirmDeleteAllShow.value = false;
      }
      return response
    })
    .then(data => {
    })
    .catch(error => { console.error('Error deleting all APRS data:', error); })

}
function deleteAllDataNo() {
  dialogConfirmDeleteAllShow.value = false;
}

function deleteAllNetCentralData() {
  dialogConfirmDeleteAllNetCentralShow.value = true;
}

function performDeleteAll(resource) {
  // send this to the server
  var url = buildNetCentralUrl(resource);
  var requestOptions = {
    method: "DELETE",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: null
  };

  fetch(url, requestOptions)
    .then(response => {
      if (response.status != 200) {
        errorMessage.value = "Error deleting Net Central data - "+response.status;
      } else {
      }
      return response
    })
    .then(data => {
    })
    .catch(error => { console.error('Error deleting Net Central data:', error); })
}
function deleteAllNetCentralDataYes() {
    performDeleteAll('/nets/all/now');
    performDeleteAll('/scheduledNets/all/now');
    performDeleteAll('/completedNets/all/now');
    performDeleteAll('/netMessages/all/now');
    performDeleteAll('/participants/all/now');
    performDeleteAll('/netQuestionAnswers/all/now');
    performDeleteAll('/netQuestions/all/now');
    dialogConfirmDeleteAllNetCentralShow.value = false;
}
function deleteAllNetCentralDataNo() {
  dialogConfirmDeleteAllNetCentralShow.value = false;
}

function sendWinlink() {
  winlinkCallsign.value = '';
  winlinkPassword.value = '';
  winlinkRecipient.value = '';
  winlinkSubject.value = '';
  winlinkMessage.value = '';
  dialogSendWinlinkShow.value = true;
}
function sendWinlinkYes() {
  // send this to the server
  var url = buildNetCentralUrl('/tools/winlink/requests');
  var bodyObject = {
    callsign : winlinkCallsign.value,
    password : winlinkPassword.value,
    recipient :  winlinkRecipient.value,
    subject : winlinkSubject.value,
    message : winlinkMessage.value
  };
  var requestOptions = {
    method: "POST",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: JSON.stringify(bodyObject)
  };

  fetch(url, requestOptions)
    .then(response => {
      if (response.status != 200) {
        errorMessage.value = "Error sending Winlink message - "+response.status;
      } else {
        dialogSendWinlinkShow.value = false;
      }
      return response.json()
    })
    .then(data => {
    })
    .catch(error => { console.error('Error sending Winlink message:', error); })
}

function sendWinlinkNo() {
  dialogSendWinlinkShow.value = false;
  winlinkCallsign.value = '';
  winlinkPassword.value = '';
  winlinkRecipient.value = '';
  winlinkMessage.value = '';
  winlinkSubject.value = '';
}

function exportRawPackets() {
  dialogExportRawPacketsShow.value = true;
}

function exportRawPacketsYes() {
  dialogExportRawPacketsShow.value = false;
  window.open(buildNetCentralUrl("/tools/rawPackets"), '_blank');
}

function exportRawPacketsNo() {
  dialogExportRawPacketsShow.value = false;
}

function enableRawPacketLogging() {
  dialogEnableRawPacketLoggingShow.value = true;
}

function enableRawPacketLoggingYes() {
  setRawLoggingValue(true);
}

function enableRawPacketLoggingNo() {
  dialogEnableRawPacketLoggingShow.value = false;
}

function disableRawPacketLogging() {
  dialogDisableRawPacketLoggingShow.value = true;
}

function disableRawPacketLoggingYes() {
  setRawLoggingValue(false);
}

function disableRawPacketLoggingNo() {
  dialogDisableRawPacketLoggingShow.value = false;
}

function getRawPacketLoggingState() {
  rawPacketLogging.value = false;

  var requestOptions = {
    method: "GET",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: null
  };
  fetch(buildNetCentralUrl('/configParameters/RawPacketLogging'), requestOptions)
    .then(response => response.json())
    .then(data => {
        rawPacketLogging.value = data;
    })
    .catch(error => { console.error('Error getting configuration parameter:', error); })
}

function setRawLoggingValue(value) {
  // send this to the server
  var url;
  
  if (value) {
    url = buildNetCentralUrl('/configParameters/RawPacketLogging?value=true');
  } else {
    url = buildNetCentralUrl('/configParameters/RawPacketLogging?value=false');
  }

  var requestOptions = {
    method: "PUT",
    headers: { "Content-Type": "application/json",
                "SessionID" : getToken()
      },
    body: null
  };

  fetch(url, requestOptions)
    .then(response => {
      if (response.status != 200) {
        errorMessage.value = "Error setting configuration parameter - "+response.status;
      } else {
        dialogEnableRawPacketLoggingShow.value = false;
        dialogDisableRawPacketLoggingShow.value = false;
        rawPacketLogging.value = value;
      }
      return response.json()
    })
    .then(data => {
    })
    .catch(error => { console.error('Error setting configuration parameter:', error); })
}

</script>

<style scoped>
.line {
  margin : 20px;
}
.grid-container {
  display: grid;
  grid-template-columns: 20% 80%;
  margin: 10px;
  gap: 10px;
}
.grid-container-nts {
  display: grid;
  grid-template-columns: 7% 10% 7% 17% 17% 8% 10% 17%;
  margin: 10px;
  gap: 10px;
}

</style>