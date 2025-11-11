<template>
  <div>
      <PageHeaderPane title="Archived Net Information" description="Information about previous nets, including participants and messages."/>
      <div>
        <splitpanes class="default-theme" horizontal style="height: 100vh; width: 100%" @resized="storePaneSize1" v-model="paneSizes1">
          <pane min-size="10" max-size="60" style="width: 100%" :size="paneSizes1">
            <div class="content">
              <ArchivedNetsListPane/> 
            </div>
          </pane>
          <pane style="width: 100%" :size="100-paneSizes1">
            <splitpanes vertical  @resized="storePaneSize2" v-model="paneSizes21">
              <pane :size="paneSizes21">
                <div class="content">
                  <ArchivedNetDetailsPane/> 
                </div>
              </pane>
              <pane :size="paneSizes22">
                <div class="content">
                  <ArchivedNetParticipantsPane/> 
                </div>
              </pane>
              <pane :size="paneSizes23">
                <div class="content">
                  <ArchivedNetMessagesPane/> 
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
import ArchivedNetsListPane from '@/components/ArchivedNetsListPane.vue'
import ArchivedNetDetailsPane from '@/components/ArchivedNetDetailsPane.vue'
import ArchivedNetParticipantsPane from '@/components/ArchivedNetParticipantsPane.vue'
import ArchivedNetMessagesPane from '@/components/ArchivedNetMessagesPane.vue'
import PageHeaderPane from '@/components/PageHeaderPane.vue'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { selectedObject , updateSelectedObject, setSelectedObjectionSelectionValue } from "@/SelectedObject.js";
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router';

const paneSizes1 = ref(50)
const paneSizes21 = ref(33)
const paneSizes22 = ref(33)
const paneSizes23 = ref(34)
var router = useRouter();

onMounted(() => {
  redirect(getToken(), "Archives", router);
  const storedSizesSetup1 = localStorage.getItem('NetControl-splitpanes-sizes-archives-1')
  if (storedSizesSetup1) {
    paneSizes1.value = JSON.parse(storedSizesSetup1)
  } else {
    paneSizes1.value = 50;
  }
  const storedSizesSetup21 = localStorage.getItem('NetControl-splitpanes-sizes-archives-21')
  if (storedSizesSetup21) {
    paneSizes21.value = JSON.parse(storedSizesSetup21)
  } else {
    paneSizes21.value = 33;
  }
  const storedSizesSetup22 = localStorage.getItem('NetControl-splitpanes-sizes-archives-22')
  if (storedSizesSetup22) {
    paneSizes22.value = JSON.parse(storedSizesSetup22)
  } else {
    paneSizes22.value = 33;
  }
  const storedSizesSetup23 = localStorage.getItem('NetControl-splitpanes-sizes-archives-23')
  if (storedSizesSetup23) {
    paneSizes23.value = JSON.parse(storedSizesSetup23)
  } else {
    paneSizes23.value = 34;
  }
})

const storePaneSize1 = ({ prevPane }) => {
  paneSizes1.value = prevPane.size;
  localStorage.setItem('NetControl-splitpanes-sizes-archives-1', JSON.stringify(paneSizes1.value))
}
const storePaneSize2 = ({ prevPane }) => {
  if (prevPane.index == 0) {
    // moved the first slider
    paneSizes21.value = prevPane.size;
    paneSizes22.value = 100 - paneSizes23.value - paneSizes21.value;
    localStorage.setItem('NetControl-splitpanes-sizes-archives-21', JSON.stringify(paneSizes21.value))
    localStorage.setItem('NetControl-splitpanes-sizes-archives-22', JSON.stringify(paneSizes22.value))
  } else if (prevPane.index == 1) {
    // moved the second slider
    paneSizes22.value = prevPane.size;
    paneSizes23.value = 100 - paneSizes21.value - paneSizes22.value;
    localStorage.setItem('NetControl-splitpanes-sizes-archives-22', JSON.stringify(paneSizes22.value))
    localStorage.setItem('NetControl-splitpanes-sizes-archives-23', JSON.stringify(paneSizes23.value))
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