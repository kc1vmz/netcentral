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

import { reactive } from "vue";

function generateValue() {
    const now = new Date();
    return now.toLocaleTimeString();
 }

export const updateDashboardEvent = reactive({ value: "" });
export function updateDashboard() {
    updateDashboardEvent.value = generateValue();
}

export const updateNetEvent = reactive({ value: null });
export function updateNet(payload) {
    updateNetEvent.value = JSON.parse(payload);
}

export const updateNetMessageEvent = reactive({ value: null });
export function updateNetMessage(payload) {
    updateNetMessageEvent.value = JSON.parse(payload);
}
export const updateNetQuestionEvent = reactive({ value: null });
export function updateNetQuestion(payload) {
    updateNetQuestionEvent.value = JSON.parse(payload);
}
export const updateNetQuestionAnswerEvent = reactive({ value: null });
export function updateNetQuestionAnswer(payload) {
    updateNetQuestionAnswerEvent.value = JSON.parse(payload);
}
export const updateNetParticipantEvent = reactive({ value: null });
export function updateNetParticipant(payload) {
    updateNetParticipantEvent.value = JSON.parse(payload);
}

export const updateScheduledNetEvent = reactive({ value: null });
export function updateScheduledNet(payload) {
    updateScheduledNetEvent.value = JSON.parse(payload);
}

export const updateCompletedNetEvent = reactive({ value: "" });
export function updateCompletedNet() {
    updateCompletedNetEvent.value = generateValue();
}

export const updateParticipantEvent = reactive({ value: null });
export function updateParticipant(payload) {
    updateParticipantEvent.value = JSON.parse(payload);
}

export const updateTransceiverEvent = reactive({ value: "" });
export function updateTransceiver() {
    updateTransceiverEvent.value = generateValue();
}

export const updateUserEvent = reactive({ value: "" });
export function updateUser() {
    updateUserEvent.value = generateValue();
}

export const updateCallsignEvent = reactive({ value: null });
export function updateCallsign(payload) {
    updateCallsignEvent.value = JSON.parse(payload);
}

export const updateTrackedStationEvent = reactive({ value: "" });
export function updateTrackedStation() {
    updateTrackedStationEvent.value = generateValue();
}

export const updateObjectEvent = reactive({ value: "" });
export function updateObject() {
    updateObjectEvent.value = generateValue();
}

export const updateAllEvent = reactive({ value: "" });
export function updateAll() {
    updateAllEvent.value = generateValue();
}

export const updateIgnoredEvent = reactive({ value: null });
export function updateIgnored(payload) {
    updateIgnoredEvent.value = JSON.parse(payload);
}

export const updateWeatherReportEvent = reactive({ value: null });
export function updateWeatherReport(payload) {
    updateWeatherReportEvent.value = JSON.parse(payload);
}

export const updateCallsignACEEvent = reactive({ value: null });
export function updateCallsignACE(payload) {
    if (payload == null) {
        updateCallsignACEEvent.value = null;
    } else {
        updateCallsignACEEvent.value = JSON.parse(payload);
    }
}

