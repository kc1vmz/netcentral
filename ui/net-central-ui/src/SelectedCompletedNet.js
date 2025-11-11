import { reactive } from 'vue';

export const selectedCompletedNet = reactive({
    value: {
        selected: false
    }
});

export function updateSelectedCompletedNet(newCompletedNet) {
    selectedCompletedNet.value = newCompletedNet;
}

export function setSelectedCompletedNetValue(value) {
    selectedCompletedNet.value.selected = value;
}