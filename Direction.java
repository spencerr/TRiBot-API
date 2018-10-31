package scripts.api.walking;

public enum Direction {
    NORTH, SOUTH, EAST, WEST, NORTH_EAST, NORTH_WEST, SOUTH_EAST, SOUTH_WEST, ME, DIAGNAL_NORTH_WEST_SOUTH_EAST, DIAGNAL_SOUTH_WEST_NORTH_EAST;

    public boolean isCorner() {
        return this == NORTH_EAST || this == SOUTH_EAST || this == NORTH_WEST || this == SOUTH_WEST;
    }

    public Direction opposite() {
        switch (this) {
            case NORTH:
                return SOUTH;
            case SOUTH:
                return NORTH;
            case EAST:
                return WEST;
            case WEST:
                return EAST;
            case NORTH_EAST:
                return SOUTH_WEST;
            case NORTH_WEST:
                return SOUTH_EAST;
            case SOUTH_EAST:
                return NORTH_WEST;
            case SOUTH_WEST:
                return NORTH_EAST;
        }

        return this;
    }

    public Direction oppositeEastWest() {
        return this == WEST || this == SOUTH_WEST || this == NORTH_WEST ? EAST : WEST;
    }

    public Direction oppositeNorthSouth() {
        return this == NORTH || this == NORTH_WEST || this == NORTH_EAST ? SOUTH : NORTH;
    }

    public boolean isEast() {
        return this == EAST || this == NORTH_EAST || this == SOUTH_EAST;
    }

    public boolean isWest() {
        return this == WEST || this == NORTH_WEST || this == SOUTH_WEST;
    }

    public boolean isNorth() {
        return this == NORTH || this == NORTH_EAST || this == NORTH_WEST;
    }

    public boolean isSouth() {
        return this == SOUTH|| this == SOUTH_EAST || this == SOUTH_WEST;
    }
}
