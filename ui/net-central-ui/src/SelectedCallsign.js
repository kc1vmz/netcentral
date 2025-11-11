import { reactive } from 'vue';

export const selectedCallsign = reactive({
    value:null
});

export function updateSelectedCallsign(newCallsign) {
  selectedCallsign.value = newCallsign;
}


