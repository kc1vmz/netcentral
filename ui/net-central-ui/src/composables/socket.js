import { io } from "socket.io-client";

export const useSocketIO = () => {
  const socket = io("http://localhost:8881" /* import.meta.env.VITE_APP_UPDATE_URL */); 

  return { socket };
};
