new Vue ({
    el: '#app',

    data: {
        games: [],
        leaderBoard: []
    },

    mounted() {
        this.getGameInfo()
        this.getLeaderBoardInfo()
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
        },
        getLeaderBoardInfo() {
            axios
                .get("/api/games/scores")
                .then(response => {
                    this.leaderBoard = response.data
                    this.leaderBoard.sort((a, b) => (a.total < b.total) ? 1 : -1);
                    console.log(this.leaderBoard)
                })
                .catch(error => console.log(error))
        }
    }
})