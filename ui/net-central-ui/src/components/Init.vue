<template>
</template>
<script>
    export default {
        name: 'Init',
        methods: {
            isMobile() {
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
                this.$router.push('/mobile');
            }
            else {
                this.$router.push('/home');
            }
        }
    }
</script>
<style scoped>
</style>