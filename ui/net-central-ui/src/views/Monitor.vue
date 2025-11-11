<template>
  <div>
      <div>
        <MonitorPageHeaderPane/>
      </div>
      <div>
        <splitpanes class="default-theme" horizontal style="height: 100vh; width: 100%" @resized="storePaneSize1" v-model="paneSizes1">
          <pane min-size="10" max-size="60" style="width: 100%" :size="paneSizes1">
            <splitpanes vertical @resized="storePaneSize2" v-model="paneSizes2">
              <pane :size="paneSizes2">
                <div class="content">
                  <MonitorNetMapPane/> 
                </div>
              </pane>
              <pane :size="100-paneSizes2">
                <div class="content">
                  <MonitorNetParticipantsPane/> 
                </div>
              </pane>
            </splitpanes>
          </pane>
          <pane style="width: 100%" :size="100-paneSizes1">
            <splitpanes vertical @resized="storePaneSize3" v-model="paneSizes3">
              <pane :size="paneSizes3">
                <div class="content">
                  <MonitorNetMessagesPane/> 
                </div>
              </pane>
              <pane :size="100-paneSizes3">
                <div class="content">
                  <MonitorNetReportPane/> 
                </div>
              </pane>
            </splitpanes>
          </pane>
        </splitpanes>
      </div>
    </div>
</template>

<script setup>
import { Splitpanes, Pane } from 'splitpanes';
import 'splitpanes/dist/splitpanes.css';
import MonitorNetMapPane from '@/components/MonitorNetMapPane.vue'
import MonitorNetParticipantsPane from '@/components/MonitorNetParticipantsPane.vue'
import MonitorNetMessagesPane from '@/components/MonitorNetMessagesPane.vue'
import MonitorNetReportPane from '@/components/MonitorNetReportPane.vue'
import MonitorPageHeaderPane from '@/components/MonitorPageHeaderPane.vue'
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue } from "@/SelectedNet.js";
import { ref, onMounted, watch } from 'vue'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

const paneSizes1 = ref(50)
const paneSizes2 = ref(50)
const paneSizes3 = ref(50)
var router = useRouter();

onMounted(() => {
  redirect(getToken(), "Monitor", router);
  const storedSizesSetup1 = localStorage.getItem('NetControl-splitpanes-sizes-monitor-1')
  if (storedSizesSetup1) {
    paneSizes1.value = JSON.parse(storedSizesSetup1)
  } else {
    paneSizes1.value = 50;
  }
  const storedSizesSetup2 = localStorage.getItem('NetControl-splitpanes-sizes-monitor-2')
  if (storedSizesSetup2) {
    paneSizes2.value = JSON.parse(storedSizesSetup2)
  } else {
    paneSizes2.value = 50;
  }
  const storedSizesSetup3 = localStorage.getItem('NetControl-splitpanes-sizes-monitor-3')
  if (storedSizesSetup3) {
    paneSizes3.value = JSON.parse(storedSizesSetup3)
  } else {
    paneSizes3.value = 50;
  }
})

const storePaneSize1 = ({ prevPane }) => {
  paneSizes1.value = prevPane.size;
  localStorage.setItem('NetControl-splitpanes-sizes-monitor-1', JSON.stringify(paneSizes1.value))
}
const storePaneSize2 = ({ prevPane }) => {
  paneSizes2.value = prevPane.size;
  localStorage.setItem('NetControl-splitpanes-sizes-monitor-2', JSON.stringify(paneSizes2.value))
}
const storePaneSize3 = ({ prevPane }) => {
  paneSizes3.value = prevPane.size;
  localStorage.setItem('NetControl-splitpanes-sizes-monitor-3', JSON.stringify(paneSizes3.value))
}
</script>

<style>
/* Basic styling for the content inside the panes */
.content {
  display: flex;
  flex-direction: column;
  justify-content: left;
  align-items: left;
  padding: 1rem;
  height: 100%;
  width: 100%;
}
.splitpanes__pane {
  display: flex;
  justify-content: left;
  align-items: left;
  background-color: white;
}

.default-theme.splitpanes .splitpanes__pane {
  display: flex;
  justify-content: left;
  align-items: left;
  background-color: white;
}

</style>