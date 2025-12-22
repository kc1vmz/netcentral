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
  <div>
      <div>
        <MonitorPageHeaderPane/>
      </div>
      <div>

        <splitpanes class="default-theme" vertical style="height: 100vh; width: 100%" @resized="storePaneSizesAll">
          <pane min-size="10" max-size="80" :size="paneSizes1">
            <div class="content">
              <MonitorNetMapPane/> 
            </div>
          </pane>

          <pane min-size="10" max-size="80" :size="paneSizes2">
            <splitpanes horizontal @resized="storePaneSizesMiddle">
              <pane :size="paneSizes4">
                <div class="content">
                  <MonitorNetParticipantsPane/> 
                </div>
              </pane>
              <pane :size="paneSizes5">
                <div class="content">
                  <MonitorNetReportPane/> 
                </div>
              </pane>
            </splitpanes>
          </pane>

          <pane min-size="10" max-size="80" :size="paneSizes3">
            <splitpanes horizontal @resized="storePaneSizesRight">
              <pane :size="paneSizes6">
                <div class="content">
                  <MonitorNetMessagesPane/> 
                </div>
              </pane>
              <pane :size="paneSizes7">
                <div class="content">
                <MonitorNetQuestionsPane/>
                </div>
              </pane>
              <pane :size="paneSizes8">
                <div class="content">
                  <MonitorNetQuestionAnswersPane/>
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
import MonitorNetQuestionsPane from '@/components/MonitorNetQuestionsPane.vue'
import MonitorNetQuestionAnswersPane from '@/components/MonitorNetQuestionAnswersPane.vue'

import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue } from "@/SelectedNet.js";
import { ref, onMounted, watch } from 'vue'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

const paneSizes1 = ref(34)
const paneSizes2 = ref(33)
const paneSizes3 = ref(33)
const paneSizes4 = ref(50)
const paneSizes5 = ref(50)
const paneSizes6 = ref(33)
const paneSizes7 = ref(33)
const paneSizes8 = ref(34)
var router = useRouter();

onMounted(() => {
  redirect(getToken(), "Monitor", router);
  const storedSizesSetup1 = localStorage.getItem('NetCentral-splitpanes-sizes-monitor-1')
  if (storedSizesSetup1) {
    paneSizes1.value = JSON.parse(storedSizesSetup1)
  } else {
    paneSizes1.value = 34;
  }
  const storedSizesSetup2 = localStorage.getItem('NetCentral-splitpanes-sizes-monitor-2')
  if (storedSizesSetup2) {
    paneSizes2.value = JSON.parse(storedSizesSetup2)
  } else {
    paneSizes2.value = 33;
  }
  const storedSizesSetup3 = localStorage.getItem('NetCentral-splitpanes-sizes-monitor-3')
  if (storedSizesSetup3) {
    paneSizes3.value = JSON.parse(storedSizesSetup3)
  } else {
    paneSizes3.value = 33;
  }
  const storedSizesSetup4 = localStorage.getItem('NetCentral-splitpanes-sizes-monitor-4')
  if (storedSizesSetup4) {
    paneSizes4.value = JSON.parse(storedSizesSetup4)
  } else {
    paneSizes4.value = 50;
  }
  const storedSizesSetup5 = localStorage.getItem('NetCentral-splitpanes-sizes-monitor-5')
  if (storedSizesSetup5) {
    paneSizes5.value = JSON.parse(storedSizesSetup5)
  } else {
    paneSizes5.value = 50;
  }
  const storedSizesSetup6 = localStorage.getItem('NetCentral-splitpanes-sizes-monitor-6')
  if (storedSizesSetup6) {
    paneSizes6.value = JSON.parse(storedSizesSetup6)
  } else {
    paneSizes6.value = 34;
  }
  const storedSizesSetup7 = localStorage.getItem('NetCentral-splitpanes-sizes-monitor-7')
  if (storedSizesSetup7) {
    paneSizes7.value = JSON.parse(storedSizesSetup7)
  } else {
    paneSizes7.value = 33;
  }
  const storedSizesSetup8 = localStorage.getItem('NetCentral-splitpanes-sizes-monitor-8')
  if (storedSizesSetup8) {
    paneSizes8.value = JSON.parse(storedSizesSetup8)
  } else {
    paneSizes8.value = 33;
  }
})

const storePaneSizesRight = ({ prevPane }) => {
  if (prevPane.index == 0) {
    var total = paneSizes6.value + paneSizes7.value;
    paneSizes6.value = prevPane.size;
    paneSizes7.value = total - prevPane.size;
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-6', JSON.stringify(paneSizes6.value))
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-7', JSON.stringify(paneSizes7.value))
  } else {
    var total = paneSizes7.value + paneSizes8.value;
    paneSizes7.value = prevPane.size;
    paneSizes8.value = total - prevPane.size;
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-7', JSON.stringify(paneSizes7.value))
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-8', JSON.stringify(paneSizes8.value))
  }
}
const storePaneSizesMiddle = ({ prevPane }) => {
    paneSizes4.value = prevPane.size;
    paneSizes5.value = 100 - prevPane.size;
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-4', JSON.stringify(paneSizes4.value))
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-5', JSON.stringify(paneSizes5.value))
}
const storePaneSizesAll  = ({ prevPane }) => {
  if (prevPane.index == 0) {
    var total = paneSizes1.value + paneSizes2.value;
    paneSizes1.value = prevPane.size;
    paneSizes2.value = total - prevPane.size;
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-1', JSON.stringify(paneSizes1.value))
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-2', JSON.stringify(paneSizes2.value))
  } else {
    var total = paneSizes2.value + paneSizes3.value;
    paneSizes2.value = prevPane.size;
    paneSizes3.value = total - prevPane.size;
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-2', JSON.stringify(paneSizes2.value))
    localStorage.setItem('NetCentral-splitpanes-sizes-monitor-3', JSON.stringify(paneSizes3.value))
  }
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