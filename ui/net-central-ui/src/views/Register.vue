<script setup>
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, getUser, redirect } from "@/LoginInformation.js";
import { ref, reactive, computed, watch, onMounted} from 'vue';
import { useRouter } from 'vue-router';
import { buildNetCentralUrl } from "@/netCentralServerConfig.js";

var loginidRef = ref('');
const loginidRefLen = computed(() => loginidRef.value.length);
var passwordRef = ref('');
const passwordRefLen = computed(() => passwordRef.value.length);
var password2Ref = ref('');
const password2RefLen = computed(() => password2Ref.value.length);
var callsignRef = ref('');
var enableButtonRef = ref(false);

var dialogRegister = ref(null);
const accesstokenRef = reactive({ value : ''});
const errorMessageRef = reactive({ value : ''});
const registerPageShow = reactive({ value : false});

var router = useRouter();

onMounted(() => {
    accesstokenRef.value = getToken();
    registerPageShow.value = true;
});

watch(loginidRef, (newValue, oldValue) => {
  if ((newValue != '') && (passwordRefLen.value > 0) && (password2RefLen.value > 0)) {
    enableButtonRef = true;
    if (passwordRef.value != password2Ref.value) {
      enableButtonRef = false;
    }
  } else {
    enableButtonRef = false;
  }
});
watch(passwordRef, (newValue, oldValue) => {
  if ((newValue != '') && (loginidRefLen.value > 0) && (password2RefLen.value > 0)) {
    enableButtonRef = true;
    if (passwordRef.value != password2Ref.value) {
      enableButtonRef = false;
    }
  } else {
    enableButtonRef = false;
  }
});
watch(password2Ref, (newValue, oldValue) => {
  if ((newValue != '') && (passwordRefLen.value > 0) && ( loginidRefLen.value > 0)) {
    enableButtonRef = true;
    if (passwordRef.value != password2Ref.value) {
      enableButtonRef = false;
    }
  } else {
    enableButtonRef = false;
  }
});

function showRegister() {
      registerPageShow.value = true;
}

function registerYes() {
    performRegister();
}

function registerNo() {
    registerPageShow.value = false;
    passwordRef.value = "";
    loginidRef.value = "";
    password2Ref.value = "";
    callsignRef.value = "";
}

function performRegister() {
    const requestOptions = {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: loginidRef.value, password: passwordRef.value, password2: password2Ref.value, callsign: callsignRef.value })
    };
    fetch(buildNetCentralUrl("/users/register"), requestOptions)
      .then(response => {
        if (response.status == 400) {
          errorMessageRef.value = "Review provided information and try again.";
        } else if (response.status == 200) {
          errorMessageRef.value = "";
        } else {
          errorMessageRef.value = "An error occurred."
        }
        return response.json();
      })
      .then(data => {
          if (errorMessageRef.value == "") {
            passwordRef.value = "";
            loginidRef.value = "";
            password2Ref.value = "";
            callsignRef.value = "";
            registerPageShow.value = false;
            router.push('Monitor');
          } else {
            registerPageShow.value = true;  // stay showing
          }
      })
      .catch(error => { console.error('Error registering user on server:', error); })
}
</script>

<template>
    <!-- dialogs -->
    <dialog :open="registerPageShow.value" ref="dialogRegister" @close="registerPageShow.value = false" class="topz">  
      <form v-if="registerPageShow.value" method="dialog">
        <div class="pagesubheader">Register User</div>
        <div class="line"><hr/></div>
        If you have not previously registered with this Net Central server, you can still use it via APRS but you cannot manage nets on this site. <br>
        If you wish to register for access, provide a username (preferrably your email address) and a password specific for using Net Central.
        <br>
          <div>
            <label for="loginidField">Login id:</label>
            <input type="text" id="loginidField" v-model="loginidRef" />
          </div>
          <div>
            <label for="passwordField">Password:</label>
            <input type="password" id="passwordField" v-model="passwordRef" />
          </div>
          <div>
            <label for="passwordField2">Password again:</label>
            <input type="password" id="passwordField2" v-model="password2Ref" />
          </div>
          <div>
            <label for="callsignField">Callsign (optional):</label>
            <input type="text" id="callsignField" maxlength="9" v-model="callsignRef" />
          </div>
          <div>
            <b>{{ errorMessageRef.value }}</b>
          </div>
        <button class="boxButtonDisabled" v-if="!enableButtonRef" disabled>Register</button>
        <button class="boxButton" v-if="enableButtonRef" v-on:click.native="registerYes">Register</button>
        <button class="boxButton" v-on:click.native="registerNo">Cancel</button>
      </form>
    </dialog>
</template>


