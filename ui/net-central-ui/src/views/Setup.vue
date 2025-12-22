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
        <splitpanes class="default-theme" horizontal style="height: 100vh; width: 100%" @resized="storePaneSize1" v-model="paneSizes1">
          <pane min-size="5" max-size="60" style="width: 100%" :size="paneSizes1">
            <div class="content">
              <div class="pagesubheader">Transceiver Configuration</div>
              <splitpanes vertical  @resized="storePaneSize2" v-model="paneSizes2">
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
          <pane min-size="5" max-size="60" style="width: 100%"  :size="100-paneSizes1">
            <div class="content">
              <div class="pagesubheader">Users</div>
              <splitpanes vertical  @resized="storePaneSize3" v-model="paneSizes3">
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
import { selectedObject , updateSelectedObject, setSelectedObjectionSelectionValue } from "@/SelectedObject.js";
import { ref, onMounted, watch } from 'vue'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

const paneSizes1 = ref(50)
const paneSizes2 = ref(50)
const paneSizes3 = ref(50)
var router = useRouter();

onMounted(() => {
  redirect(getToken(), "Setup", router);
  const storedSizesSetup1 = localStorage.getItem('NetCentral-splitpanes-sizes-setup-1')
  if (storedSizesSetup1) {
    paneSizes1.value = JSON.parse(storedSizesSetup1)
  } else {
    paneSizes1.value = 50;
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
})

const storePaneSize1 = ({ prevPane }) => {
  paneSizes1.value = prevPane.size;
  localStorage.setItem('NetCentral-splitpanes-sizes-setup-1', JSON.stringify(paneSizes1.value))
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