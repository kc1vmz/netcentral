<template>
  <MonitorPageHeaderPaneMobile/>
  <br><br><MonitorNetParticipantsPane/> 
  <br><br><MonitorNetReportPane/> 
  <br><br><MonitorNetMessagesPane/> 
  <br><br><MonitorNetMapPaneMobile/> 
</template>

<script setup>
import MonitorNetMapPaneMobile from '@/components/MonitorNetMapPaneMobile.vue'
import MonitorNetParticipantsPane from '@/components/MonitorNetParticipantsPane.vue'
import MonitorNetMessagesPane from '@/components/MonitorNetMessagesPane.vue'
import MonitorNetReportPane from '@/components/MonitorNetReportPane.vue'
import MonitorPageHeaderPaneMobile from '@/components/MonitorPageHeaderPaneMobile.vue'
import { selectedNet , updateSelectedNet, setSelectedNetSelectionValue } from "@/SelectedNet.js";
import { ref, onMounted, watch } from 'vue'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

var router = useRouter();

onMounted(() => {
  redirect(getToken(), "MonitorMobile", router);
})

</script>
