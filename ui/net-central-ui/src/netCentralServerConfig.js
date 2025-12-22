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

