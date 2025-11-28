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
