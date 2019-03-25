package com.codeoftheweb.salvo;

public enum ShipType {
    CARRIER,
    BATTLESHIP,
    SUBMARINE,
    DESTROYER,
    PATROL_BOAT,
    UNKNOWN;

    public static ShipType value(String type) {
        switch (type) {
            case "Carrier":
                return ShipType.CARRIER;
            case "Battleship":
                return ShipType.BATTLESHIP;
            case "Submarine":
                return ShipType.SUBMARINE;
            case "Destroyer":
                return ShipType.DESTROYER;
            case "Patrol Boat":
                return ShipType.PATROL_BOAT;
        }

        return ShipType.UNKNOWN;
    }

    public static int getLength(ShipType shipType) {
        switch (shipType) {
            case CARRIER:
                return 5;
            case BATTLESHIP:
                return 4;
            case SUBMARINE:
                return 3;
            case DESTROYER:
                return 3;
            case PATROL_BOAT:
                return 2;
        }

        return 0;
    }
}
