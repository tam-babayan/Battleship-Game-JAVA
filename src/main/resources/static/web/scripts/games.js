new Vue ({
    el: '#app',

    data: {
        games: [],
        leaderBoard: [],
        currentPlayer: [],
        loggedInPlayyer: ' ',
        isLoggedIn: false

    },

    mounted() {
        this.getGameInfo()
        this.getLeaderBoardInfo()
        this.getPlayerEmail()
    },

    computed: {
        formattedGames() {
            return this.games.map(one => {
                one.date = moment(one.date).format('DD.MM.YYYY HH:mm');
                return one
            })
        }
    },

    methods: {
        getGameInfo() {
            axios
                .get("/api/games")
                .then(response => {
                    this.games = response.data.games
                    this.currentPlayer = response.data.player
//                    console.log(this.gamelist)
                })
                .catch(error => console.log(error))
        },
        getLeaderBoardInfo() {
            axios
                .get("/api/games/scores")
                .then(response => {
                    this.leaderBoard = response.data
                    this.leaderBoard.sort((a, b) => (a.total < b.total) ? 1 : -1);
//                    console.log(this.leaderBoard)
                })
                .catch(error => console.log(error))
        },
        logIn() {
            const form = document.getElementById("form")
            fetch('/api/login', {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: `userName=${ form["name"].value }&password=${ form["pwd"].value }`,
                })
                .then(this.isLoggedIn = true)
                .then(location.reload())

        },
        getPlayerEmail() {
            axios
                .get('/api/games')
                .then(response => {
                    this.loggedInPlayyer = response.data.player.email
                })
                .catch(error => console.log(error))
        }
    }
})