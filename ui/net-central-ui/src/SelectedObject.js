import { ref, reactive } from 'vue';

export const selectedObject = reactive({
    ncSelectedObject: {
        selected: false
    }
});

export function updateSelectedObject(newObject) {
  selectedObject.ncSelectedObject = newObject;
}

export function setSelectedObjectionSelectionValue(value) {
    selectedObject.ncSelectedObject.selected = value;
}