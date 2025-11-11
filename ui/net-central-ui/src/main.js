import './assets/main.css'

import { createApp } from 'vue'

import App from './App.vue'
//import 'materialize-css/dist/css/materialize.min.css';
//import 'material-icons/iconfont/material-icons.css';
import Vue3EasyDataTable from 'vue3-easy-data-table';
import 'vue3-easy-data-table/dist/style.css';

import jQuery from "jquery";
const $ = jQuery;
window.$ = $;

import router from './router'

import '@fortawesome/fontawesome-free/js/all'

createApp(App).component('EasyDataTable', Vue3EasyDataTable).use(router).mount('#app');
