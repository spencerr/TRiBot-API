package scripts.api.walking;

import java.util.HashMap;

public class CollisionCache {
    public Collision collision;
    public int cflags = 0;
    public HashMap<Direction, Collision.Colors> data = new HashMap<>();

    public CollisionCache(Collision collision, HashMap<Direction, Collision.Colors> data) {
        this.collision = collision;
        this.data = data;
    }
}
