<template>
  <div>
      <PageHeaderPane title="APRS Explorer" description="Information about APRS stations, infrastructure and objects."/>
      <ExplorerTypesPane/> 
      <div>
            <splitpanes vertical class="default-theme" style="height: 100vh; width: 100%" @resized="storePaneSize1" v-model="paneSizes1" >
              <pane min-size="20" max-size="80" :size="paneSizes1">
                <div class="content">
                  <ExplorerResourceMapPane/> 
                </div>
              </pane>
              <pane min-size="20" max-size="80" :size="100-paneSizes1">
                <splitpanes horizontal @resized="storePaneSize2" v-model="paneSizes2" >
                  <pane :size="paneSizes2">
                    <div class="content">
                      <ExplorerResourcesPane/> 
                    </div>
                  </pane>
                  <pane :size="100-paneSizes2">
                    <div class="content">
                      <ExplorerResourceDetailsPane/> 
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
import ExplorerTypesPane from '@/components/ExplorerTypesPane.vue'
import ExplorerResourcesPane from '@/components/ExplorerResourcesPane.vue'
import ExplorerResourceMapPane from '@/components/ExplorerResourceMapPane.vue'
import ExplorerResourceDetailsPane from '@/components/ExplorerResourceDetailsPane.vue'
import PageHeaderPane from '@/components/PageHeaderPane.vue'
import { ref, onMounted, watch } from 'vue'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

const paneSizes1 = ref(50)
const paneSizes2 = ref(50)
var router = useRouter();

onMounted(() => {
  redirect(getToken(), "Explorer", router);
  const storedSizesExplorer1 = localStorage.getItem('NetCentral-splitpanes-sizes-explorer-1')
  if (storedSizesExplorer1) {
    paneSizes1.value = JSON.parse(storedSizesExplorer1)
  } else {
    paneSizes1.value = 50;
  }
  const storedSizesExplorer2 = localStorage.getItem('NetCentral-splitpanes-sizes-explorer-2')
  if (storedSizesExplorer2) {
    paneSizes2.value = JSON.parse(storedSizesExplorer2)
  } else {
    paneSizes2.value = 50;
  }
})

const storePaneSize1 = ({ prevPane }) => {
  paneSizes1.value = prevPane.size;
  localStorage.setItem('NetCentral-splitpanes-sizes-explorer-1', JSON.stringify(paneSizes1.value))
}
const storePaneSize2 = ({ prevPane }) => {
  paneSizes2.value = prevPane.size;
  localStorage.setItem('NetCentral-splitpanes-sizes-explorer-2', JSON.stringify(paneSizes2.value))
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