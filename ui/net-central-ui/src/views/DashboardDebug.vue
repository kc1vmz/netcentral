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
import PageHeaderPane from '@/components/PageHeaderPane.vue'
import { reactive, onMounted, watch } from 'vue'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateDashboardEvent } from "@/UpdateEvents";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";

const { socket } = useSocketIO();
socket.on("updateDashboard", (data) => {
  updateDashboard()
});

function generateValue() {
    const now = new Date();
    return now.toLocaleTimeString();
 }

function updateDashboard() {
    updateDashboardEvent.value = generateValue();
}

const summary = reactive({ value : {}});
const ncStats = reactive({ value : {}});

const runDuration = reactive({ value : 0});
const hb1Duration = reactive({ value : 0});
const hb2Duration = reactive({ value : 0});
const hb3Duration = reactive({ value : 0});
const hb4Duration = reactive({ value : 0});
const hb5Duration = reactive({ value : 0});
const hb6Duration = reactive({ value : 0});
const hb7Duration = reactive({ value : 0});
const accesstoken = reactive({ value : ''});

onMounted(() => {
  accesstoken.value = getToken();
  redirect(accesstoken.value, "Dashboard", useRouter());
  getData();
})

watch(updateDashboardEvent, (updateDashboardEventNew, updateDashboardEventOld) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  getData();
});

function getData() {
    getDashboardSummary();
    getNetCentralStatistics();
}

function getDashboardSummary() {
    var requestOptions = {
      method: "GET",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstoken.value
        },
      body: null
    };
    fetch(buildNetCentralUrl('/summaries/dashboardCounts'),requestOptions)
      .then(response => response.json())
      .then(data => {
          summary.value = data;
      })
      .catch(error => { console.error('Error getting dashboard summary from server:', error); })
}

function getNetCentralStatistics() {
    var requestOptions = {
      method: "GET",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstoken.value
        },
      body: null
    };
    fetch(buildNetCentralUrl('/summaries/dashboardNetCentral'), requestOptions)
      .then(response => response.json())
      .then(data => {
          ncStats.value = data;
          runDuration.value = calculateDuration(ncStats.value.startTimeMinutes*60);
          hb1Duration.value = calculateDuration(ncStats.value.lastHeartBeatSecondsSince1);
          hb2Duration.value = calculateDuration(ncStats.value.lastHeartBeatSecondsSince2);
          hb3Duration.value = calculateDuration(ncStats.value.lastHeartBeatSecondsSince3);
          hb4Duration.value = calculateDuration(ncStats.value.lastHeartBeatSecondsSince4);
          hb5Duration.value = calculateDuration(ncStats.value.lastHeartBeatSecondsSince5);
          hb6Duration.value = calculateDuration(ncStats.value.lastHeartBeatSecondsSince6);
          hb7Duration.value = calculateDuration(ncStats.value.lastHeartBeatSecondsSince7);
      })
      .catch(error => { console.error('Error getting dashboard summary from server:', error); })
}

function calculateDuration(seconds) {
    var days = Math.floor(seconds / (3600*24));
    var remain = (seconds) - (days*3600*24);
    var hours = Math.floor(remain/3600);
    remain = (seconds) - (days*3600*24) - (hours*3600);
    var minutes = Math.floor(remain / 60);

    if (days > 0) {
      return ""+days+" days "+hours+" hours "+minutes+" minutes";
    } else if ((days == 0) && (hours > 0)) {
      return ""+hours+" hours "+minutes+" minutes";
    } else {
      return ""+minutes+" minutes";
    }
}
</script>

<template>
  <div>
    <PageHeaderPane title="Dashboard" description="Current statistics for Net Central"/>
  </div>
  <div>
    <div class="grid-container">
      <div class="grid-item">
        <div class="grid-container">
          <div class="grid-item">
            <i class="fa fa-hexagon-nodes fa-8x" style="color:#2559a7"></i> 
          </div>
          <div class="grid-item">
            <div class="pagesubheader">Nets</div>
            <div>{{ summary.value.activeNets }} Active Nets</div>
            <div>{{ summary.value.activeNetParticipants }} Active net participants</div>
            <br>
            <div>{{ summary.value.scheduledNets }} Scheduled Nets</div>
            <br>
            <div>{{ ncStats.value.netsStarted }} Nets started</div>
            <div>{{ ncStats.value.netsClosed}} Nets secured</div>
            <br>
            <div>{{ summary.value.closedNets }} Completed Nets</div>
            <div>{{ summary.value.closedNetParticipants }} Completed net participants</div>
          </div>
        </div>
      </div>
      <div class="grid-item">
          <div class="grid-container">
          <div class="grid-item">
            <i class="fa fa-tower-broadcast fa-8x" style="color:#2559a7"></i> 
          </div>
          <div class="grid-item">
            <div class="pagesubheader">Stations</div>
            <div>{{summary.value.stationsHeard}} Stations heard</div>
            <div>{{ summary.value.digipeatersHeard }} Digipeaters heard</div>
            <div>{{ summary.value.iGatesHeard }} iGates heard</div>
            <div>{{ summary.value.internetServersHeard }} Internet Servers heard</div>
            <div>{{ summary.value.iGatesHeard }} Repeaters heard</div>
            <div>{{ summary.value.weatherStationsHeard }} Weather Stations heard</div>
            <div>{{ summary.value.winlinkGatewaysHeard }} Winlink Stations heard</div>
            <div>{{ summary.value.bbsHeard }} Bulletin Boards heard</div>
            <div>{{ summary.value.mmdvmHeard }} MMDVM heard</div>
            <div>{{ summary.value.othersHeard }} Others heard</div>
            <br>
            <div>{{ summary.value.trackedStations }} Tracked Stations</div>
            <br>
            <div>{{ summary.value.callsignsHeard }} Callsigns heard</div>
          </div>
        </div>
      </div>
      <div class="grid-item">
          <div class="grid-container">
          <div class="grid-item">
            <i class="fa-solid fa-users fa-8x" style="color:#2559a7"></i> 
          </div>
          <div class="grid-item">
            <div class="pagesubheader">Users</div>
            <div>{{summary.value.users}} Users</div>
            <div>{{ summary.value.loggedInUsers }} Users currently logged in</div>
            <br>
            <div>{{ ncStats.value.userLogins }} User logins</div>
            <div>{{ ncStats.value.userLogouts }} User logouts</div>
          </div>
        </div>
      </div>
      <div class="grid-item">
          <div class="grid-container">
          <div class="grid-item">
            <i class="fa-solid fa-arrow-right-arrow-left fa-8x" style="color:#2559a7"></i>
          </div>
          <div class="grid-item">
            <div class="pagesubheader">Communications</div>
            <div>{{summary.value.registeredTransceiversTotal}} Registered transceivers</div>
            <div>{{ summary.value.registeredTransceiversReceive }} Enabled for receive</div>
            <div>{{ summary.value.registeredTransceiversTransmit }} Enabled for transmit</div>
            <br>
            <div>{{ ncStats.value.objectsReceived }} Objects received</div>
            <div>{{ ncStats.value.objectsSent }} Objects sent</div>
            <div>{{ ncStats.value.messagesSent }} Messages sent</div>
            <div>{{ ncStats.value.reportsSent }} Reports sent</div>
            <div>{{ ncStats.value.acksRequested }} Acks requested</div>
            <div>{{ ncStats.value.acksSent }} Acks sent</div>
            <div>{{ ncStats.value.rejsSent }} Rejs sent</div>
            <div>{{ ncStats.value.outstandingObjects }} Objects awaiting processing</div>
            <div>{{ ncStats.value.lastReceivedSecondsSince }} Seconds since receiving message</div>
            <div>{{ ncStats.value.lastSentSecondsSince }} Seconds since sending message</div>
          </div>
        </div>
      </div>
      <div class="grid-item">
          <div class="grid-container">
          <div class="grid-item">
            <i class="fa-solid fa-chart-simple fa-8x" style="color:#2559a7"></i>
          </div>
          <div class="grid-item">
            <div class="pagesubheader">Operations</div>
            <div>{{ runDuration.value }} since started</div>
            <div>{{ hb1Duration.value }} since heartbeat {{ncStats.value.lastHeartBeatName1}} fired</div>
            <div>{{ hb2Duration.value }} since heartbeat {{ncStats.value.lastHeartBeatName2}} fired</div>
            <div>{{ hb3Duration.value }} since heartbeat {{ncStats.value.lastHeartBeatName3}} fired</div>
            <div>{{ hb4Duration.value }} since heartbeat {{ncStats.value.lastHeartBeatName4}} fired</div>
            <div>{{ hb5Duration.value }} since heartbeat {{ncStats.value.lastHeartBeatName5}} fired</div>
            <div>{{ hb6Duration.value }} since heartbeat {{ncStats.value.lastHeartBeatName6}} fired</div>
            <div>{{ hb7Duration.value }} since heartbeat {{ncStats.value.lastHeartBeatName7}} fired</div>
          </div>
        </div>
      </div>
    </div>
    <br>
    <a href="./Dashboard">Click here for standard dashboard.</a>
  </div>
</template>


<style scoped>
  .grid-container {
    display: grid;
    grid-template-columns: 50%  50%;
    margin: 20px;
    gap: 20px;
  }
</style>
