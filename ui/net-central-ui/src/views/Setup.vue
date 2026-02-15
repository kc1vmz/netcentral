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
      <PageHeaderPane title="Setup" description="Configuration settings for Net Central."/>
      <div>
        <splitpanes class="default-theme" horizontal style="height: 100vh; width: 100%" @resized="storePaneSize1">
          <pane style="width: 100%" :size="paneSizes1">
            <div class="content">
              <div class="pagesubheader">Transceiver Configuration</div>
              <splitpanes vertical  @resized="storePaneSize2">
                <pane min-size="40" max-size="60" :size="paneSizes2">
                  <div class="content">
                    <TransceiversListPane/> 
                  </div>
                </pane>
                <pane min-size="40" max-size="60" :size="100-paneSizes2">
                  <div class="content">
                    <TransceiverDetailsPane/> 
                  </div>
                </pane>
              </splitpanes>
            </div>
          </pane>
          <pane style="width: 100%" :size="paneSizes4">
            <div class="content">
              <div class="pagesubheader">Users</div>
              <splitpanes vertical @resized="storePaneSize3">
                <pane min-size="40" max-size="60" :size="paneSizes3">
                  <div class="content">
                    <UsersListPane/> 
                  </div>
                </pane>
                <pane min-size="40" max-size="60" :size="100-paneSizes3" >
                  <div class="content">
                    <UserDetailsPane/> 
                  </div>
                </pane>
              </splitpanes>
            </div>
          </pane>
          <pane style="width: 100%" :size="paneSizes5">
            <div class="content">
              <div class="pagesubheader">Settings</div>
                <div class="content">
                  <SettingsPane/> 
                </div>
            </div>
          </pane>
        </splitpanes>
      </div>
    </div>
</template>

<script setup>
import { Splitpanes, Pane } from 'splitpanes';
import 'splitpanes/dist/splitpanes.css';
import TransceiversListPane from '@/components/TransceiversListPane.vue'
import TransceiverDetailsPane from '@/components/TransceiverDetailsPane.vue'
import UsersListPane from '@/components/UsersListPane.vue'
import UserDetailsPane from '@/components/UserDetailsPane.vue'
import PageHeaderPane from '@/components/PageHeaderPane.vue'
import SettingsPane from '@/components/SettingsPane.vue'
import { ref, onMounted } from 'vue'
import { getToken, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

const paneSizes1 = ref(33)
const paneSizes2 = ref(50)
const paneSizes3 = ref(50)
const paneSizes4 = ref(33)
const paneSizes5 = ref(34)
var router = useRouter();

onMounted(() => {
  redirect(getToken(), "Setup", router);
  const storedSizesSetup1 = localStorage.getItem('NetCentral-splitpanes-sizes-setup-1')
  if (storedSizesSetup1) {
    paneSizes1.value = JSON.parse(storedSizesSetup1)
  } else {
    paneSizes1.value = 33;
  }
  const storedSizesSetup2 = localStorage.getItem('NetCentral-splitpanes-sizes-setup-2')
  if (storedSizesSetup2) {
    paneSizes2.value = JSON.parse(storedSizesSetup2)
  } else {
    paneSizes2.value = 50;
  }
  const storedSizesSetup3 = localStorage.getItem('NetCentral-splitpanes-sizes-setup-3')
  if (storedSizesSetup3) {
    paneSizes3.value = JSON.parse(storedSizesSetup3)
  } else {
    paneSizes3.value = 50;
  }
  const storedSizesSetup4 = localStorage.getItem('NetCentral-splitpanes-sizes-setup-4')
  if (storedSizesSetup4) {
    paneSizes4.value = JSON.parse(storedSizesSetup4)
  } else {
    paneSizes4.value = 33;
  }
  const storedSizesSetup5 = localStorage.getItem('NetCentral-splitpanes-sizes-setup-5')
  if (storedSizesSetup5) {
    paneSizes5.value = JSON.parse(storedSizesSetup5)
  } else {
    paneSizes5.value = 34;
  }
})

const storePaneSize1 = ({ prevPane }) => {
  if (prevPane.index == 0) {
    var total = paneSizes1.value + paneSizes4.value;
    paneSizes1.value = prevPane.size;
    paneSizes4.value = total - prevPane.size;
    localStorage.setItem('NetCentral-splitpanes-sizes-setup-1', JSON.stringify(paneSizes1.value))
    localStorage.setItem('NetCentral-splitpanes-sizes-setup-4', JSON.stringify(paneSizes4.value))
  } else {
    var total = paneSizes4.value + paneSizes5.value;
    paneSizes4.value = prevPane.size;
    paneSizes5.value = total - prevPane.size;
    localStorage.setItem('NetCentral-splitpanes-sizes-setup-4', JSON.stringify(paneSizes4.value))
    localStorage.setItem('NetCentral-splitpanes-sizes-setup-5', JSON.stringify(paneSizes5.value))
  }

}
const storePaneSize2 = ({ prevPane }) => {
  paneSizes2.value = prevPane.size;
  localStorage.setItem('NetCentral-splitpanes-sizes-setup-2', JSON.stringify(paneSizes2.value))
}
const storePaneSize3 = ({ prevPane }) => {
  paneSizes3.value = prevPane.size;
  localStorage.setItem('NetCentral-splitpanes-sizes-setup-3', JSON.stringify(paneSizes3.value))
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