import { ref, reactive } from 'vue';

export const selectedUser = reactive({
    ncSelectedUser: {
        selected: false
    }
});

export function updateSelectedUser(newUser) {
    selectedUser.ncSelectedUser = newUser.ncSelectedUser;
}

export function setSelectedUserSelectionValue(value) {
    selectedUser.ncSelectedUser.selected = value;
}

export const userRefresh = reactive({value: ''});

export function forceUserRefresh() {
    const now = new Date();
    const value = now.toLocaleTimeString();
    userRefresh.value = value;
}


