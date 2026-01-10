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
        <br>
        Do you wish to log out?
        <br>
        <br>
        <button class="boxButton" v-on:click.native="logoutYes">Yes</button>
        <button class="boxButton" v-on:click.native="logoutNo">No</button>
      </form>
    </dialog>
</template>
