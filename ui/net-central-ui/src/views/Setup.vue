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
        <br>
        <Tabs>
          <Tab value="Announcements">
            <div class="content">
              <SettingsAnnouncementsPane/> 
            </div>
          </Tab>
          <Tab value="Federation">
            <div class="content">
              <SettingsFederationPane/> 
            </div>
          </Tab>
          <Tab value="Map">
            <div class="content">
              <SettingsMapPane/> 
            </div>
          </Tab>
          <Tab value="Map Cache">
            <div class="content">
              <SettingsMapCachePane/> 
            </div>
          </Tab>
          <Tab value="Net Manager">
            <div class="content">
              <SettingsNetManagerPane/> 
            </div>
          </Tab>
          <Tab value="Transceivers">
            <div class="content">
              <TransceiversListPane/> 
              <TransceiverDetailsPane/> 
            </div>
          </Tab>
          <Tab value="Users">
            <div class="content">
              <UsersListPane/> 
              <UserDetailsPane/> 
            </div>
          </Tab>
          <Tab value="Timers">
            <div class="content">
              <SettingsTimersPane/> 
            </div>
          </Tab>
        </Tabs>
      </div>
    </div>
</template>

<script setup>
import 'splitpanes/dist/splitpanes.css';
import TransceiversListPane from '@/components/TransceiversListPane.vue'
import TransceiverDetailsPane from '@/components/TransceiverDetailsPane.vue'
import UsersListPane from '@/components/UsersListPane.vue'
import UserDetailsPane from '@/components/UserDetailsPane.vue'
import PageHeaderPane from '@/components/PageHeaderPane.vue'
import SettingsFederationPane from '@/components/SettingsFederationPane.vue'
import SettingsMapPane from '@/components/SettingsMapPane.vue'
import SettingsAnnouncementsPane from '@/components/SettingsAnnouncementsPane.vue'
import SettingsTimersPane from '@/components/SettingsTimersPane.vue'
import SettingsMapCachePane from '@/components/SettingsMapCachePane.vue'
import SettingsNetManagerPane from '@/components/SettingsNetManagerPane.vue'

import { Tabs, Tab } from 'super-vue3-tabs';
import { reactive, onMounted } from 'vue';
import { getToken, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

const accesstokenRef = reactive({value : null});
const localLoggedInUserRef = reactive({value : null});

var router = useRouter();

onMounted(() => {
  redirect(getToken(), "Setup", router);
  accesstokenRef.value = getToken();
  localLoggedInUserRef.value = getUser();
});

</script>

<style scoped>
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