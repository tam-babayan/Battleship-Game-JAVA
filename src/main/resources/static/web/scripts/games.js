new Vue ({
    el: '#app',

    data: {
        games: [],
    },

    mounted() {
        this.getGameInfo()
    },

    computed: {
        formattedGames() {
            return this.games.map(one => {
                one.date = moment(one.date).format("hh:mm A");
                return one
            })
        }
    },

    methods: {
        getGameInfo() {
            axios
                .get("/api/games")
                .then(response => {
                    this.games = response.data
                })
                .catch(error => console.log(error))
        }
    }
})