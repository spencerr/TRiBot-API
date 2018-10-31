package scripts.api.walking;

import org.tribot.api2007.types.RSTile;

import java.io.Serializable;

public class BasicRSTile implements Serializable {
    public int x, y, plane;

    public BasicRSTile(int x, int y, int plane) {
        this.x = x;
        this.y = y;
        this.plane = plane;
    }

    public RSTile getTile() {
        return new RSTile(x, y, plane);
    }

    public int distanceTo(BasicRSTile tile) {
        return (int) Math.sqrt(Math.pow(tile.x - x, 2) + Math.pow(tile.y - y, 2));
    }

    public double distanceToDouble(BasicRSTile tile) {
        return Math.sqrt(Math.pow(tile.x - x, 2) + Math.pow(tile.y - y, 2));
    }

    @Override
    public boolean equals(Object obj) {
        BasicRSTile tile = (BasicRSTile) obj;
        return x == tile.x && y == tile.y && plane == tile.plane;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }


    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + plane;
    }
}
