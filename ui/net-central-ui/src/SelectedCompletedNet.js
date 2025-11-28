import { reactive } from 'vue';

export const selectedCompletedNet = reactive({ value: { selected: false } });
export function updateSelectedCompletedNet(newCompletedNet) {
    selectedCompletedNet.value = newCompletedNet;
}
export function setSelectedCompletedNetValue(value) {
    selectedCompletedNet.value.selected = value;
}

export const selectedCompletedNetQuestion = reactive({ value: null});
export function updateSelectedCompletedNetQuestion(newCompletedNet) {
    if (newCompletedNet == null) {
        selectedCompletedNetQuestion.value = newCompletedNet;
    } else {
        selectedCompletedNetQuestion.value = newCompletedNet.value;
    }
}
export function setSelectedCompletedNetQuestionValue(value) {
    selectedCompletedNetQuestion.value.selected = value;
}