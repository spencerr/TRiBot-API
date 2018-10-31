package scripts.api.walking;

import java.awt.*;

public class Collision extends BitMask
{
    public static final Color OPEN_COLOR = Color.GREEN;
    public static final Color CLOSED_COLOR = Color.BLACK;
    public static final Color UNINITIALIZED_COLOR = Color.WHITE;
    public static final Color OCCUPIED_COLOR = Color.MAGENTA;
    public static final Color SOLID_COLOR = Color.MAGENTA;
    public static final Color BLOCKED_COLOR = Color.RED;
    public static final Color DOOR_CLOSED_COLOR = Color.CYAN;
    public static final Color DOOR_OPEN_COLOR = Color.ORANGE;
    public static final Color INTERACTIVE_COLOR = Color.YELLOW;
    public static final Color WALL_COLOR = Color.BLUE;

    public enum CFlags {
        OPEN(0),
        CLOSED(1),
        DOOR_NORTH(2),
        DOOR_SOUTH(4),
        DOOR_EAST(8),
        DOOR_WEST(16),
        WALL_NORTH(32),
        WALL_SOUTH(64),
        WALL_EAST(128),
        WALL_WEST(256);

        public int flag;
        CFlags(int flag) {
            this.flag = flag;
        }
    }

    public enum Colors {
        OPEN_COLOR(Color.GREEN),
        CLOSED_COLOR(Color.BLACK),
        UNINITIALIZED_COLOR(Color.WHITE),
        OCCUPIED_COLOR(Color.MAGENTA),
        SOLID_COLOR(Color.MAGENTA),
        BLOCKED_COLOR(Color.RED),
        DOOR_CLOSED_COLOR(Color.CYAN),
        DOOR_OPEN_COLOR(Color.ORANGE),
        INTERACTIVE_COLOR(Color.YELLOW),
        WALL_COLOR(Color.BLUE);

        public Color color;
        Colors(Color color) {
            this.color = color;
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

    public static final int OPEN = 0;
    public static final int CLOSED = 0xFFFFFF;
    public static final int UNINITIALIZED = 0x1000000;
    public static final int OCCUPIED = 0x100;
    public static final int SOLID = 0x20000;
    public static final int BLOCKED = 0x200000;

    public static final int NORTH = 0x2;
    public static final int EAST = 0x8;
    public static final int SOUTH = 0x20;
    public static final int WEST = 0x80;

    public static final int NORTHEAST = 0x4;
    public static final int SOUTHEAST = 0x10;
    public static final int SOUTHWEST = 0x40;
    public static final int NORTHWEST = 0x1;

    public static final int EAST_NORTH = EAST | NORTH;
    public static final int EAST_SOUTH = EAST | SOUTH;
    public static final int WEST_SOUTH = WEST | SOUTH;
    public static final int WEST_NORTH = WEST | NORTH;

    public static final int BLOCKED_NORTH = 0x400;
    public static final int BLOCKED_EAST = 0x1000;
    public static final int BLOCKED_SOUTH = 0x4000;
    public static final int BLOCKED_WEST = 0x10000;

    public static final int BLOCKED_NORTHEAST = 0x800;
    public static final int BLOCKED_SOUTHEAST = 0x2000;
    public static final int BLOCKED_NORTHWEST = 0x200;
    public static final int BLOCKED_SOUTHWEST = 0x8000;

    public static final int BLOCKED_EAST_NORTH = BLOCKED_EAST | BLOCKED_NORTH;
    public static final int BLOCKED_EAST_SOUTH = BLOCKED_EAST | BLOCKED_SOUTH;
    public static final int BLOCKED_WEST_SOUTH = BLOCKED_WEST | BLOCKED_SOUTH;
    public static final int BLOCKED_WEST_NORTH = BLOCKED_WEST | BLOCKED_NORTH;

    public Collision()
    {
        super(OPEN);
    }

    public static boolean canMove4Way(Collision start, Collision dest, Direction destApproach)
    {
        if (isBlocked(dest))
        {
            return false;
        }

        switch (destApproach)
        {
            case NORTH:
                return !(dest.has(SOUTH) || start.has(NORTH));
            case EAST:
                return !(dest.has(WEST) || start.has(EAST));
            case SOUTH:
                return !(dest.has(NORTH) || start.has(SOUTH));
            case WEST:
                return !(dest.has(EAST) || start.has(WEST));
            default:
                return false;
        }
    }

    public static boolean isRangedEnabled(int flag) {
        Collision collision = new Collision();
        collision.set(flag);
        return collision.has(OCCUPIED);
    }

    public static boolean isFakeBlocked(int flag, Direction direction) {
        Collision collision = new Collision();
        collision.set(flag);
        switch (direction) {
            case EAST:
                return collision.has(EAST);
            case WEST:
                return collision.has(WEST);
            case SOUTH:
                return collision.has(SOUTH);
            case NORTH:
                return collision.has(NORTH);
            case NORTH_EAST:
                return collision.has(EAST_NORTH) || collision.has(BLOCKED_EAST_NORTH) || collision.has(NORTHEAST);
            case NORTH_WEST:
                return collision.has(WEST_NORTH) || collision.has(BLOCKED_WEST_NORTH) || collision.has(NORTHWEST);
            case SOUTH_EAST:
                return collision.has(EAST_SOUTH) || collision.has(BLOCKED_EAST_SOUTH) || collision.has(SOUTHEAST);
            case SOUTH_WEST:
                return collision.has(WEST_SOUTH) || collision.has(BLOCKED_WEST_SOUTH) || collision.has(SOUTHWEST);
        }

        return true;
    }

    public static boolean isWall(int flag, Direction direction) {
        Collision collision = new Collision();
        collision.set(flag);
        switch (direction) {
            case EAST:
                return collision.has(BLOCKED_EAST);
            case WEST:
                return collision.has(BLOCKED_WEST);
            case SOUTH:
                return collision.has(BLOCKED_SOUTH);
            case NORTH:
                return collision.has(BLOCKED_NORTH);
            case NORTH_EAST:
                return collision.has(BLOCKED_NORTHEAST) || collision.has(BLOCKED_EAST_NORTH);
            case NORTH_WEST:
                return collision.has(BLOCKED_NORTHWEST) || collision.has(BLOCKED_WEST_NORTH);
            case SOUTH_EAST:
                return collision.has(BLOCKED_SOUTHEAST) || collision.has(BLOCKED_EAST_SOUTH);
            case SOUTH_WEST:
                return collision.has(BLOCKED_SOUTHWEST) || collision.has(BLOCKED_WEST_SOUTH);
        }

        return true;
    }

    public static boolean isRangedBlocked(int flag) {
        Collision collision = new Collision();
        collision.set(flag);
        return collision.has(BLOCKED) || collision.has(SOLID) || collision.is(CLOSED);
    }

    public static boolean isSurrounded(Collision collision, Collision N, Collision S, Collision E, Collision W) {
         return (collision.has(NORTH) && collision.has(SOUTH) && collision.has(EAST) && collision.has(WEST)) || (!canMove4Way(collision, N, Direction.NORTH) && !canMove4Way(collision, S, Direction.SOUTH) && !canMove4Way(collision, E, Direction.EAST) && !canMove4Way(collision, W, Direction.WEST));
    }

    public static boolean isBlocked(Collision collision)
    {
        return (collision.is(CLOSED) || collision.has(BLOCKED) || collision.has(OCCUPIED) || collision.has(SOLID));
    }

    public static boolean isBlocked(int flag) {
        Collision c = new Collision();
        c.set(flag);
        return isBlocked(c);
    }

    public static boolean canMove8Way(Collision start, Collision dest, Collision north, Collision east, Collision south, Collision west, Direction destApproach)
    {
        if (isBlocked(dest))
        {
            return false;
        }

        switch (destApproach)
        {
            case NORTH_EAST:
				/* -- N NE
				   -- O  E
				   -- - -- */
                return !(dest.has(SOUTHWEST) || dest.has(SOUTH) || dest.has(WEST)
                        || start.has(NORTHEAST) || start.has(EAST) || start.has(NORTH)
                        || east.has(WEST) || east.has(NORTH) || isBlocked(east)
                        || north.has(EAST) || north.has(SOUTH) || isBlocked(north));
            case NORTH_WEST:
				/* NW N --
				   W  O --
				   -- - -- */
                return !(dest.has(SOUTHEAST) || dest.has(SOUTH) || dest.has(EAST)
                        || start.has(NORTHWEST) || start.has(WEST) || start.has(NORTH)
                        || west.has(EAST) || west.has(NORTH) || isBlocked(west)
                        || north.has(WEST) || north.has(SOUTH) || isBlocked(north));
            case SOUTH_EAST:
				/* -- - --
				   -- O  E
				   -- S SE */
                // origin to southeast
                // origin to east
                // origin to south
                // east to south
                // south to east
                return !(dest.has(NORTHWEST) || dest.has(NORTH) || dest.has(WEST)
                        || start.has(SOUTHEAST) || start.has(EAST) || start.has(SOUTH)
                        || east.has(WEST) || east.has(SOUTH) || isBlocked(east)
                        || south.has(EAST) || south.has(NORTH) || isBlocked(south));
            case SOUTH_WEST:
				/* -- - --
				   W  O --
				   SW S -- */
                return !(dest.has(NORTHEAST) || dest.has(NORTH) || dest.has(EAST)
                        || start.has(SOUTHWEST) || start.has(WEST) || start.has(SOUTH)
                        || west.has(EAST) || west.has(SOUTH) || isBlocked(west)
                        || south.has(WEST) || south.has(NORTH) || isBlocked(south));
            default:
                return canMove4Way(start, dest, destApproach);
        }
    }
}
