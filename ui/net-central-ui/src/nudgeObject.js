import { ref, reactive } from 'vue';

export const nudgeObject = reactive({
    value: null
});

export function nudge(value) {
  nudgeObject.value = value;
}

export const nudgeUpdateObject = reactive({
    value: null
});

export function nudgeUpdate(value) {
  nudgeUpdateObject.value = value;
}

export const nudgeRemoveObject = reactive({
    value: null
});

export function nudgeRemove(value) {
  nudgeRemoveObject.value = value;
}

export const nudgeAddObject = reactive({
    value: null
});

export function nudgeAdd(value) {
  nudgeAddObject.value = value;
}

