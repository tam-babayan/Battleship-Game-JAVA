new Vue({
  el: "#app",
  data: {
    gameInfo: [],
    errorMessage: null,
    ships: [],
    salvos: [],
    rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
    columns: ["", 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
    playerId: null,
    gamePlayerId: null,
    gamePlayers: [],
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
    overlapShipPositions: [],
    isVertical: false,
    salvoInProcess: [],
    salvoTurn: 1,
    salvoCounter: 5,
    opponentShips: [],
    opponentSunkShips: [],
    hits: [],
    gameEnded: false
  },

  mounted() {
    this.initParams();
    setInterval(() => {
      this.getGameInfo()
    }, 3000);
  },

  computed: {

    board() {
      let array = [];
      this.rows.forEach(row => {
        let rowObject = {rowId: row, column: []};
        this.columns.forEach(column => {
          let coordinate = row + column;
          let columnObject = {
            columnId: column,
            ship: this.isShipCells(coordinate),
            hoveredCell: this.currentShipPositions.includes(coordinate),
            overlapCell: this.overlapShipPositions.includes(coordinate),
            salvoInProcessCell: this.salvoInProcess.includes(coordinate),
            mySalvos: this.isMySalvoCell(coordinate),
            opponentSalvos: this.isOpponentSalvos(coordinate),
            opponentShip: this.isOpponentShips(coordinate),
            opponentSunkShips: this.getOpponentSunkShips(coordinate),
            mySunkShips: this.isMySunkShip(coordinate),
            mySalvoTurn: this.getMySalvoTurn(coordinate),
            opponentSalvoTurn: this.getOpponentSalvoTurn(coordinate)
          };
          rowObject.column.push(columnObject);
        });
        array.push(rowObject);
      });

      return array;
    },

    hitShips() {
      let hitShips = [];
      this.ships.forEach(ship => {
        let left = ship.locations.length;
        if (this.hits[ship.type]) {
          left = left - this.hits[ship.type].locations.length
        }
        if (left === 0) {
          hitShips.push({ship: ship.type, left: left, sunk: "X", turn: this.hits[ship.type].turn, locations: ship.locations});
        } else {
          hitShips.push({ship: ship.type, left: left});
        }
      });
      hitShips.sort((a, b) => (a.ship > b.ship) ? 1 : ((b.ship > a.ship) ? -1 : 0));
      return hitShips
    },

    areAllShipsPlaced() {
      let areAllShipsPlaced = false;
      let arr = this.shipTypes.filter(ship => ship.isPlaced === false);
      if (arr.length === 0) {
        areAllShipsPlaced = true;
      }
      return areAllShipsPlaced
    }
  },

  methods: {

    // get the gamePlayerId from url
    initParams() {
      let url = new URL(window.location.href);
      this.gamePlayerId = url.searchParams.get("gp");
      this.getGameInfo()
    },

    // requests ship and salvo info
    getGameInfo() {
      axios
          .get("/api/game_view/" + this.gamePlayerId)
          .then(response => {
            this.gameInfo = response.data;
            this.gamePlayers = this.sortGamePlayers(this.gameInfo.gamePlayers, this.gamePlayerId);
            this.ships = this.gameInfo.ships;
            this.salvos = this.gameInfo.salvos;
            this.opponentShips = this.gameInfo.opponentShips;
            this.updateShipsToPlace();
            this.updateSalvoTurn();
            this.getHitInfo();
          })
          .catch(error => {
            this.errorMessage = error.response.data.error;
          })
    },

    sortGamePlayers (unsortedGamePlayers, playerID) {
      let sortedGamePlayers = []
      unsortedGamePlayers.forEach(gamePlayer => {
        if (gamePlayer.id == playerID) {
          sortedGamePlayers[0] = gamePlayer
        }
        else {
          sortedGamePlayers[1] = gamePlayer
        }
      });
      return sortedGamePlayers;
    },

    isShipCells(coordinate) {
      let isShip = false;
      this.ships.forEach(ship => {
        if (ship.locations.includes(coordinate)) {
          isShip = true;
        }
      });
      return isShip;
    },

    isMySalvoCell(coordinate) {
      let isMySalvo = false;
      this.salvos.forEach(salvo => {
        if (salvo.player == this.gamePlayerId && salvo.locations.includes(coordinate)) {
          isMySalvo = true;
        }
      });
      return isMySalvo;
    },

    isOpponentSalvos(coordinate) {
      let isOpponentSalvo = false;
      this.salvos.forEach(salvo => {
        if (salvo.player != this.gamePlayerId &&
            salvo.locations.includes(coordinate) &&
            this.isShipCells(coordinate)) {
          isOpponentSalvo = true;
        }
      })
      return isOpponentSalvo;
    },

    getMySalvoTurn(coordinate) {
      let muSalvoTurn = null;
      this.salvos.forEach(salvo => {
        if (salvo.player == this.gamePlayerId &&
            salvo.locations.includes(coordinate)) {
          muSalvoTurn = salvo.turn;
        }
      });
      return muSalvoTurn;
    },

    getOpponentSalvoTurn(coordinate) {
      let opponentSalvoTurn = null;
      this.salvos.forEach(salvo => {
        if (salvo.player != this.gamePlayerId &&
            salvo.locations.includes(coordinate) &&
            this.isShipCells(coordinate)) {
          opponentSalvoTurn = salvo.turn;
        }
      });
      return opponentSalvoTurn;
    },

    isOpponentShips(coordinate) {
      let isOpponentShip = false;
      this.opponentShips.forEach(opponentShip => {
        if (opponentShip.locations.includes(coordinate)) {
          isOpponentShip = true;
        }
      });
      return isOpponentShip;
    },

    getOpponentSunkShips(coordinate) {
      let isOpponentSunkShip = false;
      this.opponentShips.forEach(opponentShip => {
        if (opponentShip.type != null && opponentShip.locations.includes(coordinate)) {
          isOpponentSunkShip = true;
        }
      });
      return isOpponentSunkShip;
    },

    isMySunkShip(coordinate) {
      let isMySunkShip = false;
      this.hitShips.forEach(hitShip => {
        if (hitShip.locations != null && hitShip.locations.includes(coordinate)) {
          isMySunkShip = true;
        }
      });
      return isMySunkShip;
    },

    logOut() {
      fetch("/api/logout", {
        credentials: "include",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/x-www-form-urlencoded"
        }
      })
          .then(window.location.replace("/web/games.html"))
          .catch(error => console.log(error));
    },

    // posts ship data on click
    addShip() {
      if (this.currentShipPositions.length) {
        fetch('/games/players/' + this.gamePlayerId + '/ships', {
          credentials: "include",
          method: "POST",
          headers: {
            Accept: "application/json",
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            shipType: this.shipInProcess.name,
            shipLocations: this.currentShipPositions,
          })
        })
            .then(response => this.getGameInfo())
            .then(this.changePlacedStatus(this.shipInProcess.name))
            .catch(error => console.log(error))
      }
    },

    // changes the isPlaced status of placed ships
    changePlacedStatus(type) {
      this.shipInProcess.name = null;
      this.shipInProcess.length = 0;
      this.shipTypes.map(ship => {
            if (type === ship.name) {
              ship.isPlaced = true
            }
          }
      )
    },

    //gets the possible ship coordinates array on mouseover event
    updatePossibleShipPosition(a, b) {
      let shipLength = this.shipInProcess.length;
      let isVertical = this.isVertical;
      this.currentShipPositions = [];
      let isOverlap = false;
      for (let i = 0; i < shipLength; i++) {
        let letter = !isVertical ? a : this.rows[this.rows.indexOf(a) + i];
        let number = !isVertical ? parseInt(b + i) : b;
        if (!isVertical && number > 10) {
          number = 10 - i
        }
        if (isVertical && !letter) {
          letter = this.rows[9 - i]
        }
        let coordinate = letter + number;

        if (this.isShipCells(coordinate) || this.isBorderedShipPlaced(letter, number)) {
          isOverlap = true
        }

        this.currentShipPositions[i] = coordinate
      }
      if (isOverlap) {
        this.overlapShipPositions = this.currentShipPositions;
        this.currentShipPositions = []
      } else {
        this.overlapShipPositions = []
      }
    },

    // changes the ship orientation
    placeVerticalShip() {
      this.isVertical = !this.isVertical
    },

    isBorderedShipPlaced(letter, number) {
      let arr = [];
      let isBorderedCell = false;
      arr.push(this.rows[this.rows.indexOf(letter) - 1] + parseInt(number));
      arr.push(this.rows[this.rows.indexOf(letter) + 1] + parseInt(number));
      arr.push(letter + parseInt(number + 1));
      arr.push(letter + parseInt(number - 1));
      arr.forEach(element => {
        if (this.isShipCells(element)) {
          isBorderedCell = true;
        }
      });
      return isBorderedCell;
    },

    // gets the ship info on clicking on the ships
    setShipInProcess(shipName, shipLength, status) {
      if (!status) {
        this.shipInProcess.name = shipName;
        this.shipInProcess.length = shipLength;
      }
    },

    // changes isPlace status after the page reload for those ships, that are already placed
    updateShipsToPlace() {
      this.ships.forEach(ship => {
        this.shipTypes.map(shipType => {
              if (shipType.name == ship.type) {
                shipType.isPlaced = true
              }
            }
        )
      })
    },

    setSalvoInProcess(letter, number) {
      let coordinate = letter + number;
      if (!this.salvoInProcess.includes(coordinate) && this.salvoInProcess.length <= 4 && !this.isSalvoPlaced(coordinate)) {
        this.salvoInProcess.push(coordinate);
        this.salvoCounter--
      } else if (this.salvoInProcess.includes(coordinate)) {
        this.salvoInProcess.splice(this.salvoInProcess.indexOf(coordinate), 1);
        this.salvoCounter++
      }
    },

    isSalvoPlaced(coordinate) {
      let isPlaced = false;
      this.salvos.forEach(salvo => {
        if (salvo.player == this.gamePlayerId && salvo.locations.includes(coordinate)) {
          isPlaced = true;
        }
      });
      return isPlaced;
    },

    updateSalvoTurn() {
      this.salvoTurn = Math.max(this.salvoTurn, Math.max.apply(Math, this.salvos
          .filter(salvo => salvo.player == this.gamePlayerId)
          .map(salvo => {
            return salvo.turn
          })) + 1)
    },

    // posts salvo data on command Fire
    addSalvo() {
      fetch('/games/players/' + this.gamePlayerId + '/salvos', {
        credentials: "include",
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json"
        },
        body: JSON.stringify({salvoLocations: this.salvoInProcess, turn: this.salvoTurn})
      })
          .then(response => {
            if (response.ok) {
              this.salvoTurn = this.salvoTurn + 1;
              this.getGameInfo()
            }
            this.salvoInProcess = [];
            this.salvoCounter = 5
          })
          .catch(error => console.log(error))
    },

    getHitInfo() {
      this.hits = [];
      this.ships.forEach(ship => {
        ship.locations.forEach(shipLocation => {
          this.salvos
              .filter(salvo => salvo.player != this.gamePlayerId && salvo.locations.includes(shipLocation))
              .forEach(salvo => {
                if (!this.hits[ship.type]) {
                  this.hits[ship.type] = {
                    locations: [],
                    turn: 0
                  }
                }
                this.hits[ship.type].locations.push(shipLocation)
                this.hits[ship.type].turn = Math.max(this.hits[ship.type].turn, salvo.turn)
              })
        });
      });
    }
  }
});
