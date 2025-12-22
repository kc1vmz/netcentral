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

import { ref, reactive } from 'vue';

export const configShowInfrastructure = reactive({
    value: false
});
export const configShowObjects = reactive({
    value: false
});
export const configShowPriorityObjects = reactive({
    value: false
});
export const configShowTrackedStationsOnly = reactive({
    value: false
});

export function updateShowInfrastructure(value) {
  configShowInfrastructure.value = value;
}
export function updateShowObjects(value) {
  configShowObjects.value = value;
}
export function updateShowPriorityObjects(value) {
  configShowPriorityObjects.value = value;
}
export function updateShowTrackedStationsOnly(value) {
  configShowTrackedStationsOnly.value = value;
}

// import { configShowInfrastructure, configShowObjects, configShowTrackedStationsOnly, updateShowInfrastructure, updateShowObjects, updateShowTrackedStationsOnly, configShowTrackedStationsOnly } from "@/ConfigurationDisplay.js";
