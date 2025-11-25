export function isMobileClient() {
    if (navigator.maxTouchPoints > 1) {
        return true;
    }
    else {
        return false;
    }
}
