<script setup>
import { selectedUser , updateSelectedUser, setSelectedUserSelectionValue, forceUserRefresh } from "@/SelectedUser.js";
import { selectedObjectType } from "@/SelectedObjectType.js";
import { selectedObject , updateSelectedObject, setSelectedObjectionSelectionValue } from "@/SelectedObject.js";
import { ref, watch, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser } from "@/LoginInformation.js";
import { Tabs, Tab } from 'super-vue3-tabs';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

const localSelectedUser = reactive({ncSelectedUser : { id : null }});
const accesstokenRef = reactive({value : ''});
const loggedInUserLocalRef = reactive({value : null});

const dialogEditUser = ref(null);
const dialogEditUserShow = reactive({ value : false });

const dialogDeleteUser = ref(null);
const dialogDeleteUserShow = reactive({ value : false });
const dialogPromoteUser = ref(null);
const dialogPromoteUserShow = reactive({ value : false });
const dialogDemoteUser = ref(null);
const dialogDemoteUserShow = reactive({ value : false });
const dialogChangePassword = ref(null);
const dialogChangePasswordShow = reactive({ value : false });
const callsign2Ref = reactive({ value : '' });
const firstName2Ref = reactive({ value : '' });
const lastName2Ref = reactive({ value : '' });
const errorMessageRef = reactive({ value : '' });
var passwordRef = ref(false);
var password2Ref = ref(false);
var enableButtonRef = ref(false);

onMounted(() => {
    accesstokenRef.value = getToken();
    loggedInUserLocalRef.value = getUser();
});


function updateLocalSelectedUser(newUser) {
  localSelectedUser.ncSelectedUser = newUser.ncSelectedUser;
}

// Watch for changes in the selected User ref
watch(selectedUser, (newSelectedUser, oldSelectedUser) => {
  updateLocalSelectedUser(newSelectedUser);
});

watch(passwordRef, (newValue, oldValue) => {
  if ((newValue != '') && (password2Ref.value != '')) {
    enableButtonRef = true;
    if (newValue != password2Ref.value) {
      enableButtonRef = false;
    }
  } else {
    enableButtonRef = false;
  }
});

watch(password2Ref, (newValue, oldValue) => {
  if ((newValue != '') && (passwordRef.value != '')) {
    enableButtonRef = true;
    if (newValue != passwordRef.value) {
      enableButtonRef = false;
    }
  } else {
    enableButtonRef = false;
  }
});

function changePassword() {
    passwordRef.value = '';
    password2Ref.value = '';
    dialogChangePasswordShow.value = true;
}

function changePasswordYes() {
    performChangePassword();
}

function changePasswordNo() {
    dialogChangePasswordShow.value = false;
}

function performChangePassword() {
    var bodyObject = { callsign : localSelectedUser.ncSelectedUser.callsign,
                        firstName : localSelectedUser.ncSelectedUser.firstName,
                        lastName: localSelectedUser.ncSelectedUser.lastName, 
                        id: localSelectedUser.ncSelectedUser.id,
                        emailAddress: localSelectedUser.ncSelectedUser.emailAddress,
                        role: localSelectedUser.ncSelectedUser.role,
                        password : passwordRef.value
                      };
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/users/"+localSelectedUser.ncSelectedUser.id), requestOptions)
      .then(response => {
        if (response.status == 200) {
            // refetch the net list
            forceUserRefresh();
            dialogChangePasswordShow.value = false;
        } else {
          errorMessageRef.value = "An error occurred changing the password.";
        }
        return response.json();
      })
      .then(data => {
          passwordRef.value = '';
          password2Ref.value = '';
      })
      .catch(error => { console.error('Error changing the password:', error); })   
}

function deleteUser() {
    dialogDeleteUserShow.value = true;
}

function deleteUserYes() {
    // perform the delete
    performDeleteUser();
}

function deleteUserNo() {
    dialogDeleteUserShow.value = false;
}

function performDeleteUser() {
    var id = localSelectedUser.ncSelectedUser.id;
    const requestOptions = {
      method: "DELETE",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body:null
    };
    fetch(buildNetCentralUrl("/users/"+id), requestOptions)
      .then(response => {
        if (response.status == 200) {
          updateLocalSelectedUser({localSelectedUser : { localSelectedUser: null }});
          forceUserRefresh();
          dialogDeleteUserShow.value = false;
        }
        return response;
      })
      .then(data => {
      })
      .catch(error => { console.error('Error deleting user:', error); })
}

function promoteUser() {
    dialogPromoteUserShow.value = true;
}

function promoteUserYes() {
    // perform the promotion
    performPromoteUser();
}

function promoteUserNo() {
    dialogPromoteUserShow.value = false;
}

function performPromoteUser() {
    var id = localSelectedUser.ncSelectedUser.id;
    var bodyObject = {
            id : id,
            emailAddress: localSelectedUser.ncSelectedUser.emailAddress,
            callsign : localSelectedUser.ncSelectedUser.callsign,
            role: 'ADMIN',
            firstName: localSelectedUser.ncSelectedUser.firstName,
            lastName: localSelectedUser.ncSelectedUser.lastName
    };
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body:JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/users/"+id), requestOptions)
      .then(response => {
        if (response.status == 200) {
          forceUserRefresh();
          dialogPromoteUserShow.value = false;
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error promoting user:', error); })
}

function demoteUser() {
    dialogDemoteUserShow.value = true;
}

function demoteUserYes() {
    // perform the demotion
    performDemoteUser();
}

function demoteUserNo() {
    dialogDemoteUserShow.value = false;
}

function performDemoteUser() {
    var id = localSelectedUser.ncSelectedUser.id;
    var bodyObject = {
            id : id,
            emailAddress: localSelectedUser.ncSelectedUser.emailAddress,
            callsign : localSelectedUser.ncSelectedUser.callsign,
            role: 'USER',
            firstName: localSelectedUser.ncSelectedUser.firstName,
            lastName: localSelectedUser.ncSelectedUser.lastName
    };
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body:JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/users/"+id), requestOptions)
      .then(response => {
        if (response.status == 200) {
          forceUserRefresh();
          dialogDemoteUserShow.value = false;
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error promoting user:', error); })
}

function editUser() {
    dialogEditUserShow.value = true;
    firstName2Ref.value = localSelectedUser.ncSelectedUser.firstName;
    lastName2Ref.value = localSelectedUser.ncSelectedUser.lastName;
    callsign2Ref.value  = localSelectedUser.ncSelectedUser.callsign.callsign;
}

function editUserYes() {
    // perform the edit
    performEditUser();
}

function editUserNo() {
    dialogEditUserShow.value = false;
}

function performEditUser() {
    var bodyObject = { callsign : { callsign: callsign2Ref.value }, firstName : firstName2Ref.value, lastName: lastName2Ref.value, 
                        id: localSelectedUser.ncSelectedUser.id,
                        emailAddress: localSelectedUser.ncSelectedUser.emailAddress,
                        role: localSelectedUser.ncSelectedUser.role
                      };
    const requestOptions = {
      method: "PUT",
      headers: { "Content-Type": "application/json",
                  "SessionID" : accesstokenRef.value
        },
      body: JSON.stringify(bodyObject)
    };
    fetch(buildNetCentralUrl("/users/"+localSelectedUser.ncSelectedUser.id), requestOptions)
      .then(response => {
        if (response.status == 200) {
            forceUserRefresh();
            dialogEditUserShow.value = false;
            // refetch the net list - poke it somehow
        } else {
          errorMessageRef.value = "An error occurred modifying the user.";
        }
        return response.json();
      })
      .then(data => {
      })
      .catch(error => { console.error('Error modifying user:', error); })   
}
</script>


<template>
  <!-- dialogs -->
    <div v-if="dialogDeleteUserShow.value">
      <teleport to="#modals">
        <dialog :open="dialogDeleteUserShow.value" ref="dialogDeleteUser" @close="dialogDeleteUserShow.value = false" class="topz">  
          <form v-if="dialogDeleteUserShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to delete user {{ localSelectedUser.ncSelectedUser.emailAddress }} ?
            <br>
            <button class="boxButton" v-on:click.native="deleteUserYes">Yes</button>
            <button class="boxButton" v-on:click.native="deleteUserNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogPromoteUserShow.value">
      <teleport to="#modals">
        <dialog :open="dialogPromoteUserShow.value" ref="dialogPromoteUser" @close="dialogPromoteUserShow.value = false" class="topz">  
          <form v-if="dialogPromoteUserShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to promote user {{ localSelectedUser.ncSelectedUser.emailAddress }} to Net Central administrator?
            <br>
            <button class="boxButton" v-on:click.native="promoteUserYes">Yes</button>
            <button class="boxButton" v-on:click.native="promoteUserNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogDemoteUserShow.value">
      <teleport to="#modals">
        <dialog :open="dialogDemoteUserShow.value" ref="dialogDemoteUser" @close="dialogDemoteUserShow.value = false" class="topz">  
          <form v-if="dialogDemoteUserShow.value" method="dialog">
            <div class="pagesubheader">Confirm</div>
            <div class="line"><hr/></div>
            Do you wish to demote user {{ localSelectedUser.ncSelectedUser.emailAddress }} from Net Central administrator?
            <br>
            <button class="boxButton" v-on:click.native="demoteUserYes">Yes</button>
            <button class="boxButton" v-on:click.native="demoteUserNo">No</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogEditUserShow.value">
      <teleport to="#modals">    
        <dialog :open="dialogEditUserShow.value" ref="dialogEditUser" @close="dialogEditUserShow.value = false" class="topz">  
          <form v-if="dialogEditUserShow.value" method="dialog">
            <div class="pagesubheader">Edit User {{ localSelectedUser.ncSelectedUser.emailAddress }}</div>
            <div class="line"><hr/></div>
            Modify the user information.
            <br>
              <div>
                <label for="firstNameField">First name:</label>
                <input type="text" id="firstNameField" v-model="firstName2Ref.value" />
              </div>
              <div>
                <label for="lastNameField">Last name:</label>
                <input type="text" id="lastNameField" v-model="lastName2Ref.value" />
              </div>
              <div>
                <label for="callsignField">Callsign:</label>
                <input type="text" id="callsignField" v-model="callsign2Ref.value" />
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButton" v-on:click.native="editUserYes">Modify</button>
            <button class="boxButton" v-on:click.native="editUserNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>
    <div v-if="dialogChangePasswordShow.value">
      <teleport to="#modals">    
        <dialog :open="dialogChangePasswordShow.value" ref="dialogChangePassword" @close="dialogChangePasswordShow.value = false" class="topz">  
          <form v-if="dialogChangePasswordShow.value" method="dialog">
            <div class="pagesubheader">Change Password</div>
            <div class="line"><hr/></div>
            Change you password below.
            <br>
              <div>
                <label for="passwordField">Password:</label>
                <input type="password" id="passwordField" v-model="passwordRef" />
              </div>
              <div>
                <label for="password2Field">Password again:</label>
                <input type="password" id="password2Field" v-model="password2Ref" />
              </div>
              <div>
                <b>{{ errorMessageRef.value }}</b>
              </div>
            <br>
            <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Change Password</button>
            <button class="boxButton" v-if="enableButtonRef" v-on:click.native="changePasswordYes">Change Password</button>
            <button class="boxButton" v-on:click.native="changePasswordNo">Cancel</button>
          </form>
        </dialog>
      </teleport>
    </div>

    <!-- main page -->
    <div v-if="(localSelectedUser != null) && (localSelectedUser.ncSelectedUser != null) && (localSelectedUser.ncSelectedUser.id != null)">
      <Tabs>
        <Tab value="Details">
          <br>Login ID: {{ localSelectedUser.ncSelectedUser.emailAddress }}
          <br>Name: {{ localSelectedUser.ncSelectedUser.firstName }} {{ localSelectedUser.ncSelectedUser.lastName }}
          <div v-if="((localSelectedUser.ncSelectedUser.callsign != null) && (localSelectedUser.ncSelectedUser.callsign.callsign != null))">
            Callsign: {{ localSelectedUser.ncSelectedUser.callsign.callsign }}
          </div>
        </Tab>
        <Tab value="Actions">
            <div class="grid-container-actions">
                  <!-- begin pair logged in user -->
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && (loggedInUserLocalRef.value != null) && (loggedInUserLocalRef.value.emailAddress == localSelectedUser.ncSelectedUser.emailAddress))">
                    <button class="boxButton" v-on:click.native="changePassword">Change Password</button>
                  </div>
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && (loggedInUserLocalRef.value != null) && (loggedInUserLocalRef.value.emailAddress == localSelectedUser.ncSelectedUser.emailAddress))">
                    Change your password for Net Central.
                  </div>
                  <!-- end pair -->

                  <!-- begin pair logged in user or admin -->
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && (loggedInUserLocalRef.value != null) && ((loggedInUserLocalRef.value.emailAddress == localSelectedUser.ncSelectedUser.emailAddress) || (loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')))">
                    <button class="boxButton" v-on:click.native="editUser">Edit</button>
                  </div>
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && (loggedInUserLocalRef.value != null) && ((loggedInUserLocalRef.value.emailAddress == localSelectedUser.ncSelectedUser.emailAddress) || (loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')))">
                    Edit user information.
                  </div>
                  <!-- end pair -->

                  <!-- begin pair -->
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')) && (localSelectedUser.ncSelectedUser.role == 'USER'))">
                    <button class="boxButton" v-on:click.native="promoteUser">Promote</button>
                  </div>
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')) && (localSelectedUser.ncSelectedUser.role == 'USER'))">
                    Promote the user to an administrator.
                  </div>
                  <!-- end pair -->

                  <!-- begin pair -->
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')) && (localSelectedUser.ncSelectedUser.role == 'ADMIN'))">
                    <button class="boxButton" v-on:click.native="demoteUser">Demote</button>
                  </div>
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')) && (localSelectedUser.ncSelectedUser.role == 'ADMIN'))">
                    Demote the user from administrator to simple user.
                  </div>
                  <!-- end pair -->

                  <!-- begin pair -->
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')))">
                    <button class="boxButton" v-on:click.native="deleteUser">Delete</button>
                  </div>
                  <div class="grid-item" v-if="((accesstokenRef.value != null) && (localSelectedUser.ncSelectedUser != null) && ((loggedInUserLocalRef.value.role == 'ADMIN') || (loggedInUserLocalRef.value.role == 'SYSADMIN')))">
                    Delete the user from Net Central.
                  </div>
                  <!-- end pair -->
            </div>
        </Tab>
      </Tabs>
  </div>

</template>

<style scoped>
.grid-container-actions {
  display: grid;
  grid-template-columns: 30% 70%;
  margin: 5px;
  gap: 5px;
}
</style>