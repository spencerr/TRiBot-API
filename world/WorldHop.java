package scripts.api.world;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.Login.STATE;
import scripts.api.util.Global;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class WorldHop {

    public static List<World> allWorlds = null;
    private static int[] ftpWorlds = null;
    private static int[] p2pWorlds = null;
    public static int[] worldsNotSupported = null;
    public static int[] worldsThatDontExist = null;

    public final static Color WORLD_SWITCH_COLOUR = new Color(189, 152, 57);
    public final Color WORLD_GREEN_ARROW_COLOUR = new Color(47, 130, 43);
    public final static Color WORLD_RED_ARROW_COLOUR = new Color(172, 12, 4);

    public final static int WORLD_PIXEL_SIZE_X = 78;
    public final static int WORLD_PIXEL_SIZE_Y = 17;
    public static int Y_SPACER = 24;
    public static int worldPixelStartX = 208;
    public static int worldPixelStartY = 61;
    public static int worldsPerColumn = -1;
    public static boolean useF2P = false;

    private static int[] toIntArray(ArrayList<Integer> list)  {
        int[] ret = new int[list.size()];
        int i = 0;
        for (Integer e : list)
            ret[i++] = e.intValue();
        return ret;
    }

    static {
        /**/
    }

    public static void load() {
        while(allWorlds == null) {
            System.out.println("Loading runescape world list");

            if((allWorlds = World.loadWorlds()) == null)
                General.sleep(500, 1000);
        }

        ftpWorlds = getF2pWorlds();
        p2pWorlds = getP2pWorlds();
        worldsThatDontExist = getWorldsThatDontExist(false);
        worldsNotSupported = getWorldsNotSupported();
        setWorldColumnData();
    }


    public static void setWorldColumnData() {
        if (allWorlds.size() > 95) {
            worldPixelStartX = 155;
            worldPixelStartY = 35;
            worldsPerColumn = 20;
            Y_SPACER = 23;
            return;
        }

        if (allWorlds.size() > 90) {
            worldPixelStartX = 155;
            worldPixelStartY = 37;
            worldsPerColumn = 19;
            return;
        }

        if(allWorlds.size() > 80) { // pattern has shown if there are more than 68 worlds then they fit 18 worlds per column
            worldPixelStartX = 155;
            worldPixelStartY = 51;
            worldsPerColumn = 18;
            return;
        }

        worldPixelStartX = 155;
        worldPixelStartY = 61;
        worldsPerColumn = 17;
    }



    private static int worldhopAttempts = 0;

    private static int failedworldhopAttempts = 0;

    public static int getRandomF2pWorld() {
        return ftpWorlds[General.random(0, ftpWorlds.length - 1)];
    }

    public static int getRandomMembersWorld() {
        return p2pWorlds[General.random(0, p2pWorlds.length -1)];
    }

    public static int getRandomSafeWorld(boolean members) {
        if (allWorlds == null) load();
        ArrayList<World> safeWorlds = new ArrayList<>(allWorlds.stream().filter(world -> world.isMember() == members && !world.isBlueWorld() && !world.isDeadman() && !world.isPvp() && !world.isHighRisk() && !world.getActivity().contains("skill")).collect(Collectors.toList()));
        return safeWorlds.get(Global.srand(safeWorlds.size() - 1)).getId() - 300;
    }

    public static boolean isAtWorldHopScreen() {
        return Screen.getColorAt(11,0).equals(WORLD_SWITCH_COLOUR);

    }

    public static boolean hasMisconfiguredWorldSettings() {
        return Screen.getColorAt(301, 9).equals(WORLD_RED_ARROW_COLOUR);
    }

    public final static int[] getWorldsNotSupported() {
        if (allWorlds == null) return new int[0];
        ArrayList<Integer> worldsNotSupported = new ArrayList<>();
        for(World world : allWorlds) {
            if(!useF2P && !world.isMember() || world.isDeadman() || world.isPvp() || world.getActivity().contains("skill total") || world.getPlayerCount() < 5 || world.isBlueWorld()) { // 33554433 is blue world?
                worldsNotSupported.add(world.getId() - 300);
            }
        }
        for(int i : worldsThatDontExist) { // combine both arrays; worlds that are not supported & worlds that dont exist
            worldsNotSupported.add(i);
        }
        int[] worlds = toIntArray(worldsNotSupported);
        return worlds;
    }

    public final static int[] getWorldsThatDontExist(boolean use300Format) {
        ArrayList<Integer> worldsThatDontExist = new ArrayList<Integer>();
        for(int i = 0; i < allWorlds.size(); i++) {
            if(i == allWorlds.size() - 1)
                break;

            int worldOne = allWorlds.get(i).getId();
            int worldTwo = allWorlds.get(i+1).getId();
            int difference = worldTwo - worldOne;

            if(difference <= 1)
                continue;

            for(int s = worldOne + 1; s < worldTwo; s++) {
                worldsThatDontExist.add(s - (!use300Format ? 300 : 0));
            }
        }
        return toIntArray(worldsThatDontExist);
    }

    public final static int[] getF2pWorlds() {
        ArrayList<Integer> f2pWorlds = new ArrayList<Integer>();
        for(World world : allWorlds) {
            if(!world.isMember() && !world.getActivity().contains("skill total"))
                f2pWorlds.add(world.getId() - 300);
        }
        int[] worlds = toIntArray(f2pWorlds);

        return worlds;
    }

    public final static int[] getP2pWorlds() {
        ArrayList<Integer> p2pWorlds = new ArrayList<Integer>();
        for(World world : allWorlds) {
            if(world.isMember() && !world.isPvp() && !world.isDeadman() && !world.isBlueWorld() && !world.getActivity().contains("skill total"))
                p2pWorlds.add(world.getId() - 300);
        }

        return toIntArray(p2pWorlds);
    }

    public static void setUseF2P(boolean useF2p) {
        useF2P = useF2p;
        worldsNotSupported = getWorldsNotSupported();
    }

    public static boolean isInMemberWorld() {
        int world = getWorld();
        for(int i : ftpWorlds) {
            if(i == world)
                return false;
        }
        return true;
    }

    public static int getWorld() {
        return Game.getCurrentWorld() - 300;
    }

    public static void drawWorlds(Graphics2D g2) {
        if (allWorlds == null) return;

        for (World world : allWorlds) {
            Rectangle rect = getWorldClickArea(world.getId());
            g2.draw(rect);
        }
    }


    public static void closeWorldHopScreen() {
        Mouse.click(734, 11, 1);
    }

    public static boolean switchWorld(final int world) {

        if (allWorlds == null)
            load();

        General.println("hopping to world " + world);
        long timeout = Timing.currentTimeMillis() + 60000;

        while (true && timeout > Timing.currentTimeMillis()) {

            if(failedworldhopAttempts > 2) {
                if(isAtWorldHopScreen()) {
                    General.println("detected worlds are most likely offline");
                    closeWorldHopScreen();
                    General.sleep(500,1000);
                    failedworldhopAttempts = 0;
                }
            }

            if(worldhopAttempts >= 4) {
                worldhopAttempts = 0;
                failedworldhopAttempts++;
                return false;
            }

            if(Game.getGameState() != 10)
                Interfaces.closeAll();

            if(Combat.isUnderAttack()) {
                General.println("under attack exiting world hop loop");
                worldhopAttempts = 0;
                return false;
            }

            if(!isAtWorldHopScreen() && getWorld() == world % 300)  {
                //General.println("it returned true, currenet world = designated world");
                worldhopAttempts = 0;
                return true;
            }

            if (Login.getLoginState() == STATE.INGAME) {
                if(Login.logout()) {
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(50,100);
                            return Game.getGameState() != 30;
                        }
                    }, General.random(800,1000));
                }
            } else if(Login.getLoginState().equals(STATE.WELCOMESCREEN)) {
                Login.login();
            } else if (isAtWorldHopScreen()) {
                if (hasMisconfiguredWorldSettings()) {
                    Mouse.click(301, 9, 1);
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return !hasMisconfiguredWorldSettings();
                        }
                    }, 2000);

                } else {

                    if(worldsPerColumn == -1)
                        setWorldColumnData();

                    Rectangle clickArea = getWorldClickArea(world);
                    Point location = new Point((int)clickArea.getCenterX(), (int)clickArea.getCenterY());
                    Mouse.hop(location);
                    General.sleep(200);
                    Mouse.sendPress(location, 1);
                    Mouse.sendRelease(location, 1);
                    Mouse.sendClickEvent(location, 1);

                    if(Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return !isAtWorldHopScreen() && getWorld() == world;
                        }
                    }, 1000)) {
                        //General.println("it returned true, we not at world hop screen and currenet world = designated world");
                        return true;
                    } else {
                        worldhopAttempts++;
                    }
                }
            } else {
                if(getWorld() != world) {
                    Rectangle rec = new Rectangle(10, 486, 100, 493);
                    Point location = rec.getLocation();
                    Mouse.hop(location);
                    Mouse.sendPress(location, 1);
                    Mouse.sendRelease(location, 1);
                    Mouse.sendClickEvent(location, 1);
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return isAtWorldHopScreen();
                        }
                    }, 5000);
                }
            }
        }
        worldhopAttempts = 0;
        failedworldhopAttempts++;
        return !isAtWorldHopScreen() && getWorld() == world;
    }

    public static boolean isInP2P() {
        for (int world : WorldHop.getP2pWorlds()) {
            if (world == WorldHop.getWorld() || world - 300 == WorldHop.getWorld() || WorldHop.getWorld() - 300 == world) {
                return true;
            }
        }

        return false;
    }


    public static boolean isWorldSupported(int world) {
        for (int i=0; i < worldsNotSupported.length; i++) {
            if (worldsNotSupported[i] == world) {
                return false;
            }
        }
        return true;
    }

    public static int getRandomWorld() {
        int randomWorld;
        while (!isWorldSupported(randomWorld = (allWorlds.get(General.random(0, allWorlds.size() - 1))).getId() - 300));
        return randomWorld;
    }

    public static Rectangle getWorldClickArea(int world) {
        return getWorldClickArea(world, worldsPerColumn, true);
    }

    public static Rectangle getWorldClickArea(int world, int worldPerColumn, boolean checkExist) {
        if(checkExist && !isWorldSupported(world)) {
            General.println("World " + world + " is not supported");
            return null;
        }


        int diff = 0;
        world %= 300;
        for(int i : worldsThatDontExist) {
            if(world > i) {
                diff++;
            }
        }

        int x = (world - diff) / worldPerColumn;
        int y = (world - diff) % worldPerColumn;
        if (x == -1 || y == -1) {
            return null;
        } else {
            if(y == 0) {
                y =  worldPerColumn;
                x -= 1;
            }
            int xR = worldPixelStartX + (x * 93);
            int yR = worldPixelStartY + ((y - 1) * Y_SPACER);
            return new Rectangle(xR, yR, WORLD_PIXEL_SIZE_X, WORLD_PIXEL_SIZE_Y);
        }
    }

}
