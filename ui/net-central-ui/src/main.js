/*
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
*/

import './assets/main.css'

import { createApp } from 'vue'

import App from './App.vue'
import Vue3EasyDataTable from 'vue3-easy-data-table';
import 'vue3-easy-data-table/dist/style.css';
import Vue3SocketIO from '@hlf01/vue3-socket.io';

import jQuery from "jquery";
const $ = jQuery;
window.$ = $;

import router from './router'

import '@fortawesome/fontawesome-free/js/all'

import { getSocketIOUrl } from "@/composables/socket";

const app = createApp(App);

// Configure and use the Socket.IO plugin
app.use(
  new Vue3SocketIO({
    debug: false, // Optional: Enable debug logs
    connection: getSocketIOUrl()
  })
);
app.component('EasyDataTable', Vue3EasyDataTable);
app.use(router);
app.mount('#app');