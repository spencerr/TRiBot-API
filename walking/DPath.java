package scripts.api.walking;

import scripts.api.util.Utilities;
import org.tribot.api.General;
import org.tribot.api2007.Game;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Spencer on 10/17/2016.
 */
public class DPath {
    public RSTile base = new RSTile(0, 0);
    public Collision[][] collisionMap = new Collision[0][0];
    public CollisionCache[][] collisionCaches = new CollisionCache[104][104];

    public List<RSObject> objectCache = new ArrayList<>();
    public List<RSObject> doorCache = new ArrayList<>();
    public RSObject[] boundaryCache = new RSObject[0];
    public RSObject[] interactiveCache = new RSObject[0];

    public DirectedGraph<BasicRSTile> directedGraph = new DirectedGraph<>();

    public DPath() {
        base = new RSTile(Game.getBaseX(), Game.getBaseY(), Game.getPlane());
    }

    public void clear() {
        base = new RSTile(Game.getBaseX(), Game.getBaseY(), Game.getPlane());
        directedGraph.mGraph.clear();
    }

    public List<BasicRSTile> findPath(RSTile destination) {
        return findPath(new BasicRSTile(destination.getX(), destination.getY(), destination.getPlane()));
    }

    public List<BasicRSTile> findPath(RSTile start, RSTile destination) {
        return findPath(new BasicRSTile(start.getX(), start.getY(), start.getPlane()), new BasicRSTile(destination.getX(), destination.getY(), destination.getPlane()));
    }

    public void apply() {
        clear();

        long timer = System.currentTimeMillis();
        updateCollisionData();
        General.println("updateCollisionData: " + (System.currentTimeMillis() - timer) + "ms");
        timer = System.currentTimeMillis();

        updateCollisionCache();
        General.println("updateCollisionCache: " + (System.currentTimeMillis() - timer) + "ms");
        timer = System.currentTimeMillis();

        updateDoorCache();
        General.println("updateDoorCache: " + (System.currentTimeMillis() - timer) + "ms");
        timer = System.currentTimeMillis();

        updateObjectCache();
        General.println("updateObjectCache: " + (System.currentTimeMillis() - timer) + "ms");
        timer = System.currentTimeMillis();

        applyDoorCache();
        General.println("applyDoorCache: " + (System.currentTimeMillis() - timer) + "ms");
        timer = System.currentTimeMillis();

        applyObjectCache();
        General.println("applyObjectCache: " + (System.currentTimeMillis() - timer) + "ms");
        timer = System.currentTimeMillis();

        addNodes();
        General.println("addNodes: " + (System.currentTimeMillis() - timer) + "ms");
        timer = System.currentTimeMillis();

        addEdges();
        General.println("addEdges: " + (System.currentTimeMillis() - timer) + "ms");
    }

    public List<BasicRSTile> findPath(BasicRSTile start, BasicRSTile destination) {
        apply();

        return FindShortest.path(directedGraph, start, destination);
    }

    public List<BasicRSTile> findPath(BasicRSTile destination) {
        return findPath(new BasicRSTile(Player.getPosition().getX(), Player.getPosition().getY(), Player.getPosition().getPlane()), destination);
    }

    public void addEdges() {
        int baseX = base.getX();
        int baseY = base.getY();
        int basePlane = base.getPlane();

        for (int x = 0; x < collisionCaches.length; x++) {
            for (int y = 0; y < collisionCaches[x].length; y++) {
                BasicRSTile loc = new BasicRSTile(baseX + x, baseY + y, basePlane);

                if (Collision.isBlocked(collisionCaches[x][y].collision) || !directedGraph.mGraph.containsKey(loc)) continue;

                try {
                    if (Collision.canMove4Way(collisionCaches[x][y].collision, collisionCaches[x][y + 1].collision, Direction.NORTH)) {
                        BasicRSTile newLoc = new BasicRSTile(baseX + x, baseY + y + 1, basePlane);
                        directedGraph.addEdge(loc, newLoc, loc.distanceToDouble(newLoc));

                        if (Collision.canMove8Way(collisionCaches[x][y].collision, collisionCaches[x - 1][y + 1].collision,
                                collisionCaches[x][y + 1].collision, collisionCaches[x + 1][y].collision,
                                collisionCaches[x][y - 1].collision, collisionCaches[x - 1][y].collision, Direction.NORTH_WEST)) {
                            newLoc = new BasicRSTile(baseX + x - 1, baseY + y + 1, basePlane);
                            directedGraph.addEdge(loc, newLoc, loc.distanceToDouble(newLoc));
                        }

                        if (Collision.canMove8Way(collisionCaches[x][y].collision, collisionCaches[x + 1][y + 1].collision,
                                collisionCaches[x][y + 1].collision, collisionCaches[x + 1][y].collision,
                                collisionCaches[x][y - 1].collision, collisionCaches[x - 1][y].collision, Direction.NORTH_EAST)) {
                            newLoc = new BasicRSTile(baseX + x + 1, baseY + y + 1, basePlane);
                            directedGraph.addEdge(loc, newLoc, loc.distanceToDouble(newLoc));
                        }
                    }

                    if (Collision.canMove4Way(collisionCaches[x][y].collision, collisionCaches[x][y - 1].collision, Direction.SOUTH)) {
                        BasicRSTile newLoc = new BasicRSTile(baseX + x, baseY + y - 1, basePlane);
                        directedGraph.addEdge(loc, newLoc, loc.distanceToDouble(newLoc));

                        if (Collision.canMove8Way(collisionCaches[x][y].collision, collisionCaches[x - 1][y - 1].collision,
                                collisionCaches[x][y + 1].collision, collisionCaches[x + 1][y].collision,
                                collisionCaches[x][y - 1].collision, collisionCaches[x - 1][y].collision, Direction.SOUTH_WEST)) {
                            newLoc = new BasicRSTile(baseX + x - 1, baseY + y - 1, basePlane);
                            directedGraph.addEdge(loc, newLoc, loc.distanceToDouble(newLoc));
                        }

                        if (Collision.canMove8Way(collisionCaches[x][y].collision, collisionCaches[x + 1][y - 1].collision,
                                collisionCaches[x][y + 1].collision, collisionCaches[x + 1][y].collision,
                                collisionCaches[x][y - 1].collision, collisionCaches[x - 1][y].collision, Direction.SOUTH_EAST)) {
                            newLoc = new BasicRSTile(baseX + x + 1, baseY + y - 1, basePlane);
                            directedGraph.addEdge(loc, newLoc, loc.distanceToDouble(newLoc));
                        }
                    }

                    if (Collision.canMove4Way(collisionCaches[x][y].collision, collisionCaches[x + 1][y].collision, Direction.EAST)) {
                        BasicRSTile newLoc = new BasicRSTile(baseX + x + 1, baseY + y, basePlane);
                        directedGraph.addEdge(loc, newLoc, loc.distanceToDouble(newLoc));
                    }

                    if (Collision.canMove4Way(collisionCaches[x][y].collision, collisionCaches[x - 1][y].collision, Direction.WEST)) {
                        BasicRSTile newLoc = new BasicRSTile(baseX + x - 1, baseY + y, basePlane);
                        directedGraph.addEdge(loc, newLoc, loc.distanceToDouble(newLoc));
                    }
                } catch (Exception e) { }
            }
        }
    }

    public void addNodes() {
        for (int x = 0; x < collisionCaches.length; x++) {
            for (int y = 0; y < collisionCaches[x].length; y++) {
                if (!collisionCaches[x][y].data.containsKey(Direction.ME) || collisionCaches[x][y].data.get(Direction.ME).color != Collision.BLOCKED_COLOR) {
                    directedGraph.addNode(new BasicRSTile(base.getX() + x, base.getY() + y, base.getPlane()));
                }
            }
        }
    }

    public void applyObjectCache() {
        for (RSObject obj : objectCache) {
            RSTile[] allTiles = obj.getAllTiles();
            if (allTiles.length > 1) {
                List<RSObject> boundaries = objectCache.stream().filter(rsObject -> rsObject.getType() == RSObject.TYPES.BOUNDARY && rsObject.getPosition().getX() == obj.getPosition().getX() && rsObject.getPosition().getY() == obj.getPosition().getY()).collect(Collectors.toList());
                if (boundaries.size() > 0) {
                    RSObject boundary = boundaries.get(0);
                    for (RSTile tile : allTiles) {
                        //General.println("Applying all tile modifier.");
                        int x = tile.getX() - base.getX();
                        int y = tile.getY() - base.getY();
                        if (x >= 104 || x <= 0 || y >= 104 || y <= 0) continue;

                        try {
                            switch (boundary.getOrientation()) {
                                case 0:
                                    //collisionCaches[x][y].data.put(Direction.DIAGNAL_NORTHSOUTH, Collision.Colors.WALL_COLOR);
                                    break;
                                case 1:
                                    collisionCaches[x][y].data.put(Direction.WEST, Collision.Colors.WALL_COLOR);
                                    collisionCaches[x - 1][y].data.put(Direction.EAST, Collision.Colors.WALL_COLOR);
                                    break;
                                case 2:
                                    collisionCaches[x][y].data.put(Direction.NORTH, Collision.Colors.WALL_COLOR);
                                    collisionCaches[x][y + 1].data.put(Direction.SOUTH, Collision.Colors.WALL_COLOR);
                                    break;
                                case 4:
                                    collisionCaches[x][y].data.put(Direction.EAST, Collision.Colors.WALL_COLOR);
                                    collisionCaches[x + 1][y].data.put(Direction.WEST, Collision.Colors.WALL_COLOR);

                                    break;
                                case 8:
                                    collisionCaches[x][y].data.put(Direction.SOUTH, Collision.Colors.WALL_COLOR);
                                    collisionCaches[x][y - 1].data.put(Direction.NORTH, Collision.Colors.WALL_COLOR);
                                    break;

                            }
                        } catch (Exception e) { }
                    }
                }
            }
        }

        for (RSObject obj : boundaryCache) {
            int x = obj.getPosition().getX() - base.getX();
            int y = obj.getPosition().getY() - base.getY();
            if (x >= 104 || x <= 0 || y >= 104 || y <= 0) continue;

            if (!Collections.singletonList(doorCache).contains(obj)) {
                try {
                    switch (obj.getOrientation()) {
                        case 0:
                            //collisionCaches[x][y].data.put(Direction.DIAGNAL_NORTHSOUTH, Collision.Colors.WALL_COLOR);
                            break;
                        case 1:
                            collisionCaches[x][y].data.put(Direction.WEST, Collision.Colors.WALL_COLOR);
                            collisionCaches[x - 1][y].data.put(Direction.EAST, Collision.Colors.WALL_COLOR);
                            break;
                        case 2:
                            collisionCaches[x][y].data.put(Direction.NORTH, Collision.Colors.WALL_COLOR);
                            collisionCaches[x][y + 1].data.put(Direction.SOUTH, Collision.Colors.WALL_COLOR);
                            break;
                        case 4:
                            collisionCaches[x][y].data.put(Direction.EAST, Collision.Colors.WALL_COLOR);
                            collisionCaches[x + 1][y].data.put(Direction.WEST, Collision.Colors.WALL_COLOR);

                            break;
                        case 8:
                            collisionCaches[x][y].data.put(Direction.SOUTH, Collision.Colors.WALL_COLOR);
                            collisionCaches[x][y - 1].data.put(Direction.NORTH, Collision.Colors.WALL_COLOR);
                            break;

                    }
                } catch (Exception e) { }
            }
        }
    }

    public void applyDoorCache() {
        for (RSObject obj : doorCache) {
            int x = obj.getPosition().getX() - base.getX();
            int y = obj.getPosition().getY() - base.getY();
            if (x >= 104 || x <= 0 || y >= 104 || y <= 0) continue;

            boolean isOpen = true;
            RSObjectDefinition def = obj.getDefinition();
            if (def != null) {
                String[] actions = def.getActions();
                if (actions != null) {
                    for (String action : actions) {
                        if (action.contains("Open")) {
                            isOpen = false;
                        }
                    }
                }
            }

            try {
                switch (obj.getOrientation()) {
                    case 1:
                        collisionCaches[x][y].data.put(Direction.WEST, isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                        collisionCaches[x - 1][y].data.put(Direction.EAST, isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);

                        collisionCaches[x][y].collision.clear(Collision.WEST);
                        collisionCaches[x - 1][y].collision.clear(Collision.EAST);

                        if (!Collision.isBlocked(collisionCaches[x - 1][y].collision) && !Collision.isBlocked(collisionCaches[x - 1][y + 1].collision) && !collisionCaches[x - 1][y].collision.has(Collision.BLOCKED_NORTH) && !collisionCaches[x - 1][y + 1].collision.has(Collision.BLOCKED_SOUTH)) {
                            collisionCaches[x - 1][y].data.put(Direction.NORTH, !isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                            collisionCaches[x - 1][y + 1].data.put(Direction.SOUTH, !isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                        }

                        if (!Collision.isBlocked(collisionCaches[x][y].collision) && !Collision.isBlocked(collisionCaches[x][y + 1].collision) && !collisionCaches[x][y].collision.has(Collision.BLOCKED_NORTH) && !collisionCaches[x][y + 1].collision.has(Collision.BLOCKED_SOUTH)) {
                            collisionCaches[x][y].data.put(Direction.NORTH, !isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                            collisionCaches[x][y + 1].data.put(Direction.SOUTH, !isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                        }
                        break;
                    case 2:
                        collisionCaches[x][y].data.put(Direction.NORTH, isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                        collisionCaches[x][y + 1].data.put(Direction.SOUTH, isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);

                        collisionCaches[x][y].collision.clear(Collision.NORTH);
                        collisionCaches[x][y + 1].collision.clear(Collision.SOUTH);

                        collisionCaches[x + 1][y].data.put(Direction.WEST, Collision.Colors.DOOR_CLOSED_COLOR);
                        collisionCaches[x][y].data.put(Direction.EAST, Collision.Colors.DOOR_CLOSED_COLOR);
                        break;
                    case 4:
                        collisionCaches[x][y].data.put(Direction.EAST, isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                        collisionCaches[x + 1][y].data.put(Direction.WEST, isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);

                        collisionCaches[x][y].collision.clear(Collision.EAST);
                        collisionCaches[x + 1][y].collision.clear(Collision.WEST);

                        break;
                    case 8:
                        collisionCaches[x][y].data.put(Direction.SOUTH, isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                        collisionCaches[x][y - 1].data.put(Direction.NORTH, isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);

                        collisionCaches[x][y].collision.clear(Collision.SOUTH);
                        collisionCaches[x][y - 1].collision.clear(Collision.NORTH);

                        if (!Collision.isBlocked(collisionCaches[x - 1][y - 1].collision) && !Collision.isBlocked(collisionCaches[x][y - 1].collision) && !collisionCaches[x - 1][y - 1].collision.has(Collision.BLOCKED_NORTH) && !collisionCaches[x][y - 1].collision.has(Collision.BLOCKED_SOUTH)) {
                            collisionCaches[x - 1][y - 1].data.put(Direction.EAST, !isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                            collisionCaches[x][y - 1].data.put(Direction.WEST, !isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                        }

                        if (!Collision.isBlocked(collisionCaches[x][y].collision) && !Collision.isBlocked(collisionCaches[x - 1][y].collision) && !collisionCaches[x][y].collision.has(Collision.BLOCKED_NORTH) && !collisionCaches[x - 1][y].collision.has(Collision.BLOCKED_SOUTH)) {
                            collisionCaches[x][y].data.put(Direction.WEST, !isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                            collisionCaches[x - 1][y + 1].data.put(Direction.EAST, !isOpen ? Collision.Colors.DOOR_OPEN_COLOR : Collision.Colors.DOOR_CLOSED_COLOR);
                        }
                        break;
                }
            } catch (Exception e) {

            }
        }
    }

    public void updateDoorCache() {
        objectCache = Arrays.asList(Objects.getAll(104));
        doorCache = objectCache.stream().filter(rsObject -> {
            if (rsObject.getType() != RSObject.TYPES.BOUNDARY) return false;
            String[] actions = rsObject.getDefinition().getActions();
            return (Utilities.contains(actions, "Open") || Utilities.contains(actions, "Close"));
        }).collect(Collectors.toList());

    }

    public void updateObjectCache() {
        boundaryCache = objectCache.stream().filter(rsObject -> rsObject.getType() == RSObject.TYPES.BOUNDARY || rsObject.getType() == RSObject.TYPES.WALL).toArray(RSObject[]::new);
        interactiveCache = objectCache.stream().filter(rsObject -> rsObject.getType() == RSObject.TYPES.INTERACTIVE).toArray(RSObject[]::new);
    }

    public void updateCollisionCache() {
        for (int x = 0; x < collisionMap.length; x++) {
            for (int y = 0; y < collisionMap[x].length; y++) {
                Collision collision = collisionMap[x][y];
                HashMap<Direction, Collision.Colors> data = new HashMap<>();

                if (collision.has(Collision.BLOCKED_NORTH) || collision.has(Collision.NORTH)) {
                    data.put(Direction.NORTH, Collision.Colors.BLOCKED_COLOR);
                }

                if (collision.has(Collision.BLOCKED_EAST) || collision.has(Collision.EAST)) {
                    data.put(Direction.EAST, Collision.Colors.BLOCKED_COLOR);
                }

                if (collision.has(Collision.BLOCKED_SOUTH) || collision.has(Collision.SOUTH)) {
                    data.put(Direction.SOUTH, Collision.Colors.BLOCKED_COLOR);
                }

                if (collision.has(Collision.BLOCKED_WEST) || collision.has(Collision.WEST)) {
                    data.put(Direction.WEST, Collision.Colors.BLOCKED_COLOR);
                }

                if (Collision.isBlocked(collision)) {
                    data.put(Direction.ME, Collision.Colors.BLOCKED_COLOR);
                }

                if (collision.is(Collision.OPEN)) {
                    //data.put(Direction.ME, Collision.OPEN_COLOR);
                }

                collisionCaches[x][y] = new CollisionCache(collision, data);
            }
        }
    }

    public void updateCollisionData() {
        collisionMap = getCollisionData();
    }

    public static Collision[][] getCollisionData() {
        final int[][] flags = PathFinding.getCollisionData();
        Collision[][] collisions = new Collision[104][104];
        for (int x = 0; x < flags.length; x++) {
            for (int y = 0; y < flags[x].length; y++) {
                collisions[x][y] = new Collision();
                collisions[x][y].set(flags[x][y]);
            }
        }

        return collisions;
    }

}
