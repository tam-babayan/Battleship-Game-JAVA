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
        salvoTurn: 0,
        salvoCounter: 5
    },

    mounted() {
        this.initParams()
        this.getCurrentPlayerId();
    },

    computed: {
        board() {
            let array = [];
            for (let i = 0; i < this.rows.length; i++) {
                let rowObject = {rowId: this.rows[i], column: []};
                for (let j = 1; j < this.columns.length; j++) {
                    let columnObject = {
                        columnId: this.columns[j],
                        ship: this.getShipCells(this.rows[i] + this.columns[j]),
                        hoveredCell: this.currentShipPositions.includes(this.rows[i] + this.columns[j]),
                        overlapCell: this.overlapShipPositions.includes(this.rows[i] + this.columns[j]),
                        salvoInprocessCell: this.salvoInProcess.includes(this.rows[i] + this.columns[j]),
                        mySalvos: this.getMySalvoCell(this.rows[i] + this.columns[j]),
                        oponentSalvos: this.getOponentSalvos(
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
                    this.updateShipsToPlace()
                    this.updateSalvoTurn()
                    console.log(this.salvos)
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

        getOponentSalvos(coordinate) {
            for (let i = 0; i < this.salvos.length; i++) {
                if (
                    this.salvos[i].player != this.gamePlayerId &&
                    this.salvos[i].locations.includes(coordinate) && this.getShipCells(coordinate)
                ) {
                    return true;
                }
            }
            return false;
        },

        getTurn(coordinate) {
            for (let i = 0; i < this.salvos.length; i++) {
                if (this.salvos[i].locations.includes(coordinate)) {
                    return this.salvos[i].turn;
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


        // posts ship data on click
        addShips() {
            if (this.currentShipPositions.length) {
                fetch('/games/players/' + this.gamePlayerId + '/ships', {
                    credentials: "include",
                    method: "POST",
                    headers: {
                        Accept: "application/json",
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify([{
                        shipType: this.shipInProcess.name,
                        shipLocations: this.currentShipPositions
                    }])
                })
                    .then(response => this.getGameInfo())
                    .then(this.changePlacedStatus(this.shipInProcess.name))
                    .catch(error => console.log(error))
            }
        },


        // changes the isPlaced status of placed ships
        changePlacedStatus(type) {
            this.shipInProcess.name = null
            this.shipInProcess.length = 0
            this.shipTypes.map(ship => {
                    if (type == ship.name) {
                        ship.isPlaced = true
                    }
                }
            )
        },

        //gets the possible ship coordinates array on mouseover event
        updatePossibleShipPosition(a, b) {
            let shipLength = this.shipInProcess.length
            let isVertical = this.isVertical
            this.currentShipPositions = []
            let isOverlap = false
            for (let i = 0; i < shipLength; i++) {
                let letter = !isVertical ? a : this.rows[this.rows.indexOf(a) + i]
                let number = !isVertical ? parseInt(b + i) : b
                if (!isVertical && number > 10) {
                    number = 10 - i
                }
                if (isVertical && !letter) {
                    letter = this.rows[9 - i]
                }
                let coordinate = letter + number

                if (this.getShipCells(coordinate) || this.isBorderedShipPlaced(letter, number)) {
                    isOverlap = true
                }

                this.currentShipPositions[i] = coordinate
            }

            if (isOverlap) {
                this.overlapShipPositions = this.currentShipPositions
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
            let cellAbove = this.rows[this.rows.indexOf(letter) - 1] + parseInt(number)
            let cellBottom = this.rows[this.rows.indexOf(letter) + 1] + parseInt(number)
            let CellRight = letter + parseInt(number + 1)
            let CellLeft = letter + parseInt(number - 1)
            let arr = []
            arr.push(cellAbove)
            arr.push(cellBottom)
            arr.push(CellRight)
            arr.push(CellLeft)
            for (let i = 0; i < arr.length; i++) {
                if (this.getShipCells(arr[i])) {
                    return true
                }
            }

            return false
        },

        // gets the ship info on clicking on the ships
        setShipInProcess(shipName, shipLength, status) {
            if (!status) {
                this.shipInProcess.name = shipName
                this.shipInProcess.length = shipLength
                console.log(this.shipInProcess)
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
            let coordinate = letter + number
            if (!this.salvoInProcess.includes(coordinate) && this.salvoInProcess.length <= 4 && !this.isSalvoPlaced(coordinate)) {
                this.salvoInProcess.push(coordinate)
                this.salvoCounter--
            } else if (this.salvoInProcess.includes(coordinate)) {
                this.salvoInProcess.splice(this.salvoInProcess.indexOf(coordinate), 1);
                this.salvoCounter++
            }
            console.log(this.salvoInProcess)
            console.log(this.salvoCounter)
        },


        isSalvoPlaced(coordinate) {
            for (let i = 0; i < this.salvos.length; i++) {
                if (this.salvos[i].locations.includes(coordinate)) {
                    return true
                }
            }
            return false
        },

        updateSalvoTurn() {
            if (this.salvos.length < 1) {
                return;
            } else {
                this.salvoTurn = Math.max.apply(Math, this.salvos.map(o => {
                    return o.turn
                }))
            }
        },


        // posts salvo data on command Fire
        addSalvo() {
            this.salvoTurn = this.salvoTurn + 1
            fetch('/games/players/' + this.gamePlayerId + '/salvos', {
                credentials: "include",
                method: "POST",
                headers: {
                    Accept: "application/json",
                    "Content-Type": "application/json"
                },
                body: JSON.stringify([{salvoLocations: this.salvoInProcess, turn: this.salvoTurn}])
            })
                .then(response => this.getGameInfo())
                .then(() => {
                    this.salvoInProcess = []
                })
                .then(this.salvoCounter = 5)
                .catch(error => console.log(error))
        }
    }
})
