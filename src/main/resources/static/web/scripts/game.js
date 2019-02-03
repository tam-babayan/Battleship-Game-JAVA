new Vue ({
    el: '#app',
    data: {
        gameInfo: [],
        ships: [],
        rows: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
        columns: ['',1,2,3,4,5,6,7,8,9,10],
        playerId: 1
    },
    mounted() {
        this.getgameInfoInfo()
    },
    computed: {
        board () {
            let array = []
            for (let i = 0; i < this.rows.length; i++) {
                let object = {  rowId : this.rows[i],
                                column : []
                              }
                for (let j = 1; j < this.columns.length; j++) {
                    let anotherObject = {   columnId: this.columns[j],
                                            active: this.getActiveCells(this.rows[i] + this.columns[j])
                                        }
                    object.column.push(anotherObject)
                }
                array.push(object)
            }
            return array;
        }
    },
    methods: {
        getgameInfoInfo () {
            axios
                .get('http://localhost:8080/api/game_view/' + this.playerId)
                .then(response => {
                    this.gameInfo = response.data
                    console.log(this.gameInfo)
                    this.ships = this.gameInfo.ships
                 })
                .catch(error => console.log(error))
        },
        getActiveCells(coordinate) {
            for (let i = 0; i < this.ships.length; i++) {
                if(this.ships[i].locations.includes(coordinate)) {
                    return true
                }
            }
            return false
        }
    }
})