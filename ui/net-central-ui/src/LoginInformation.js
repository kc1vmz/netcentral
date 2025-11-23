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
