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
  <MonitorPageHeaderPaneMobile/>
  <br><br><MonitorNetParticipantsPane/> 
  <br><br><MonitorNetReportPane/> 
  <br><br><MonitorNetMessagesPane/> 
  <br><br><MonitorNetQuestionsPane/>
  <br><br><MonitorNetQuestionAnswersPane/>
  <br><br><MonitorNetMapPaneMobile/> 
</template>

<script setup>
import MonitorNetMapPaneMobile from '@/components/MonitorNetMapPaneMobile.vue'
import MonitorNetParticipantsPane from '@/components/MonitorNetParticipantsPane.vue'
import MonitorNetMessagesPane from '@/components/MonitorNetMessagesPane.vue'
import MonitorNetReportPane from '@/components/MonitorNetReportPane.vue'
import MonitorPageHeaderPaneMobile from '@/components/MonitorPageHeaderPaneMobile.vue'
import MonitorNetQuestionsPane from '@/components/MonitorNetQuestionsPane.vue'
import MonitorNetQuestionAnswersPane from '@/components/MonitorNetQuestionAnswersPane.vue'
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue } from "@/SelectedNet.js";
import { ref, onMounted, watch } from 'vue'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

var router = useRouter();

onMounted(() => {
  redirect(getToken(), "MonitorMobile", router);
})

</script>
