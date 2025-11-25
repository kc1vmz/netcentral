<template>
</template>
<script>
    export default {
        name: 'Init',
        methods: {
            isMobile() {
                return true;
                if( screen.width <= 760 ) {
                    return true;
                }
                else {
                    return false;
                }
            }
        },
        created() {
            if (this.isMobile()) {
                this.$router.push('/homeMobile');
            }
            else {
                this.$router.push('/home');
            }
        }
    }
</script>
<style scoped>
</style>