<script>
import SidebarLink from './SidebarLink.vue'
import { collapsed, toggleSidebar, sidebarWidth } from './state.js'
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";

export default {
  props: {},
  components: { SidebarLink },
  setup() {
    return { collapsed, toggleSidebar, sidebarWidth, loggedInUserToken }
  }
}
</script>

<template>
  <div class="sidebar" :style="{ width: sidebarWidth }">
    <span v-if="collapsed"></span>
    <span v-else><div class="menuheader">Net Central</div></span>
    <SidebarLink to="/" icon="fas fa-home">Home</SidebarLink>
    <SidebarLink v-if="((loggedInUserToken != null) && (loggedInUserToken.value != null))" to="/dashboard" icon="fa fa-tachometer">Dashboard</SidebarLink>
    <SidebarLink v-if="((loggedInUserToken != null) && (loggedInUserToken.value != null))" to="/monitor" icon="fas fa-heartbeat">Net Monitor</SidebarLink>
    <SidebarLink v-if="((loggedInUserToken != null) && (loggedInUserToken.value != null))" to="/archives" icon="fa-solid fa-calendar">Archives</SidebarLink>
    <SidebarLink v-if="((loggedInUserToken != null) && (loggedInUserToken.value != null))" to="/explorer" icon="fas fa-folder">Explorer</SidebarLink>
    <SidebarLink v-if="((loggedInUserToken != null) && (loggedInUserToken.value != null))" to="/tools" icon="fas fa-wrench">Tools</SidebarLink>
    <SidebarLink v-if="((loggedInUserToken != null) && (loggedInUserToken.value != null))" to="/setup" icon="fas fa-cog">Setup</SidebarLink>
    <div class="line"><hr/></div>
    <div v-if="((loggedInUserToken == null) || (loggedInUserToken.value == null))">
      <SidebarLink to="/login" icon="fa-solid fa-right-to-bracket">Login</SidebarLink>
      <SidebarLink to="/register" icon="fa-solid fa-pen-to-square">Register</SidebarLink>
    </div>
    <div v-else>
      <SidebarLink to="/logout" icon="fa-solid fa-right-to-bracket fa-rotate-180">Logout</SidebarLink>
    </div>

    <span
      class="collapse-icon"
      :class="{ 'rotate-180': collapsed }"
      @click="toggleSidebar"
    >
      <i class="fas fa-angle-double-left" />
    </span>
  </div>
</template>

<style>
:root {
  --sidebar-bg-color: #2559a7;
  --sidebar-item-hover: #3A6ABA;
  --sidebar-item-active: #2A4A9A;
}
</style>

<style scoped>
.menuheader {
    font-size: 2rem;
    line-height: 110%;
    margin: 1rem 0 1rem 0;
    font-weight: 400;
}

.sidebar {
  color: white;
  background-color: var(--sidebar-bg-color);

  float: left;
  position: fixed;
  z-index: 1;
  top: 0;
  left: 0;
  bottom: 0;
  padding: 0.5em;

  transition: 0.3s ease;

  display: flex;
  flex-direction: column;
}

.sidebar h1 {
  height: 2.5em;
}

.collapse-icon {
  position: absolute;
  bottom: 0;
  padding: 0.75em;

  color: rgba(255, 255, 255, 0.7);

  transition: 0.2s linear;
}

.rotate-180 {
  transform: rotate(180deg);
  transition: 0.2s linear;
}
</style>