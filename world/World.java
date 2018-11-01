package scripts.api.world;

import org.tribot.api.General;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class World {
    private final int id;
    private final boolean member;
    private final boolean pvp;
    private final boolean highRisk;
    private final boolean deadman;
    private final boolean bountyHunter;
    private final String host;
    private final String activity;
    private final int serverLoc;
    private final int playerCount;
    private final int flag;
    private final boolean blueWorld;

    public World(int id, int flag, boolean member, boolean pvp, boolean highRisk, String host, String activity, int serverLoc, int playerCount) {
        this.id = id;
        this.flag = flag;
        this.member = member;
        this.pvp = pvp;
        this.highRisk = highRisk;
        this.host = host;
        this.activity = activity;
        this.serverLoc = serverLoc;
        this.playerCount = playerCount;
        this.deadman = activity.toLowerCase().contains("deadman");
        this.bountyHunter = activity.toLowerCase().contains("bounty");
        this.blueWorld = (flag == 33554433);
    }


    public boolean isBlueWorld() {
        return blueWorld;
    }

    public int getId() {
        return id;
    }

    public boolean isBountyhunter() {
        return bountyHunter;
    }

    public int getFlag() {
        return flag;
    }

    public boolean isMember() {
        return member;
    }

    public boolean isPvp() {
        return pvp;
    }

    public boolean isHighRisk() {
        return highRisk;
    }

    public boolean isDeadman() {
        return deadman;
    }

    public String getHost() {
        return host;
    }

    public String getActivity() {
        return activity;
    }

    public int getServerLoc() {
        return serverLoc;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public static List<World> loadWorlds()  {
        List<World> worldList = new ArrayList<>();
        try {

            URLConnection conn = new URL("http://oldschool.runescape.com/slr").openConnection();
            try (DataInputStream dis = new DataInputStream(conn.getInputStream())) {
                int size = dis.readInt() & 0xFF;
                int worldCount = dis.readShort();
                for (int i = 0; i < worldCount; i++) {
                    int world = dis.readShort() & 0xFFFF;
                    int flag = dis.readInt();
                    boolean member = (flag & 0x1) != 0;
                    boolean pvp = (flag & 0x4) != 0;
                    boolean highRisk = (flag & 0x400) != 0;

                    String host = null, activity = null;
                    StringBuilder sb = new StringBuilder();
                    byte b;
                    while (true) {
                        b = dis.readByte();
                        if (b == 0) {
                            if (host == null) {
                                host = sb.toString();
                                sb = new StringBuilder();
                            } else {
                                activity = sb.toString();
                                break;
                            }
                        } else {
                            sb.append((char) b);
                        }
                    }

                    int serverLoc = dis.readByte() & 0xFF;
                    int playerCount = dis.readShort();
                    worldList.add(new World(world, flag, member, pvp, highRisk, host, activity, serverLoc, playerCount));
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                General.println("Failed to load runescape worlds");
                return null;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            General.println("Failed to load runescape worlds");
            return null;
        }
        Collections.sort(worldList, new Comparator<World>() {

            @Override
            public int compare(World o1, World o2) {
                // TODO Auto-generated method stub
                return o1.getId() - o2.getId();
            }
        });
        return Collections.unmodifiableList(worldList);
    }
}
