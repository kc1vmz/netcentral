export function buildNetCentralUrl(remains) {
  var apiPort = import.meta.env.VITE_APP_API_PORT;
  var apiHost = import.meta.env.VITE_APP_API_HOST;
  
  if ((apiPort == undefined) || (apiPort == null) || (apiPort == '')) {
    apiPort = "8880";
  }
  if ((apiHost == undefined) || (apiHost == null) || (apiHost == '')) {
    apiHost = window.location.hostname;
  }
  return "http://"+apiHost+":"+apiPort+"/api/v1"+remains;
}

