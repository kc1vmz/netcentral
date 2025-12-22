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

<template>
  <div>
    <div class="mobilepageheader">How APRS nets work</div>
    <br>Start a net from the web app (not mobile) and let people know about it.  Nets can be announced as bulletins when started.
    <br>Operators can then participate by checking in and out of a net by sending APRS messages to the callsign of the net.
    <p></p>
    <br>List of valid commands sent as APRS messages to the net callsign:
    <br><b>CI</b> - Check in to net
    <br><b>CO</b> - Check out of net
    <br><b>I</b> - Send net info (name, voice frequency, description)
    <br><b>S status</b> - report your status so other participants know what you are doing
    <br><b>T callsign</b> - set tactical callsign for net
    <br><b>F frequency</b> - report your voice freqency that you are monitoring
    <br><b>X n</b> - report your transmit power n in watts
    <br><b>E (BATTERY, COMMERCIAL, GENERATOR, NONE, SOLAR)</b> - report your electrical power type
    <br><b>B (BATTERY, COMMERCIAL, GENERATOR, NONE, SOLAR)</b> - report your backup electrical power type
    <br><b>Y (APPLIANCE, BASE, HANDHELD, INTERNET, MOBILE, OTHER)</b> - report your radio style
    <br><b>M</b> - send message to all participants in net
    <br><b>MC</b> - send message to only net control
    <br><b>R n</b> - replay last n messages sent to the net (n is a number)
    <br><b>L</b> - list net participants, their callsigns, status, location, and voice freqency monitored
    <br><b>P callsign</b> - ping callsign to test connectivity
    <br><b>O callsign</b> - get known operational status of a station by its callsign
    <br><b>Q</b> - send a question to all participants in net
    <br><b>A n</b> - answer question n sent to the net
    <br><b>QL</b> - send a list of unanswered questions
    <br><b>INV callsign[,callsign2,...] </b> - invite one or more callsigns to a net
    <br><b>H</b> - display help message of all commands
    <br>
    <br><b>CI, CO, T, F, X, E, B, and Y</b> can all be combined in a single message  (ex: CI X 50 E COMM B SO)
    <br>
    <br>You must be a registered user on Net Central to start and close nets, but any operator with APRS can participate!
  </div>
</template>

<script setup>
import { ref, watch, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

var router = useRouter();

onMounted(() => {
  redirect(getToken(), "homeMobile", router);
})

</script>

<style scoped>
</style>