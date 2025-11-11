<script setup>
import PageHeaderPane from '@/components/PageHeaderPane.vue'
import { reactive, onMounted } from 'vue'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const summary = reactive({ value : {}});
const ncStats = reactive({ value : {}});

const runDuration = reactive({ value : 0});
const hb1Duration = reactive({ value : 0});
const hb2Duration = reactive({ value : 0});
const hb3Duration = reactive({ value : 0});
const hb4Duration = reactive({ value : 0});
const accesstoken = reactive({ value : ''});

onMounted(() => {
  accesstoken.value = getToken();
  redirect(accesstoken.value, "Dashboard", useRouter());
  getData();
})

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
          </div>
        </div>
      </div>
    </div>
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
