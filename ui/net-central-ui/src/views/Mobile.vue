<template>
  <div>
    Mobile only app
  </div>
</template>

<script setup>
import { ref, watch, reactive, onMounted } from 'vue';
import { loggedInUser, loggedInUserToken, updateLoggedInUser, updateLoggedInUserToken, loginPageShow, logoutPageShow, getToken, registerPageShow, getUser, redirect } from "@/LoginInformation.js";
import { useRouter } from 'vue-router';

var router = useRouter();

onMounted(() => {
  redirect(getToken(), "Mobile", router);
})

</script>

<style scoped>
</style>