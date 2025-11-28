<script setup>
import { selectedCompletedNet } from "@/SelectedCompletedNet.js";
import { ref, watch, reactive } from 'vue';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";


const localSelectedCompletedNet = reactive({value : { id : null }});

function updateLocalSelectedCompletedNet(newObject) {
  localSelectedCompletedNet.value = newObject.value;
}

// Watch for changes in the selected object ref
watch(selectedCompletedNet, (newSelectedCompletedNet, oldSelectedCompletedNet) => {
  updateLocalSelectedCompletedNet(newSelectedCompletedNet);
});

const openDialogFlag = reactive({ value : false });
const openDialogRef = ref(null);
const openMessageDialogFlag = reactive({ value : false });
const openMessageDialogRef = ref(null);
const openQuestionAnswerDialogFlag = reactive({ value : false });
const openQuestionAnswerDialogRef = ref(null);

function generateReport() {
    openDialogFlag.value = true;
}
function generateReportYes() {
    openDialogFlag.value = false;
    window.open(buildNetCentralUrl("/completedNets/"+localSelectedCompletedNet.value.completedNetId+"/partipationReports"), '_blank');
}
function generateReportNo() {
    openDialogFlag.value = false;
}
function generateMessageReport() {
    openMessageDialogFlag.value = true;
}
function generateMessageReportYes() {
    openMessageDialogFlag.value = false;
    window.open(buildNetCentralUrl("/completedNets/"+localSelectedCompletedNet.value.completedNetId+"/messageReports"), '_blank');
}
function generateMessageReportNo() {
    openMessageDialogFlag.value = false;
}
function generateQuestionAnswerReport() {
    openQuestionAnswerDialogFlag.value = true;
}
function generateQuestionAnswerReportYes() {
    openQuestionAnswerDialogFlag.value = false;
    window.open(buildNetCentralUrl("/completedNets/"+localSelectedCompletedNet.value.completedNetId+"/questionAnswerReports"), '_blank');
}
function generateQuestionAnswerReportNo() {
    openQuestionAnswerDialogFlag.value = false;
}
</script>

<template>
    <!-- dialogs -->
    <dialog :open="openDialogFlag.value" ref="openDialogRef" @close="openDialogFlag.value = false" class="topz">   
      <form v-if="openDialogFlag.value" method="dialog">
        <div class="pagesubheader">Confirm</div>
        <div class="line"><hr/></div>
        Do you wish to generate an ICS 309 report for this net?
        <br>
        <button class="boxButton" v-on:click.native="generateReportYes">Yes</button>
        <button class="boxButton" v-on:click.native="generateReportNo">No</button>
      </form>
    </dialog>
    <dialog :open="openMessageDialogFlag.value" ref="openMessageDialogRef" @close="openMessageDialogFlag.value = false" class="topz">   
      <form v-if="openMessageDialogFlag.value" method="dialog">
        <div class="pagesubheader">Confirm</div>
        <div class="line"><hr/></div>
        Do you wish to generate a message report for this net?
        <br>
        <button class="boxButton" v-on:click.native="generateMessageReportYes">Yes</button>
        <button class="boxButton" v-on:click.native="generateMessageReportNo">No</button>
      </form>
    </dialog>
    <dialog :open="openQuestionAnswerDialogFlag.value" ref="openQuestionAnswerDialogRef" @close="openQuestionAnswerDialogFlag.value = false" class="topz">   
      <form v-if="openQuestionAnswerDialogFlag.value" method="dialog">
        <div class="pagesubheader">Confirm</div>
        <div class="line"><hr/></div>
        Do you wish to generate a question and answer report for this net?
        <br>
        <button class="boxButton" v-on:click.native="generateQuestionAnswerReportYes">Yes</button>
        <button class="boxButton" v-on:click.native="generateQuestionAnswerReportNo">No</button>
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
      <br><button class="boxButton" v-on:click.native="generateMessageReport">Generate message report</button>
      <br><button class="boxButton" v-on:click.native="generateQuestionAnswerReport">Generate Q&A report</button>
    </div>
    <div v-else>
    </div>
</template>
