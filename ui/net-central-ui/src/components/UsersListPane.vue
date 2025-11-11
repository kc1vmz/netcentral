<script setup>
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser } from "@/LoginInformation.js";
import { updateSelectedUser, userRefresh, forceUserRefresh } from "@/SelectedUser.js";
import { reactive, ref, onMounted, watch } from 'vue';
import 'vue3-easy-data-table/dist/style.css';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

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
