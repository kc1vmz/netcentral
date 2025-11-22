import { io } from "socket.io-client";

export const useSocketIO = () => {
  var updateUrl = import.meta.env.VITE_APP_UPDATE_URL;
  if ((updateUrl == undefined) || (updateUrl == null) || (updateUrl == '')) {
    updateUrl = "http://localhost:8881";
  }
  const socket = io(updateUrl); 

  return { socket };
};
