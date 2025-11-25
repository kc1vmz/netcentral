<script setup>
import MonitorNetSelectPaneMobile from '@/components/MonitorNetSelectPaneMobile.vue'
import { ref, reactive, watch, onMounted } from 'vue';
import { configShowInfrastructure, configShowObjects, configShowTrackedStationsOnly, configShowPriorityObjects, updateShowInfrastructure, updateShowObjects, updateShowTrackedStationsOnly, updateShowPriorityObjects } from "@/ConfigurationDisplay.js";
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { selectedNet, forceNetRefresh } from "@/SelectedNet.js";
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const showPriorityObjectsLabel = reactive({ value : "Show Priority Objects" });
const localSelectedNet = reactive({ncSelectedNet : { callsign : null }});
var netNameRef = ref('');
var netCallsignRef = ref('');
const accesstokenRef = reactive({ value : '' });
const localLoggedInUserRef = reactive({ value : null });

onMounted(() => {
    accesstokenRef.value = getToken();
    localLoggedInUserRef.value = getUser();
})

function updateLocalSelectedNet(newObject) {
  localSelectedNet.ncSelectedNet = newObject.ncSelectedNet;
}

// Watch for changes in the selected object ref
watch(selectedNet, (newSelectedNet, oldSelectedNet) => {
  updateLocalSelectedNet(newSelectedNet);
});

watch(netNameRef, (newValue, oldValue) => {
  if ((newValue != '') && (netCallsignRef.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});
watch(netCallsignRef, (newValue, oldValue) => {
  if ((newValue != '') && (netNameRef.value != '')) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});

</script>

<template>
  <!-- main page -->
  <div>
    <div class="mobilepageheader">Net Monitor</div>
    <MonitorNetSelectPaneMobile/>
  </div>

</template>

<style scoped>
.grid-container {
  display: grid;
  grid-template-columns: 25% 10% 25% 5% 15% 15%;
  margin: 10px;
  gap: 10px;
}

/* The switch - the box around the slider */
.switch {
  position: relative;
  display: inline-block;
  width: 60px;
  height: 34px;
  margin: 0px;
  gap: 0px;
}

/* Hide default HTML checkbox */
.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

/* The slider */
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}

.slider:before {
  position: absolute;
  content: "";
  height: 26px;
  width: 26px;
  left: 4px;
  bottom: 4px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}

input:checked + .slider {
  background-color: #2196F3;
}

input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}

input:checked + .slider:before {
  -webkit-transform: translateX(26px);
  -ms-transform: translateX(26px);
  transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
  border-radius: 34px;
}

.slider.round:before {
  border-radius: 50%;
}
</style>
