<script setup>
import { selectedCompletedNet } from "@/SelectedCompletedNet.js";
import { ref, watch, reactive } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const dialog = ref(null);

const localSelectedCompletedNet = reactive({value : { id : null }});

function updateLocalSelectedCompletedNet(newObject) {
  localSelectedCompletedNet.value = newObject.value;
}

// Watch for changes in the selected object ref
watch(selectedCompletedNet, (newSelectedCompletedNet, oldSelectedCompletedNet) => {
  updateLocalSelectedCompletedNet(newSelectedCompletedNet);
});

const openDialogFlag = reactive({ value : false });

function generateReport() {
    openDialogFlag.value = true;
    dialog.value.showModal();
}
function generateReportYes() {
    openDialogFlag.value = false;
    window.open(buildNetCentralUrl("/completedNets/"+localSelectedCompletedNet.value.completedNetId+"/partipationReports"), '_blank');
    dialog.value.close(true);
}
function generateReportNo() {
    openDialogFlag.value = false;
    dialog.value.close(false);
}
</script>

<template>
    <!-- dialogs -->
    <dialog :open="openDialogFlag.value" ref="dialog" @close="openDialogFlag.value = false" class="topz">   
      <form v-if="openDialogFlag.value" method="dialog">
        <div class="pagesubheader">Confirm</div>
        <div class="line"><hr/></div>
        Do you wish to generate an ICS 309 report for this net?
        <br>
        <button class="boxButton" v-on:click.native="generateReportYes">Yes</button>
        <button class="boxButton" v-on:click.native="generateReportNo">No</button>
      </form>
    </dialog>
    <!-- main page -->
    <div v-if="((localSelectedCompletedNet != null) && (localSelectedCompletedNet.value != null) && (localSelectedCompletedNet.value.callsign != null))">
      <div class="pagesubheader">Details - {{ localSelectedCompletedNet.value.callsign }}</div>
      <div class="line"><hr/></div>
      <br>Name: {{ localSelectedCompletedNet.value.name }}
      <br>Description: {{ localSelectedCompletedNet.value.description }}
      <br>Voice Frequency: {{ localSelectedCompletedNet.value.vFrequency }}
      <br>Start time: {{ localSelectedCompletedNet.value.prettyStartTime }}
      <br>End time: {{ localSelectedCompletedNet.value.prettyEndTime }}
      <br>
      <br><button class="boxButton" v-on:click.native="generateReport">Generate ICS 309 report</button>
    </div>
    <div v-else>
    </div>
</template>
