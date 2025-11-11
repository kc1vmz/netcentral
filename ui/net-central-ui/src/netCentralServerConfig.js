import { ref, reactive } from 'vue';

const netCentralServerUrl = reactive({
    value: "http://localhost:8880/api/v1"
});

export function buildNetCentralUrl(remains) {
  var apiUrl = import.meta.env.VITE_APP_API_URL;
  if ((apiUrl != undefined) && (apiUrl != null) && (apiUrl != '')) {
    netCentralServerUrl.value = apiUrl;
  }
  return netCentralServerUrl.value+remains;
}

