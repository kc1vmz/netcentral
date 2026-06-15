/*
    Net Central
    Copyright (c) 2026 John Rokicki KC1VMZ

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

export function getMapServerUrl() {
  var ret = import.meta.env.VITE_APP_MAP_SERVER_URL;
    
  if ((ret == undefined) || (ret == null) || (ret == '')) {
    var osmpcPort = import.meta.env.VITE_APP_OSMPC_HTTP_PORT;
    if ((osmpcPort == undefined) || (osmpcPort == null) || (osmpcPort == '')) {
      ret = "http://localhost:8889/api/v1/tiles/{z}/{x}/{y}";
    } else {
      ret = "http://localhost:"+osmpcPort+"/api/v1/tiles/{z}/{x}/{y}";
    }
  }
  return ret;
}

export function getOSMPCServerUrl() {
  var ret = import.meta.env.VITE_APP_OSMPC_SERVER_URL;
    
  if ((ret == undefined) || (ret == null) || (ret == '')) {
    var osmpcPort = import.meta.env.VITE_APP_OSMPC_HTTP_PORT;
    if ((osmpcPort == undefined) || (osmpcPort == null) || (osmpcPort == '')) {
      ret = "http://localhost:8889";
    } else {
      ret = "http://localhost:"+osmpcPort";
    }
  }
  return ret;
}
