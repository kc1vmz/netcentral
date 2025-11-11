import { reactive } from 'vue';

export const selectedObjectType = reactive({
    value: "STATION"
});

export function updateSelectedObjectType(newObject) {
  selectedObjectType.value = newObject;
}
