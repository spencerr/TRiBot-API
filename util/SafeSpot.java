package scripts.api.util;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSTile;
import scripts.api.webwalker.shared.Pair;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Created by Spencer on 10/5/2017.
 */
public class SafeSpot {

    public static Pair<RSNPC, RSTile> getBest(HashMap<RSNPC, ArrayList<RSTile>> sets) {
        RSTile playerPosition = Player.getPosition();

        sets.keySet().forEach(rsnpc -> sets.get(rsnpc).sort((o1, o2) -> o1.distanceTo(playerPosition) - o2.distanceTo(playerPosition)));
        ArrayList<RSNPC> reachable = sets.keySet().stream().filter(rsnpc -> sets.get(rsnpc).stream().anyMatch(rsTile -> rsTile.equals(playerPosition))).sorted((o1, o2) -> o1.getPosition().distanceTo(playerPosition) - o2.getPosition().distanceTo(playerPosition)).collect(Collectors.toCollection(ArrayList::new));

        if (reachable.size() > 0) return new Pair<>(reachable.get(0), playerPosition);

        RSTile resultTile = null;
        RSNPC resultNPC = null;
        for (RSNPC npc : sets.keySet()) {

            if (sets.get(npc).size() == 0) continue;

            RSTile firstTile = sets.get(npc).get(0);

            if (resultTile == null) {
                resultTile = firstTile;
                resultNPC = npc;
                continue;
            }

            if (playerPosition.distanceTo(firstTile) < playerPosition.distanceTo(resultTile)) {
                resultTile = firstTile;
                resultNPC = npc;
            }
        }

        return new Pair<>(resultNPC, resultTile);

    }

    public static HashMap<RSNPC, ArrayList<RSTile>> generateSafeSpots(RSNPC... npcs) {
        return generateSafeSpots(8, npcs);
    }

    public static HashMap<RSNPC, ArrayList<RSTile>> generateSafeSpots(int range, RSNPC... npcs) {
        HashMap<RSNPC, ArrayList<RSTile>> safeSpots = new HashMap<>();
        int[][] flags = PathFinding.getCollisionData();
        for (RSNPC npc : npcs) {
            safeSpots.put(npc, getSafeSpots(range, flags, npc));
        }

        return safeSpots;
    }

    private static ArrayList<RSTile> getSafeSpots(int range, int[][] flags, RSNPC npc) {
        RSTile pos = npc.getPosition();
        ArrayList<RSTile> all = new ArrayList<>();
        for (int x = 0; x < range; x++) {
            for (int y = 0; y < range; y++) {
                all.add(new RSTile(pos.getX() + x, pos.getY() + y));
                all.add(new RSTile(pos.getX() - x, pos.getY() - y));
                all.add(new RSTile(pos.getX() + x, pos.getY() - y));
                all.add(new RSTile(pos.getX() - x, pos.getY() + y));
            }
        }

        ArrayList<RSTile> result = new ArrayList<>();
        all.stream().filter(rsTile -> rsTile.distanceTo(pos) <= range)
            .forEach(rsTile -> {
                if (checkSafe(flags, npc, rsTile, all)) {
                    result.add(rsTile);
                }
            });

        return result;

    }


    private static boolean checkSafe(int[][] flags, Positionable positionable, RSTile tile, ArrayList<RSTile> all) {
        RSTile pos = positionable.getPosition();

        Line2D line = new Line2D.Double(pos.getX() * 64 + 32, pos.getY() * 64 + 32, tile.getX() * 64 + 32, tile.getY() * 64 + 32);

        all = all.stream()
                .filter(rsTile -> intersects(line, rsTile) && !rsTile.equals(pos))
                .sorted((o1, o2) -> o1.distanceTo(pos) - o2.distanceTo(pos))
                .collect(Collectors.toCollection(ArrayList::new));

        boolean foundObstacle = false;
        int result = -1;
        for (RSTile t : all) {
            result = isValid(flags, line, t);
            if (result == -1) return false;
            if (result == 1 || result == 2) foundObstacle = true;
        }

        return foundObstacle && (result == 0 || result == 1);
    }

    private static boolean intersects(Line2D line, RSTile tile) {
        Rectangle2D rect = new Rectangle2D.Double(tile.getX() * 64, tile.getY() * 64, 64, 64);
        return line.intersects(rect);
    }

    private static int isValid(int[][] flags, Line2D line, RSTile tile) {
        Rectangle2D rect = new Rectangle2D.Double(tile.getX() * 64, tile.getY() * 64, 64, 64);

        int lX = tile.toLocalTile().getX();
        int lY = tile.toLocalTile().getY();
        int flag = flags[lX][lY];

        if (isRangedBlocked(flag)) return -1;
        if (isRangedEnabled(flag)) return 2;

        Line2D north = new Line2D.Double(rect.getMinX(), rect.getMaxY(), rect.getMaxX(), rect.getMaxY());
        Line2D south = new Line2D.Double(rect.getMinX(), rect.getMinY(), rect.getMaxX(), rect.getMinY());
        Line2D east = new Line2D.Double(rect.getMaxX(), rect.getMinY(), rect.getMaxX(), rect.getMaxY());
        Line2D west = new Line2D.Double(rect.getMinX(), rect.getMinY(), rect.getMinX(), rect.getMaxY());


        if (north.intersectsLine(line)) {
            if (isWall(flag, Direction.NORTH)) return -1;
            if (isFakeBlocked(flag, Direction.NORTH)) return 1;
        }

        if (south.intersectsLine(line)) {
            if (isWall(flag, Direction.SOUTH)) return -1;
            if (isFakeBlocked(flag, Direction.SOUTH)) return 1;
        }

        if (east.intersectsLine(line)) {
            if (isWall(flag, Direction.EAST)) return -1;
            if (isFakeBlocked(flag, Direction.EAST)) return 1;
        }

        if (west.intersectsLine(line)) {
            if (isWall(flag, Direction.WEST)) return -1;
            if (isFakeBlocked(flag, Direction.WEST)) return 1;
        }

        return 0;
    }

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

    public enum Flags {
        OPEN(0), CLOSED(0xFFFFFF), UNINITIALIZED(0x1000000), OCCUPIED(0x100), SOLID(0x20000), BLOCKED(0x200000),

        NORTH(0x2), EAST(0x8), SOUTH(0x20), WEST(0x80),

        NORTHEAST(0x4),
        SOUTHEAST(0x10),
        SOUTHWEST(0x40),
        NORTHWEST(0x1),

        EAST_NORTH(EAST.getValue() | NORTH.getValue()),
        EAST_SOUTH(EAST.getValue() | SOUTH.getValue()),
        WEST_SOUTH(WEST.getValue() | SOUTH.getValue()),
        WEST_NORTH(WEST.getValue() | NORTH.getValue()),

        BLOCKED_NORTH(0x400),
        BLOCKED_EAST(0x1000),
        BLOCKED_SOUTH(0x4000),
        BLOCKED_WEST(0x10000),

        BLOCKED_NORTHEAST(0x800),
        BLOCKED_SOUTHEAST(0x2000),
        BLOCKED_NORTHWEST(0x200),
        BLOCKED_SOUTHWEST(0x8000),

        BLOCKED_EAST_NORTH(BLOCKED_EAST.getValue() | BLOCKED_NORTH.getValue()),
        BLOCKED_EAST_SOUTH(BLOCKED_EAST.getValue() | BLOCKED_SOUTH.getValue()),
        BLOCKED_WEST_SOUTH(BLOCKED_WEST.getValue() | BLOCKED_SOUTH.getValue()),
        BLOCKED_WEST_NORTH(BLOCKED_WEST.getValue() | BLOCKED_NORTH.getValue());

        private int value;
        Flags(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public boolean has(int flag) {
            return (flag & value) != 0;
        }

        public boolean is(int flag) {
            return flag == value;
        }
    }

    public static boolean isRangedEnabled(int flag) {
        return Flags.OCCUPIED.has(flag);
    }

    public static boolean isFakeBlocked(int flag, Direction direction) {
        switch (direction) {
            case EAST:
                return Flags.EAST.has(flag);
            case WEST:
                return Flags.WEST.has(flag);
            case SOUTH:
                return Flags.SOUTH.has(flag);
            case NORTH:
                return Flags.NORTH.has(flag);
            case NORTH_EAST:
                return Flags.EAST_NORTH.has(flag) || Flags.BLOCKED_EAST_NORTH.has(flag) || Flags.NORTHEAST.has(flag);
            case NORTH_WEST:
                return Flags.WEST_NORTH.has(flag) || Flags.BLOCKED_WEST_NORTH.has(flag) || Flags.NORTHWEST.has(flag);
            case SOUTH_EAST:
                return Flags.EAST_SOUTH.has(flag) || Flags.BLOCKED_EAST_SOUTH.has(flag) || Flags.SOUTHEAST.has(flag);
            case SOUTH_WEST:
                return Flags.WEST_SOUTH.has(flag) || Flags.BLOCKED_WEST_SOUTH.has(flag) || Flags.SOUTHWEST.has(flag);
        }

        return true;
    }

    public static boolean isWall(int flag, Direction direction) {
        switch (direction) {
            case EAST:
                return Flags.BLOCKED_EAST.has(flag);
            case WEST:
                return Flags.BLOCKED_WEST.has(flag);
            case SOUTH:
                return Flags.BLOCKED_SOUTH.has(flag);
            case NORTH:
                return Flags.BLOCKED_NORTH.has(flag);
            case NORTH_EAST:
                return Flags.BLOCKED_NORTHEAST.has(flag) || Flags.BLOCKED_EAST_NORTH.has(flag);
            case NORTH_WEST:
                return Flags.BLOCKED_NORTHWEST.has(flag) || Flags.BLOCKED_WEST_NORTH.has(flag);
            case SOUTH_EAST:
                return Flags.BLOCKED_SOUTHEAST.has(flag) || Flags.BLOCKED_EAST_SOUTH.has(flag);
            case SOUTH_WEST:
                return Flags.BLOCKED_SOUTHWEST.has(flag) || Flags.BLOCKED_WEST_SOUTH.has(flag);
        }

        return true;
    }

    public static boolean isRangedBlocked(int flag) {
        return Flags.BLOCKED.has(flag) || Flags.SOLID.has(flag) || Flags.CLOSED.is(flag);
    }
}

