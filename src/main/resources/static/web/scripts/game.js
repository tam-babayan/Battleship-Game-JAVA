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
        this.getCurrentPlayerId();

        setInterval(() => {
            this.getGameInfo()
        }, 3000);
    },

    computed: {
        board() {
            let array = [];
            for (let i = 0; i < this.rows.length; i++) {
                let rowObject = {rowId: this.rows[i], column: []};
                for (let j = 1; j < this.columns.length; j++) {
                    let columnObject = {
                        columnId: this.columns[j],
                        ship: this.isShipCells(this.rows[i] + this.columns[j]),
                        hoveredCell: this.currentShipPositions.includes(this.rows[i] + this.columns[j]),
                        overlapCell: this.overlapShipPositions.includes(this.rows[i] + this.columns[j]),
                        salvoInProcessCell: this.salvoInProcess.includes(this.rows[i] + this.columns[j]),
                        mySalvos: this.isMySalvoCell(this.rows[i] + this.columns[j]),
                        opponentSalvos: this.isOpponentSalvos(this.rows[i] + this.columns[j]),
                        opponentShip: this.isOpponentShips(this.rows[i] + this.columns[j]),
                        opponentSunkShips: this.getOpponentSunkShips(this.rows[i] + this.columns[j]),
                        mySalvoTurn: this.getMySalvoTurn(this.rows[i] + this.columns[j]),
                        opponentSalvoTurn: this.getOpponentSalvoTurn(this.rows[i] + this.columns[j])
                    };
                    rowObject.column.push(columnObject);
                }
                array.push(rowObject);
            }

            return array;
        },

        hitShips() {
            let hitShips = [];
            for (let i = 0; i < this.ships.length; i++) {
                let ship = this.ships[i];
                let left = ship.locations.length;
                if (this.hits[ship.type]) {
                    left = left - this.hits[ship.type].locations.length
                }
                if (left === 0) {
                    hitShips.push({ship: ship.type, left: left, sunk: "SUNK", turn: this.hits[ship.type].turn});
                } else {
                    hitShips.push({ship: ship.type, left: left});
                }
            }
            hitShips.sort((a, b) => (a.ship > b.ship) ? 1 : ((b.ship > a.ship) ? -1 : 0));

            return hitShips
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
                    if (response.data.player.id != null) {
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
                    this.salvos = this.gameInfo.salvos;
                    this.opponentShips = this.gameInfo.opponentShips;
                    this.updateShipsToPlace();
                    this.updateSalvoTurn();
                    this.getHitInfo();
                    // console.log(this.opponentShips);
                })
                .catch(error => {
                    this.errorMessage = error.response.data.error;
                })
        },

        isShipCells(coordinate) {
            for (let i = 0; i < this.ships.length; i++) {
                if (this.ships[i].locations.includes(coordinate)) {
                    return true;
                }
            }
            return false;
        },

        isMySalvoCell(coordinate) {
            for (let i = 0; i < this.salvos.length; i++) {
                if (
                    this.salvos[i].player == this.gamePlayerId &&
                    this.salvos[i].locations.includes(coordinate)
                ) {
                    return true;
                }
            }
            return false;
        },

        isOpponentSalvos(coordinate) {
            for (let i = 0; i < this.salvos.length; i++) {
                if (
                    this.salvos[i].player != this.gamePlayerId &&
                    this.salvos[i].locations.includes(coordinate) && this.isShipCells(coordinate)
                ) {
                    return true;
                }
            }
            return false;
        },

        getMySalvoTurn(coordinate) {
            for (let i = 0; i < this.salvos.length; i++) {
                if (
                    this.salvos[i].player == this.gamePlayerId &&
                    this.salvos[i].locations.includes(coordinate)
                ) {
                    return this.salvos[i].turn;
                }
            }
            return null;
        },

        getOpponentSalvoTurn(coordinate) {
            for (let i = 0; i < this.salvos.length; i++) {
                if (
                    this.salvos[i].player != this.gamePlayerId &&
                    this.salvos[i].locations.includes(coordinate) &&
                    this.isShipCells(coordinate)
                ) {
                    return this.salvos[i].turn;
                }
            }
            return null;
        },

        isOpponentShips(coordinate) {
            for (let i = 0; i < this.opponentShips.length; i++) {
                if (this.opponentShips[i].locations.includes(coordinate)) {
                    return true;
                }
            }
            return false;
        },

        getOpponentSunkShips(coordinate) {
            for (let i = 0; i < this.opponentShips.length; i++) {
                if (this.opponentShips[i].type != null && this.opponentShips[i].locations.includes(coordinate)) {
                    return true;
                }
            }
            return false;
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
                    if (type == ship.name) {
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
            arr.push(this.rows[this.rows.indexOf(letter) - 1] + parseInt(number));
            arr.push(this.rows[this.rows.indexOf(letter) + 1] + parseInt(number));
            arr.push(letter + parseInt(number + 1));
            arr.push(letter + parseInt(number - 1));
            for (let i = 0; i < arr.length; i++) {
                if (this.isShipCells(arr[i])) {
                    return true
                }
            }
            return false
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
            for (let i = 0; i < this.salvos.length; i++) {
                if (this.salvos[i].player == this.gamePlayerId && this.salvos[i].locations.includes(coordinate)) {
                    return true
                }
            }
            return false
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
                    this.salvoInProcess = []
                    this.salvoCounter = 5
                })
                .catch(error => console.log(error))
        },


        getHitInfo() {
            this.hits = []
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
