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
