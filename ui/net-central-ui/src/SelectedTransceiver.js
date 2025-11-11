import { ref, reactive } from 'vue';

export const selectedTransceiver = reactive({
    ncSelectedTransceiver: {
        selected: false
    }
});

export function updateSelectedTransceiver(newTransceiver) {
    selectedTransceiver.ncSelectedTransceiver = newTransceiver.ncSelectedTransceiver;
}

export function setSelectedTransceiverSelectionValue(value) {
    selectedTransceiver.ncSelectedTransceiver.selected = value;
}

export const transceiverRefresh = reactive({value: ''});

export function forceTransceiverRefresh() {
    const now = new Date();
    const value = now.toLocaleTimeString();
    transceiverRefresh.value = value;
}


