new Vue ({
    el: "#app",
    data: {
        gameInfo: [],
        errorMessage: null,
        ships: [],
        salvoes: [],
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        columns: ["", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
        playerId: null,
        gamePlayerId: null,
        shipTypes: [{name: "Carrier", length: 5, isPlaced: false},
                    {name: "Battleship", length: 4, isPlaced: false},
                    {name: "Submarine", length: 3, isPlaced: false},
                    {name: "Destroyer", length: 3, isPlaced: false},
                    {name: "Patrol Boat", length: 2, isPlaced: false}],
        shipInProcess: {
            name: null,
            length: 0,
        },
        currentShipPositions: [],
        isVertical: false
    },

    mounted() {
      this.initParams()
      this.getCurrentPlayerId();
    },

    computed: {
    board() {
      let array = [];
      for (let i = 0; i < this.rows.length; i++) {
        let rowObject = { rowId: this.rows[i], column: [] };
        for (let j = 1; j < this.columns.length; j++) {
          let columnObject = {
            columnId: this.columns[j],
            ship: this.getShipCells(this.rows[i] + this.columns[j]),
            hoveredCell: this.currentShipPositions.includes(this.rows[i] + this.columns[j]),
            mySalvoes: this.getMySalvoCell(this.rows[i] + this.columns[j]),
            oponentSalvoes: this.getOponentSalvoes(
              this.rows[i] + this.columns[j]
            ),
            salvoTurn: this.getTurn(this.rows[i] + this.columns[j])
          };
          rowObject.column.push(columnObject);
        }
        array.push(rowObject);
      }
      return array;
    }
    },

    methods: {

    // get the gamePlayerId from url
    initParams() {
      let url = new URL(window.location.href);
      this.gamePlayerId = url.searchParams.get("gp");
    },

    // get the logged in user id and calls a function to get game info
    getCurrentPlayerId() {
      axios
        .get("/api/games")
        .then(response => {
            if(response.data.player.id != null) {
              this.playerId = response.data.player.id;
              this.getGameInfo()
            }
        })
        .catch(error => console.log(error));
    },

    // requests ship and salvo info
    getGameInfo() {
      axios
        .get("/api/game_view/" + this.gamePlayerId)
        .then(response => {
            this.gameInfo = response.data;
            this.ships = this.gameInfo.ships;
            this.salvoes = this.gameInfo.salvoes;
            this.updateShipsToPlace ()
        })
        .catch(error => {
            this.errorMessage = error.response.data.error;
        })
    },

    getShipCells(coordinate) {
      for (let i = 0; i < this.ships.length; i++) {
        if (this.ships[i].locations.includes(coordinate)) {
          return true;
        }
      }
      return false;
    },

    getMySalvoCell(coordinate) {
      for (let i = 0; i < this.salvoes.length; i++) {
        if (
          this.salvoes[i].player === this.playerId &&
          this.salvoes[i].locations.includes(coordinate)
        ) {
          return true;
        }
      }
      return false;
    },

    getOponentSalvoes(coordinate) {
      for (let i = 0; i < this.salvoes.length; i++) {
        if (
          this.salvoes[i].player != this.playerId &&
          this.salvoes[i].locations.includes(coordinate)
        ) {
          return true;
        }
      }
      return false;
    },

    getTurn(coordinate) {
      for (let i = 0; i < this.salvoes.length; i++) {
        if (this.salvoes[i].locations.includes(coordinate)) {
          return this.salvoes[i].turn;
        }
      }
      return null;
    },


    logOut() {
      fetch("/api/logout", {
        credentials: "include",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/x-www-form-urlencoded"
        }
      }).then(window.location.replace("/web/games.html"));
    },


    // posts the data on click
    addShips () {
        if (this.currentShipPositions.length) {
            fetch ('/games/players/' + this.gamePlayerId + '/ships', {
                credentials: "include",
                method: "POST",
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json"
                },
                body: JSON.stringify([{shipType: this.shipInProcess.name , shipLocations: this.currentShipPositions}])
            })
            .then (response => this.getGameInfo())
            .then (this.changePlacedStatus(this.shipInProcess.name))
            .catch (error => console.log(error))
        }
    },


    // changes the isPlaced status of placed ships
    changePlacedStatus (type) {
        this.shipInProcess.name = null
        this.shipInProcess.length = 0
        this.shipTypes.map(ship => {
            if (type == ship.name) {
                ship.isPlaced = true
            }}
        )
    },

    //gets the possible ship positions array on mouseover event
    updatePossibleShipPosition(a, b) {
        let shipLength = this.shipInProcess.length
        let isVertical = this.isVertical
        this.currentShipPositions = []
        for (let i = 0; i < shipLength; i ++) {
            let letter = !isVertical ? a : this.rows[this.rows.indexOf(a)+i]
            let number = !isVertical ? parseInt(b + i) : b
            if (!isVertical && number > 10) {
                number = 10 - i
            }
            if (isVertical && !letter) {
                letter = this.rows[9 - i]
            }
            let coordinate = letter + number

            if (this.getShipCells(coordinate)) {
                this.currentShipPositions = []
                return
            }

            this.currentShipPositions[i] = coordinate
        }
    },

    // changes the ship orientation
    placeVerticalShip () {
        this.isVertical = !this.isVertical
    },


    // get the ship info on clicking on the ships
    getShipInProcess (shipName, shipLength, status) {
        if (!status) {
            this.shipInProcess.name = shipName
            this.shipInProcess.length = shipLength
            console.log(this.shipInProcess)
        }
    },

    // changes isPlace status after the page reload for those ships, that are already placed
    updateShipsToPlace () {
        this.ships.forEach(ship => {
            this.shipTypes.map(shipType => {
                if (shipType.name == ship.type) {
                    shipType.isPlaced = true
                }}
            )
        })
    }
}
})