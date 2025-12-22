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

import { reactive } from 'vue';

export const loggedInUser = reactive({
    value: null
});
export const loggedInUserToken = reactive({
    value: null
});
export const loginPageShow = reactive({
    value: false
});
export const logoutPageShow = reactive({
    value: false
});
export const registerPageShow = reactive({
    value: false
});

export function updateLoggedInUser(value) {
  var valueAsString = JSON.stringify(value);
  localStorage.setItem("NetCentral-user", valueAsString)
  loggedInUser.value = value;
}

export function getLoggedInUser() {
  return localStorage.getItem("NetCentral-user");
}

export function updateLoggedInUserToken(value) {
  setToken(value)
}

export function getToken() {
  var token = localStorage.getItem("NetCentral-token");
  if (token == "null") {
    token = null;
  }
  if (token != loggedInUserToken.value) {
    loggedInUserToken.value = token;
  }
  return token;
}

export function getUser() {
  var userAsString = localStorage.getItem("NetCentral-user");
  var user = null;
  if (userAsString != "null") {
    user = JSON.parse(userAsString);
  }

  if ((loggedInUser == null) || (loggedInUser.value == null) || (user != loggedInUser.value)) {
    loggedInUser.value = user;
  }
  return user;
}

export function setToken(token) {
  localStorage.setItem("NetCentral-token", token);
  loggedInUserToken.value = token;
}


export function getRefreshToken() {
  var token = localStorage.getItem("NetCentral-refreshtoken");
  if (token == "null") {
    token = null;
  }
  return token
}

export function setRefreshToken(token) {
  localStorage.setItem("NetCentral-refreshtoken", token)
}


export function redirect(token, page, router) {
  if ((token === null) || (token === '')) {
    if (page != null) {
      const pageFixed = btoa(page).toString();
      router.push('/login?redirect=' + pageFixed)
    } else {
      router.push('/login')
    }
  }
}
