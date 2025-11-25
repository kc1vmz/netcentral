<script setup>
import MobilePageHeaderPane from '@/components/MobilePageHeaderPane.vue'
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
    <MobilePageHeaderPane title="Dashboard" description="Current statistics"/>
  </div>
  <br>
  <div class="mobilepagesubheader">Nets</div>
  <div>{{ summary.value.activeNets }} Active Nets</div>
  <div>{{ summary.value.activeNetParticipants }} Active net participants</div>

  <br>
  <div class="mobilepagesubheader">Stations</div>
  <div>{{summary.value.stationsHeard}} Stations</div>
  <div>{{ summary.value.digipeatersHeard }} Digipeaters</div>
  <div>{{ summary.value.iGatesHeard }} iGates</div>
  <div>{{ summary.value.internetServersHeard }} Internet Servers</div>
  <div>{{ summary.value.iGatesHeard }} Repeaters</div>
  <div>{{ summary.value.weatherStationsHeard }} Weather Stations</div>
  <div>{{ summary.value.winlinkGatewaysHeard }} Winlink Stations</div>
  <div>{{ summary.value.bbsHeard }} Bulletin Boards</div>
  <div>{{ summary.value.mmdvmHeard }} MMDVM</div>
  <div>{{ summary.value.othersHeard }} Others</div>
  <div>{{ summary.value.trackedStations }} Tracked Stations</div>
  <div>{{ summary.value.callsignsHeard }} Callsigns</div>
</template>


<style scoped>
</style>
