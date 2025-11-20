import { reactive } from "vue";
export const liveUpdateEnabled = reactive({ value: true });

export function enableLiveUpdate() {
    liveUpdateEnabled.value = true;
}

export function disableLiveUpdate() {
    liveUpdateEnabled.value = false;
}
