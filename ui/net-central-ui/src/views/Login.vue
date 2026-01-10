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
import { ref, reactive, watch, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

var passwordRef = ref('');
var loginidRef = ref('');
const passwordRefLen = computed(() => passwordRef.value.length);
const loginidRefLen = computed(() => loginidRef.value.length);
var enableButtonRef = ref(false);
const accesstokenRef = reactive({ value  : ''});
const redirectValueRef = reactive({ value  : ''});
const errorMessageRef = reactive({ value  : ''});
var router = useRouter();
var route = useRoute();

onMounted(() => {
    loginPageShow.value = true;
});

watch(passwordRef, (newValue, oldValue) => {
  if ((newValue != null) && (newValue.length > 0) && (loginidRefLen.value > 0)) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});
watch(loginidRef, (newValue, oldValue) => {
  if ((newValue != null) && (newValue.length > 0) && (passwordRefLen.value > 0)) {
    enableButtonRef = true;
  } else {
    enableButtonRef = false;
  }
});

function showLogin() {
    loginPageShow.value = true;
}

function loginYes() {
    performLogin();
}

function loginNo() {
    loginPageShow.value = false;
    passwordRef = "";
    loginidRef = "";
}

function performLogin() {
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: loginidRef.value, password: passwordRef.value })
    };
    fetch(buildNetCentralUrl("/login"), requestOptions)
      .then(response => {
        if (response.status != 200)  {
          errorMessageRef.value = response.statusText;
        } else {
          errorMessageRef.value = "";
        }
        return response.json();
      })
      .then(data => {
          if (errorMessageRef.value == "") {
            updateLoggedInUser(data);
            updateLoggedInUserToken(data.accessToken);
            loginPageShow.value = false;
            passwordRef.value = "";
            loginidRef.value = "";
            if ((redirectValueRef.value != undefined) && (redirectValueRef.value != null) && (redirectValueRef.value != '')) {
              router.push('/' + atob(redirectValueRef.value))
            } else {
              router.push('/monitor')
            }
          } else {
            loginPageShow.value = true;  // stay showing
          }
      })
      .catch(error => { console.error('Error logging into server:', error); })
}

function isText(data) {
    return typeof data === 'string';
}

function getRedirect() {
    const redirect = route.query.redirect;

    if (isText(redirect)) {  
        redirectValueRef.value = redirect;
    } else {
        redirectValueRef.value = null;
    }
}

/* execute this on create */
getRedirect();

</script>

<template>
    <dialog :open="loginPageShow.value" ref="dialogLogin" @close="loginPageShow.value = false" class="topz">  
      <form v-if="loginPageShow.value" method="dialog">
        <div class="pagesubheader">Login</div>
        <div class="line"><hr/></div>
        <br>
        Enter your user login id and password to login.
        <br>
        <br>
          <div>
            <label for="loginidField">Login id:</label>
            <input type="text" id="loginidField" v-model="loginidRef"/>
          </div>
          <div>
            <label for="passwordField">Password:</label>
            <input type="password" id="passwordField" v-model="passwordRef"/>
          </div>
          <br>
          <div>
            <b>{{ errorMessageRef.value }}</b>
          </div>
        <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Login</button>
        <button class="boxButton" v-if="enableButtonRef" v-on:click.native="loginYes">Login</button>
        <button class="boxButton" v-on:click.native="loginNo">Cancel</button>
      </form>
    </dialog>
</template>

<style scoped>
</style>
