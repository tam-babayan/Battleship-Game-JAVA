new Vue ({
  el: '#app',

  data: {
    games: [],
    leaderBoard: [],
    currentPlayer: {},
    isLoggedIn: false,
    username: "",
    password: ""
  },

  mounted() {
    this.getGameInfo();
    this.getLeaderBoardInfo();
  },

  computed: {
    formattedGames() {
      return this.games.map(one => {
        one.date = moment(one.date).format("DD.MM.YYYY HH:mm");
        return one;
      });
    }
  },

  methods: {
    getGameInfo() {
      axios
        .get("/api/games")
        .then(response => {
          this.games = response.data.games;
          this.currentPlayer = response.data.player;
          if (this.currentPlayer != null) {
            this.isLoggedIn = true;
          }
        })
        .catch(error => console.log(error));
    },
    getLeaderBoardInfo() {
      axios
        .get("/api/games/scores")
        .then(response => {
          this.leaderBoard = response.data;
          this.leaderBoard.sort((a, b) => (a.total < b.total ? 1 : -1));
        })
        .catch(error => console.log(error));
    },
    handleFormSubmit(e) {
      if (e.target.name == "login") {
        this.logIn();
      } else if (e.target.name == "signup") {
        this.signUp();
      }
    },
    logIn() {
      fetch("/api/login", {
        credentials: "include",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/x-www-form-urlencoded"
        },
//        body: `userName=${ form["name"].value }&password=${ form["pwd"].value }`,
        body: `userName=${this.username}&password=${this.password}`
      }).then(response => {
        if (response.status == 200) {
          this.getGameInfo();
          this.username = "";
          this.password = "";
        }
      });
    },

    validEmail(email) {
      var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(email);
    },

    logOut() {
      const form = document.getElementById("logOutForm");
      fetch("/api/logout", {
        credentials: "include",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/x-www-form-urlencoded"
        }
      }).then(response => {
        this.isLoggedIn = false;
      });
    },

    signUp() {
      if (!this.validEmail(this.username)) {
        alert(this.username + " is not valid email");
        return;
      }

      if (!this.password) {
        alert("password is empty");
        return;
      }
      fetch("/api/players", {
        credentials: "include",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/x-www-form-urlencoded"
        },
        body: `userName=${this.username}&password=${this.password}`
      }).then(response => {
        switch (response.status) {
          case 201:
            this.getLeaderBoardInfo();
            this.username = "";
            this.password = "";
            break;
          case 409:
            alert(this.username + " already exists");
            break;
        }
      });
    }
  }
})