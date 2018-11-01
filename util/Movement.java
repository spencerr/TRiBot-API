package scripts.api.util;

import org.tribot.api.General;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.util.Sorting;
import org.tribot.api.util.abc.preferences.WalkingPreference;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;
import org.tribot.api2007.util.PathNavigator;
import scripts.api.antiban.Antiban;
import scripts.api.pattern.BooleanLambda;
import scripts.api.pattern.Condition;
import scripts.api.walking.BasicRSTile;
import scripts.api.walking.DPath;
import scripts.api.walking.PathWalker;
import scripts.api.webwalker.WebWalker;
import scripts.api.webwalker.local.walker_engine.WalkingCondition;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class that manages movement logic.
 *
 * @author Laniax
 */
public class Movement {

    private static DPathNavigator nav = new DPathNavigator();

    private static final int NOT_MOVING_TIMEOUT = 2000;

    static {
        nav.setStoppingConditionCheckDelay(50);
        Walking.setWalkingTimeout(7500);
    }

    public static DPathNavigator getNavigator() {
        return nav;
    }

    /**
     * Checks if we can reach the specified Positionable (RSTile/RSObject/RSPlayer/RSGroundItem etc)
     *
     * @param toReach
     * @return if we can reach the clickable07 or not.
     */
    public static boolean canReach(Positionable toReach) {
        if (toReach instanceof RSObject) {
            toReach = getWalkableTile(toReach.getPosition());
        }
        return PathFinding.canReach(toReach, toReach instanceof RSObject);

    }

    /**
     * Check if a position is in the currently loaded region.
     *
     * @param pos to check
     * @return true if in the region, false otherwise.
     */
    public static boolean isInLoadedRegion(Positionable pos) {

        final RSTile base = new RSTile(Game.getBaseX(), Game.getBaseY());
        final RSArea chunk = new RSArea(base, new RSTile(base.getX() + 103, base.getY() + 103));

        return chunk.contains(pos) && pos.getPosition().getPlane() == Game.getPlane();
    }

    /**
     * Gets all the walkable tiles in the {@link RSArea}.
     *
     * @param area
     * @return array of walkable tiles, empty if none are found.
     */
    public static RSTile[] getAllWalkableTiles(RSArea area) {

        ArrayList<RSTile> walkables = new ArrayList<>();

        for (RSTile tile : area.getAllTiles()) {
            if (PathFinding.isTileWalkable(tile) && PathFinding.canReach(tile, false))
                walkables.add(tile);
        }

        return walkables.toArray(new RSTile[walkables.size()]);
    }

    public static RSTile[] getTilesAround(int distance, RSObject object) {
        if (object == null)
            return null;
        List<RSTile> list = new ArrayList<RSTile>();
        for (int i = (0 - distance); i < (distance + 1); i++) {
            for (int j = (0 - distance); j < (distance + 1); j++) {
                list.add(new RSTile(object.getPosition().getX() + i, object.getPosition().getY() + j,
                        object.getPosition().getPlane()));
            }
        }
        return list.stream().toArray(RSTile[]::new);
    }

    public static boolean canReach(RSObject object) {
        if (object == null)
            return false;
        if (PathFinding.canReach(object, true))
            return true;
        RSTile[] tiles = object.getAllTiles();
        for (RSTile tile : tiles) {
            if (PathFinding.canReach(tile, true))
                return true;
        }
        return false;
    }

    public static RSTile getClosestReachableTile(RSTile object) {
        if (object == null || !canReach(object))
            return null;

        int radius = 0, distance = Integer.MAX_VALUE;
        RSTile closest = null;
        while (closest == null && radius <= 3) {
            for (int i = (0 - radius); i < (radius + 1); i++) {
                for (int j = (0 - radius); j < (radius + 1); j++) {
                    RSTile tile = new RSTile(object.getPosition().getX() + i, object.getPosition().getY() + j,
                            object.getPosition().getPlane());
                    if (Player.getPosition().distanceTo(tile) < distance && PathFinding.canReach(tile, false)) {
                        closest = tile;
                        distance = Player.getPosition().distanceTo(tile);
                    }
                }
            }
            radius++;
        }
        return closest;
    }

    public static RSTile getWalkableTile(RSTile tile) {
        return getWalkableTile(tile, 1);
    }

    private static RSTile getWalkableTile(RSTile tile, int dist) {
        RSArea area = new RSArea(tile, dist);
        RSTile[] walkables = getAllWalkableTiles(area);

        if (walkables.length == 0)
            return dist == 5 ? tile : getWalkableTile(tile, dist + 1);

        Sorting.sortByDistance(walkables, tile, true);
        return walkables[0];
    }

    /**
     * Walks to the destination using DPathNavigator
     *
     * @param posToWalk
     * @return true if reached the destination, false otherwise.
     */
    public static boolean walkToPrecise(Positionable posToWalk) {

        // DPathNavigator cannot traverse outside the currently loaded region
        if (!isInLoadedRegion(posToWalk))
            return false;

        return nav.traverse(posToWalk);
    }

    /**
     * Walks to the destination using WebWalking
     *
     * @param posToWalk
     * @return true if reached the destination, false otherwise.
     */
    public static boolean webWalkTo(final Positionable posToWalk) {

        Antiban.activateRun();

        if (Antiban.getWalkingPreference(Player.getPosition().distanceTo(posToWalk)) == WalkingPreference.SCREEN) {

            RSTile[] path = Walking.generateStraightScreenPath(posToWalk);

            if (Walking.walkScreenPath(path, getWalkingCondition(posToWalk), General.random(10000, 12500)))
                return true;
        }


        return WebWalking.walkTo(posToWalk, getWalkingCondition(posToWalk), 100);
    }

    public static void setUseCustomDoors(RSObject[] doors) {
        nav.overrideDoorCache(true, doors);
    }

    public static void setUseDefaultDoors() {
        nav.overrideDoorCache(false, null);
    }

    public static void setExcludeTiles(final Positionable[] tiles) {

        nav.setExcludeTiles(tiles);
    }

    public static Positionable[] getExcludeTiles() {

        return nav.getExcludeTiles();
    }

    /**
     * Walks to the position using either DPathNavigator for close by precision or WebWalking for greater lengths.
     * <p>
     * Checks if run can be toggled.
     *
     * @param posToWalk
     * @return if successfully reached destination or not.
     */

    public static boolean stuckWalkTo(Positionable posToWalk) {
        PathNavigator navigator = new PathNavigator();
        navigator.overrideStartPosition(Player.getPosition());
        return navigator.traverse(posToWalk);
    }

    public static Positionable getNearestInRegion(Positionable posToWalk) {
        int newX = 0;
        int newY = 0;
        int x = posToWalk.getPosition().getX() - Game.getBaseX();
        int y = posToWalk.getPosition().getY() - Game.getBaseY();

        RSTile playerPos = Player.getPosition();

        int offsetX = playerPos.getX() - Game.getBaseX();
        int offsetY = playerPos.getY() - Game.getBaseY();
        if (offsetX - x >= 0 && offsetX - x < 104) {
            newX = x;
        } else {
            newX = offsetX;
        }

        if (offsetY - y >= 0 && offsetX - y < 104) {
            newY = y;
        } else {
            newY = offsetY;
        }

        return new RSTile(newX, newY);
    }

    public static boolean walkToBeta(Positionable posToWalk, BooleanLambda lambda) {
        return walkTo(posToWalk, lambda == null ? null : new Condition(lambda));
    }

    public static boolean walkToBeta(Positionable posToWalk) {
        return walkToBeta(posToWalk, null);
    }

    public static boolean walkTo(Positionable posToWalk, Condition stoppingCondition) {
        /*if (!isInLoadedRegion(posToWalk)) {
            posToWalk = getNearestInRegion(posToWalk);
        }
        return PathWalker.walkTo(posToWalk, stoppingCondition);*/

        Positionable realTile = posToWalk;

        if (isInLoadedRegion(posToWalk) && (posToWalk instanceof RSObject || !PathFinding.isTileWalkable(posToWalk))) {
            posToWalk = getWalkableTile(posToWalk.getPosition());
        }

        final RSTile tile = posToWalk == null ? null : posToWalk.getPosition();

        if (tile == null) {
            Walking.blindWalkTo(realTile);
            return false;
        }


        Antiban.activateRun();

        if (isInLoadedRegion(posToWalk)) {
            General.println("PathWalking to " + posToWalk.toString());
            return PathWalker.walkTo(posToWalk, stoppingCondition);
            //return PathWalker.walkTo(tile, stoppingCondition);
        } else {
            General.println("WebWalking to " + posToWalk.toString());
            final long walkTimer = System.currentTimeMillis();
            if (!WebWalker.walkTo(posToWalk.getPosition(), new WalkingCondition() {
                @Override
                public State action() {
                    if (System.currentTimeMillis() - walkTimer > 5*60*1000) {
                        return State.EXIT_OUT_WALKER_FAIL;
                    }

                    if (isInLoadedRegion(tile)) {
                        List<RSTile> path = new DPath().findPath(tile.getPosition()).stream().map(BasicRSTile::getTile).collect(Collectors.toList());
                        if (path.size() > 0) {
                            return State.EXIT_OUT_WALKER_SUCCESS;
                        }
                    }

                    return State.CONTINUE_WALKER;
                }
            })) {
                if (!WebWalking.walkTo(posToWalk, new Condition() {
                    @Override
                    public boolean active() {
                        return isInLoadedRegion(tile) || stoppingCondition.active();
                    }
                }, 1000)) {
                    return false;
                }
            }
        }

        return false;
    }

    public static boolean walkTo(Positionable posToWalk, Condition stoppingCondition, boolean useAntiban) {
        /*if (!isInLoadedRegion(posToWalk)) {
            posToWalk = getNearestInRegion(posToWalk);
        }
        return PathWalker.walkTo(posToWalk, stoppingCondition);*/

        if (isInLoadedRegion(posToWalk) && (posToWalk instanceof RSObject || !PathFinding.isTileWalkable(posToWalk))) {
            posToWalk = getWalkableTile(posToWalk.getPosition());
        }

        final RSTile tile = posToWalk == null ? null : posToWalk.getPosition();

        if (tile == null)
            return false;

        Antiban.activateRun();

        if (isInLoadedRegion(posToWalk)) {
            General.println("PathWalking to " + posToWalk.toString());
            return PathWalker.walkTo(posToWalk, stoppingCondition, useAntiban);
        } else {
            General.println("WebWalking to " + posToWalk.toString());
            final long walkTimer = System.currentTimeMillis();
            if (!WebWalker.walkTo(posToWalk.getPosition(), new WalkingCondition() {
                @Override
                public State action() {
                    if (System.currentTimeMillis() - walkTimer > 5*60*1000) {
                        return State.EXIT_OUT_WALKER_FAIL;
                    }

                    if (isInLoadedRegion(tile)) {
                        List<RSTile> path = new DPath().findPath(tile.getPosition()).stream().map(BasicRSTile::getTile).collect(Collectors.toList());
                        if (path.size() > 0) {
                            return State.EXIT_OUT_WALKER_SUCCESS;
                        }
                    }

                    return State.CONTINUE_WALKER;
                }
            })) {
                if (!WebWalking.walkTo(posToWalk, new Condition() {
                    @Override
                    public boolean active() {
                        return isInLoadedRegion(tile) || stoppingCondition.active();
                    }
                }, 1000)) {
                    return false;
                }
            }
        }

        return false;
    }

    public static boolean walkTo(Positionable posToWalk, BooleanLambda stoppingCondition) {
        return walkTo(posToWalk, new Condition(stoppingCondition));
    }

    public static boolean walkTo(Positionable posToWalk) {
        return walkTo(posToWalk, getWalkingCondition(posToWalk));
    }

    public static Condition getWalkingCondition(BooleanLambda lambda) {

        return new Condition() {

            long notMovingSince = 0;
            long noDistanceSince = 0;
            RSTile lastTile = null;

            @Override
            public boolean active() {
                if (lastTile != null && Player.getPosition().distanceTo(lastTile) < 2 && noDistanceSince == 0) {
                    noDistanceSince = System.currentTimeMillis();
                } else {
                    if (lastTile == null || Player.getPosition().distanceTo(lastTile) >= 2) {
                        lastTile = Player.getPosition();
                        noDistanceSince = 0;
                    }

                    if (noDistanceSince != 0 && System.currentTimeMillis() - noDistanceSince > 7500) {
                        return true;
                    }

                    if (!Player.isMoving()) {
                        if (notMovingSince == 0) {
                            notMovingSince = System.currentTimeMillis();
                        } else if (System.currentTimeMillis() - notMovingSince > NOT_MOVING_TIMEOUT) {
                            return true;
                        }
                    } else {
                        notMovingSince = 0;
                    }
                }
                return lambda.active();
            }
        };
    }

    public static Condition animationWait(BooleanLambda lambda, int animationLength) {
        return new Condition() {

            long notAnimatingSince = 0;

            @Override
            public boolean active() {

                if (!Player.isMoving() && Player.getAnimation() == -1) {
                    if (notAnimatingSince == 0) {
                        notAnimatingSince = System.currentTimeMillis();
                    } else if (System.currentTimeMillis() - notAnimatingSince > animationLength) {
                        return true;
                    }
                } else {
                    notAnimatingSince = 0;
                }

                return lambda.active();
            }
        };
    }

    /**
     * Gets a stopping condition that keeps track for when you reach the target, or if you stood still for to long.
     *
     * @param destination
     * @return
     */
    public static Condition getWalkingCondition(final Positionable destination) {
        return getWalkingCondition(() -> Player.getPosition() == destination);
    }


    /**
     * Walks to the position by straight user-made paths. Will try to handle doors.
     * This is mostly used in areas which WebWalking has not mapped.
     * <p>
     * Checks if run can be toggled.
     *
     * @param path
     * @return if succesfully reached destination or not.
     */
    public static boolean walkPath(final RSTile[] path, Condition stoppingCondition) {
        Antiban.activateRun();

        for (int i = 0; i < path.length; i++) {
            if (i < path.length - 1 && Player.getPosition().distanceTo(path[i]) < Player.getPosition().distanceTo(path[i+1])) {
                if (!Movement.walkTo(path[i], stoppingCondition))
                    return false;
            }
        }

        return true;
    }

    public static boolean walkPath(final RSTile[] path, BooleanLambda condition) {
        return walkPath(path, new Condition(condition));
    }

    public static boolean walkPath(final RSTile[] path) {
        return walkPath(path, getWalkingCondition(path[path.length - 1]));
    }


}
