import './assets/main.css'

import { createApp } from 'vue'

import App from './App.vue'
//import 'materialize-css/dist/css/materialize.min.css';
//import 'material-icons/iconfont/material-icons.css';
import Vue3EasyDataTable from 'vue3-easy-data-table';
import 'vue3-easy-data-table/dist/style.css';
import Vue3SocketIO from '@hlf01/vue3-socket.io';

import jQuery from "jquery";
const $ = jQuery;
window.$ = $;

import router from './router'

import '@fortawesome/fontawesome-free/js/all'

// createApp(App).component('EasyDataTable', Vue3EasyDataTable).use(router).mount('#app');


const app = createApp(App);

// Configure and use the Socket.IO plugin
app.use(
  new Vue3SocketIO({
    debug: true, // Optional: Enable debug logs
    connection: 'http://localhost:8881', // Replace with your Socket.IO server URL
    // options: { path: "/my-app/" } // Optional: If your server uses a custom path
  })
);
app.component('EasyDataTable', Vue3EasyDataTable);
app.use(router);
app.mount('#app');