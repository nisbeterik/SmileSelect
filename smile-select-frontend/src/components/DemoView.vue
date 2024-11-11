<template>
    <h1>{{ text }}</h1>
    <button @click="helloWorld">Click Here</button>
</template>

<script>


export default {
    data() {
        return {
            text: ""
        }
    },
    methods: {
        async helloWorld() {
            try {
                const response = await this.$axios.get('/helloworld');
                this.text = response.data;
            } catch (error) {
                console.error('Error fetching data:', error);
                this.text = 'Error occurred while fetching data';
            }

        }
    }
}
</script>

<style scoped></style>