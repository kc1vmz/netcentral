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

