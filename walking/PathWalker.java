package scripts.api.walking;

import org.tribot.api.General;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.abc.preferences.WalkingPreference;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.api.antiban.Antiban;
import scripts.api.pattern.BooleanLambda;
import scripts.api.util.Movement;
import scripts.api.util.Utilities;
import scripts.api.webwalker.WebWalker;
import scripts.api.webwalker.local.walker_engine.interaction_handling.AccurateMouse;
import scripts.api.webwalker.local.walker_engine.interaction_handling.InteractionHelper;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Spencer on 10/18/2016.
 */
public class PathWalker {

    private static int WALKING_DELAY_SHORT = 125;
    private static int WALKING_DELAY_LONG = 425;
    public static List<RSTile> path;
    public static List<RSObject> doors;

    public static int index = 0;
    public static boolean isDoor = false;
    public static RSObject doorToOpen = null;

    public static boolean walkPath(List<RSTile> walkingPath, BooleanLambda stoppingCondition) {
        path = walkingPath;
        for (RSTile tile : path) {
            if (!walkTo(tile, stoppingCondition))
                return stoppingCondition != null && stoppingCondition.active();
        }

        return Player.getPosition().distanceTo(path.get(path.size() - 1)) < 3 || (stoppingCondition != null && stoppingCondition.active());
    }

    public static void getNext() {
        int next = General.random(8, 14);
        for (; index < path.size() - 1; index++) {
            if (Player.getPosition().distanceTo(path.get(index)) > next) {
                index--;
                return;
            }

            for (RSObject door : doors) {
                if (door.getPosition().equals(path.get(index))) {
                    isDoor = true;
                    doorToOpen = door;
                    break;
                }
            }

            if (isDoor && !PathFinding.canReach(path.get(index + 1 > path.size() - 1 ? index : index + 1), false)) {
                break;
            } else {
                isDoor = false;
                doorToOpen = null;
            }
        }
    }

    public static void drawPath(Graphics2D g2) {
        if (path == null || path.size() == 0) return;

        for (int i = 0; i < path.size(); i++) {
            if (path.get(i) == null) return;

            if (i == 0) g2.setColor(Color.GREEN);
            else if (i == path.size() - 1) g2.setColor(Color.red);
            else g2.setColor(Color.cyan);

            if (doors != null) {
                for (RSObject door : doors) {
                    if (door != null) {
                        RSTile position = door.getPosition();
                        if (position != null) {
                            if (position.equals(path.get(i))) {
                                g2.setColor(Color.ORANGE);
                            }
                        }
                    }
                }
            }

            if (i == index) g2.setColor(Color.PINK);

            RSTile tile = path.get(i);
            if (tile != null && tile.isOnScreen()) g2.draw(Projection.getTileBoundsPoly(tile, 0));
        }
    }

    private static void reset() {
        index = 0;
        isDoor = false;
        doorToOpen = null;
    }

    public static boolean walkTo(Positionable positionable, BooleanLambda lambda) {
        return walkTo(positionable, lambda, false);
    }

    public static void generatePath(Positionable posToWalk) {
        reset();
        path = null;
        doors = null;
        path = new DPath().findPath(posToWalk.getPosition()).stream().map(BasicRSTile::getTile).collect(Collectors.toList());
        doors = Arrays.asList(Objects.find(25, new Filter<RSObject>() {
            @Override
            public boolean accept(RSObject rsObject) {
                return rsObject.getType() == RSObject.TYPES.BOUNDARY && Utilities.contains(rsObject.getDefinition().getActions(), "Open");
            }
        }));
    }

    public static boolean walkToClickable(Positionable positionable, boolean exact) {
        return walkToClickable(positionable, null, exact);
    }

    public static boolean walkToClickable(Positionable posToWalk, BooleanLambda stoppingCondition, boolean exact) {
        reset();
        path = null;
        doors = null;

        long walkTimer = System.currentTimeMillis();
        int lastIndex = 0;
        int failCount = 0;

        int give = exact ? 0 : 3;

        while (!Player.getPosition().equals(posToWalk.getPosition())) {
            if (Login.getLoginState() != Login.STATE.INGAME) return false;

            if (System.currentTimeMillis() - walkTimer > 90*1000) {
                return false;
            }

            Antiban.activateRun();

            if (stoppingCondition != null && stoppingCondition.active())
                return true;

            Positionable tileToWalk = posToWalk;
            if (tileToWalk instanceof RSObject || !PathFinding.isTileWalkable(tileToWalk)) {
                tileToWalk = Movement.getWalkableTile(posToWalk.getPosition());
                if (tileToWalk == null) {
                    tileToWalk = posToWalk;
                }
            }

            if (!exact && InteractionHelper.isOnScreenAndClickable((Clickable07) posToWalk) && Movement.canReach(tileToWalk)) {
                return true;
            }

            if (!Movement.isInLoadedRegion(tileToWalk))
                return false;

            if (failCount > 25) {
                return false;
            }


            path = new DPath().findPath(tileToWalk.getPosition()).stream().map(BasicRSTile::getTile).collect(Collectors.toList());
            doors = Arrays.asList(Objects.find(25, new Filter<RSObject>() {
                @Override
                public boolean accept(RSObject rsObject) {
                    return rsObject.getType() == RSObject.TYPES.BOUNDARY && Utilities.contains(rsObject.getDefinition().getActions(), "Open");
                }
            }));

            if (path == null || path.size() <= 1) {
                failCount++;
                continue;
            }

            index = 0;
            getNext();

            if (index == -1) return false;

            RSTile tile = path.get(index);

            if (isDoor && doorToOpen != null) {
                //if (!InteractionHelper.isOnScreenAndClickable(doorToOpen) || Player.getPosition().distanceTo(doorToOpen) >= 4) {
                    InteractionHelper.quickFocus(doorToOpen);
                //}

                if (InteractionHelper.isOnScreenAndClickable(doorToOpen) && AccurateMouse.click(doorToOpen, "Open")) {
                    Condition condition = Movement.getWalkingCondition(() -> Movement.canReach(path.get(index + 1 > path.size() - 1 ? index : index + 1)) || Player.getPosition() == path.get(index + 1 > path.size() - 1 ? index : index + 1));
                    while (!condition.active()) {
                        Antiban.doIdleActions();
                        General.sleep(WALKING_DELAY_SHORT, WALKING_DELAY_LONG);
                    }
                } else {
                    while (!((Antiban.getWalkingPreference(Player.getPosition().distanceTo(tile)) == WalkingPreference.SCREEN) ? Walking.clickTileMS(tile, 1) : Walking.clickTileMM(tile, 1))) {
                        index--;
                        if (index < 0) return false;
                        tile = path.get(index);
                    }

                    InteractionHelper.quickFocus(doorToOpen);

                    final RSTile finalTile = tile;
                    Condition condition = Movement.getWalkingCondition(() -> Player.getPosition().distanceTo(finalTile) <= 3 || (stoppingCondition != null && stoppingCondition.active()) || InteractionHelper.isOnScreenAndClickable(doorToOpen));
                    while (!condition.active()) {
                        Antiban.doIdleActions();
                        General.sleep(WALKING_DELAY_SHORT, WALKING_DELAY_LONG);
                    }
                }
            } else {
                if (Player.getPosition().distanceTo(tile) < 3 || !PathFinding.isTileWalkable(tile) || !PathFinding.canReach(tile, false)) {
                    tile = Movement.getWalkableTile(tile);
                }

                while (!((Antiban.getWalkingPreference(Player.getPosition().distanceTo(tile)) == WalkingPreference.SCREEN) ? Walking.clickTileMS(tile, 1) : Walking.clickTileMM(tile, 1))) {
                    index--;
                    if (index < 0) return false;
                    tile = path.get(index);
                }

                if (tile.distanceTo(posToWalk) <= 3) {
                    InteractionHelper.quickFocus((Clickable07) posToWalk);
                }

                final RSTile finalTile = tile;
                Condition condition = Movement.getWalkingCondition(() -> InteractionHelper.isOnScreenAndClickable((Clickable07) posToWalk) || Player.getPosition().distanceTo(finalTile) <= 3 || (stoppingCondition != null && stoppingCondition.active()));
                while (!condition.active()) {
                    Antiban.doIdleActions();
                    General.sleep(WALKING_DELAY_SHORT, WALKING_DELAY_LONG);
                }
            }
        }

        reset();
        path = null;
        doors = null;

        return Player.getPosition().distanceTo(posToWalk) < give && PathFinding.canReach(posToWalk, false);
    }

    public static boolean walkTo(Positionable posToWalk, BooleanLambda stoppingCondition, boolean useAntiban) {
        reset();
        path = null;
        doors = null;

        long walkTimer = System.currentTimeMillis();

        int lastIndex = 0;
        int failCount = 0;
        while (Player.getPosition().distanceTo(posToWalk) >= 3 || !Movement.canReach(posToWalk)) {
            if (Login.getLoginState() != Login.STATE.INGAME) return false;

            if (System.currentTimeMillis() - walkTimer > 90*1000) {
                Walking.blindWalkTo(posToWalk);
                return false;
            }

            if (Player.getPosition().equals(posToWalk)) return true;

            Antiban.activateRun();

            if (stoppingCondition != null && stoppingCondition.active())
                return true;

            Positionable tileToWalk = posToWalk;
            if (tileToWalk instanceof RSObject || !PathFinding.isTileWalkable(tileToWalk)) {
                tileToWalk = Movement.getWalkableTile(posToWalk.getPosition());
                if (tileToWalk == null) {
                    tileToWalk = posToWalk;
                }
            }

            if (!Movement.isInLoadedRegion(tileToWalk))
                return false;

            if (failCount > 25) {
                if (!WebWalker.walkTo(tileToWalk.getPosition())) {
                    if (!WebWalking.walkTo(tileToWalk)) {
                        return false;
                    }
                }
            }

            General.println("Attempting to walk to " + tileToWalk.toString());

            path = new DPath().findPath(tileToWalk.getPosition()).stream().map(BasicRSTile::getTile).collect(Collectors.toList());
            doors = Arrays.asList(Objects.find(25, new Filter<RSObject>() {
                @Override
                public boolean accept(RSObject rsObject) {
                    return rsObject.getType() == RSObject.TYPES.BOUNDARY && Utilities.contains(rsObject.getDefinition().getActions(), "Open");
                }
            }));

            if (path == null || path.size() <= 1) {
                General.println("Path is null or 1");
                if (!WebWalker.walkTo(posToWalk.getPosition())) {
                    if (!Walking.blindWalkTo(tileToWalk)) {
                        return false;
                    }
                }

                continue;
            }

            index = 0;
            getNext();

            if (index == -1) return false;

            if (lastIndex == index) {
                failCount++;
            } else {
                failCount = 0;
            }

            lastIndex = index;

            RSTile tile = path.get(index);
            if (isDoor && doorToOpen != null) {
                if (doorToOpen.isOnScreen() && doorToOpen.isClickable()) {
                    if (AccurateMouse.click(doorToOpen, "Open")) {
                        Condition condition = Movement.getWalkingCondition(() -> Movement.canReach(path.get(index + 1 > path.size() - 1 ? index : index + 1)) || Player.getPosition() == path.get(index + 1 > path.size() - 1 ? index : index + 1));
                        while (!condition.active()) {
                            Antiban.doIdleActions();
                            General.sleep(WALKING_DELAY_SHORT, WALKING_DELAY_LONG);
                        }
                        General.sleep(WALKING_DELAY_SHORT, WALKING_DELAY_LONG);
                    }
                } else {
                    while (!((Antiban.getWalkingPreference(Player.getPosition().distanceTo(tile)) == WalkingPreference.SCREEN) ? Walking.clickTileMS(tile, 1) : Walking.clickTileMM(tile, 1))) {
                        index--;
                        if (index < 0) return false;
                        tile = path.get(index);
                    }

                    final RSTile finalTile = tile;
                    Condition condition = Movement.getWalkingCondition(() -> Player.getPosition().distanceTo(finalTile) < 3 || (stoppingCondition != null && stoppingCondition.active()));
                    while (!condition.active()) {
                        Antiban.doIdleActions();
                        General.sleep(WALKING_DELAY_SHORT, WALKING_DELAY_LONG);
                    }
                    General.sleep(WALKING_DELAY_SHORT, WALKING_DELAY_LONG);
                }
            } else {
                if (Player.getPosition().distanceTo(tile) < 3 || !PathFinding.isTileWalkable(tile) || !PathFinding.canReach(tile, false)) {
                    tile = Movement.getWalkableTile(tile);
                }

                while (!((Antiban.getWalkingPreference(Player.getPosition().distanceTo(tile)) == WalkingPreference.SCREEN) ? Walking.clickTileMS(tile, 1) : Walking.clickTileMM(tile, 1))) {
                    index--;
                    if (index < 0) return false;
                    tile = path.get(index);
                }

                final RSTile finalTile = tile;
                Condition condition = Movement.getWalkingCondition(() -> Player.getPosition().distanceTo(finalTile) < 3 || (stoppingCondition != null && stoppingCondition.active()));
                while (!condition.active()) {
                    Antiban.doIdleActions();
                    General.sleep(WALKING_DELAY_SHORT, WALKING_DELAY_LONG);
                }
                General.sleep(WALKING_DELAY_SHORT, WALKING_DELAY_LONG);
            }

            if (useAntiban) {
                Antiban.react();
            }

            General.sleep(250, 750);
        }

        reset();
        path = null;
        doors = null;

        return Player.getPosition().distanceTo(posToWalk) < 3 && PathFinding.canReach(posToWalk, false);
    }
}
