import { io } from "socket.io-client";

export function getSocketIOUrl() {
  var updatePort = import.meta.env.VITE_APP_UPDATE_PORT;
  var updateHost = import.meta.env.VITE_APP_API_HOST;
  
  if ((updatePort == undefined) || (updatePort == null) || (updatePort == '')) {
    updatePort = "8881";
  }
  if ((updateHost == undefined) || (updateHost == null) || (updateHost == '')) {
    updateHost = window.location.hostname;
  }
  return "http://"+updateHost+":"+updatePort;
}

export const useSocketIO = () => {

  const socket = io(getSocketIOUrl()); 

  return { socket };
};
