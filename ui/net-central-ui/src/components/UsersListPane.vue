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
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser } from "@/LoginInformation.js";
import { updateSelectedUser, userRefresh, forceUserRefresh } from "@/SelectedUser.js";
import { reactive, ref, onMounted, watch } from 'vue';
import 'vue3-easy-data-table/dist/style.css';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";
import { useSocketIO } from "@/composables/socket";
import { updateUserEvent } from "@/UpdateEvents";
import { liveUpdateEnabled, enableLiveUpdate, disableLiveUpdate } from "@/composables/liveUpdate";

const { socket } = useSocketIO();
socket.on("updateUser", (data) => {
  updateUser(data)
});

function updateUser(data) {
    updateUserEvent.value = JSON.parse(data);
}

onMounted(() => {
  accesstoken.value = getToken()
  selectedItem.value = null;
  updateLocalSelectedUser({ncSelectedUser : { id : null }});
  getUsers();
});

const localSelectedUser = reactive({ncSelectedUser : { id : null }});
const selectedItem = ref(null);

var usersRef = reactive({value : []});
var accesstoken = ref('');

watch(userRefresh, (newValue, oldValue) => {
  getUsers();
});

function updateLocalSelectedUser(newUser) {
    localSelectedUser.ncSelectedUser = newUser.ncSelectedUser;
    updateSelectedUser(newUser);
}

watch(updateUserEvent, (newValue, oldValue) => {
  if (!liveUpdateEnabled.value) {
    return;
  }
  if ((newValue.value.action == "Create") || (newValue.value.action == "Delete") || (newValue.value.action == "Update")) {
    getUsers();
  }
});

function getUsers() {
  var requestOptions = {
    method: "GET",
    headers: { "Content-Type": "application/json",
                "SessionID" : accesstoken.value
    },
    body: null
  };
  fetch(buildNetCentralUrl('/users'), requestOptions)
    .then(response => response.json())
    .then(data => {
        usersRef.value = data;

        // find the previously selected list item and update it for everyone else
        if (usersRef.value != null) {
          usersRef.value.forEach(function(user){
              if (user.id == localSelectedUser.ncSelectedUser.id) {
                updateSelectedUser({ ncSelectedUser : user});
              }
          });
        }
    })
    .catch(error => { console.error('Error getting user from server:', error); })
}

function showRow(item) {
    selectedItem.value = item;
    updateLocalSelectedUser({ncSelectedUser : item}); 
}

function getBodyRowClass(item, rowNumber) {
    if ((selectedItem.value != null) && (selectedItem.value.id === item.id)) {
      return 'bold-row';
    }
    return '';
}

const headers = [
              { text: "Login ID", value: "emailAddress", sortable: true },
              { text: "callsign", value: "callsign.callsign", sortable: true},
              { text: "Role", value: "role", sortable: true}];

</script>

<template>
    <EasyDataTable :headers="headers" :items="usersRef.value" 
    :rows-per-page="10"
    @click-row="showRow"
    :body-row-class-name="getBodyRowClass" buttons-pagination
    />
</template>
