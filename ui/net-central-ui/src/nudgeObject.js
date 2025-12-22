/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

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

