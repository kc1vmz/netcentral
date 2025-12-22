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
