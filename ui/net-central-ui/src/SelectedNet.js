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

export const selectedNet = reactive({ ncSelectedNet: { id: null } });
export function updateSelectedNet(newNet) {
  selectedNet.ncSelectedNet = newNet;
}

export function setSelectedNetSelectionValue(value) {
}

export const selectedNetQuestion = reactive({ value: null });
export function updateSelectedNetQuestion(newValue) {
  selectedNetQuestion.value = newValue;
}

export const netMessageRefresh = reactive({value: ''});
export function forceNetMessageRefresh() {
    const now = new Date();
    const value = now.toLocaleTimeString();
    netMessageRefresh.value = value;
}

export const netQuestionRefresh = reactive({value: ''});
export function forceNetQuestionRefresh() {
    const now = new Date();
    const value = now.toLocaleTimeString();
    netQuestionRefresh.value = value;
}

export const nudgeUpdateNetObject = reactive({ value: null });
export function nudgeUpdateNet(value) {
  nudgeUpdateNetObject.value = value;
}

export const nudgeRemoveNetObject = reactive({ value: null });
export function nudgeRemoveNet(value) {
  nudgeRemoveNetObject.value = value;
}

export const netRefresh = reactive({ value: null });
export function forceNetRefresh() {
    const now = new Date();
    const value = now.toLocaleTimeString();
    netRefresh.value = value;
}
