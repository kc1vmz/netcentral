
<script setup>
import { ref, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const logoutPageShowRef = reactive({ value : false});
var router = useRouter();


onMounted(() => {
    showLogout();
});

function showLogout() {
    logoutPageShowRef.value = true;
}

function logoutYes() {
    logoutPageShow.value = false;
    performLogout();
}

function logoutNo() {
    logoutPageShow.value = false;
}

function performLogout() {
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(getUser())
    };
    fetch(buildNetCentralUrl("/logout"), requestOptions)
      .then(response => {
          updateLoggedInUser(null);
          updateLoggedInUserToken(null);
          router.push('/');
        return response.json()
        })
      .then(data => {
      })
      .catch(error => { console.error('Error logging out of server:', error); })
}
</script>

<template>
    <dialog :open="logoutPageShowRef.value" @close="logoutPageShowRef.value = false" class="topz">  
      <form v-if="logoutPageShowRef.value" method="dialog">
        <div class="pagesubheader">Logout</div>
        <div class="line"><hr/></div>
        Do you wish to log out?
        <br>
        <button class="boxButton" v-on:click.native="logoutYes">Yes</button>
        <button class="boxButton" v-on:click.native="logoutNo">No</button>
      </form>
    </dialog>
</template>
