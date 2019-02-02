new Vue ({
    el: '#app',
    data: {
        gamePlayer: [],
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        columns: [1,2,3,4,5,6,7,8,9,10]
    },
    mounted() {
        this.getGamePlayerInfo()
    },
    methods: {
        getGamePlayerInfo() {
            axios
                .get()
                .then(response => {
                    this.gamePlayer = response
                 })
                .catch(error => console.log(error))
        }
    }
})