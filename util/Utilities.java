/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts.api.util;

import org.tribot.api.General;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Login;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;
import scripts.api.login.Login07;
import scripts.api.pattern.BaseScript;
import scripts.api.world.WorldHop;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Spencer
 */
public class Utilities {

    public static BaseScript script;

    public static String formatTime(final long time) {
        final StringBuilder t = new StringBuilder();
        final long total_secs = time / 1000;
        final long total_mins = total_secs / 60;
        final long total_hrs = total_mins / 60;
        final long total_days = total_hrs / 24;
        final int secs = (int) total_secs % 60;
        final int mins = (int) total_mins % 60;
        final int hrs = (int) total_hrs % 24;
        final int days = (int) total_days;
        if (days > 0) {
            if (days < 10)
                t.append("0");

            t.append(days);
            t.append(":");
        }
        if (hrs < 10)
            t.append("0");

        t.append(hrs);
        t.append(":");
        if (mins < 10)
            t.append("0");

        t.append(mins);
        t.append(":");
        if (secs < 10)
            t.append("0");

        t.append(secs);
        return t.toString();
    }

    public static String[] array(String... t) {
        return t;
    }

    public static boolean contains(String[] arr, String val) {
        if (arr == null || val == null) return false;
        for (String str : arr) {
            if (str == null) continue;
            if (str.toLowerCase().contains(val.toLowerCase()) || val.toLowerCase().contains(str.toLowerCase()))
                return true;
        }

        return false;
    }

    public static boolean equals(String[] arr, String val) {
        if (arr == null || val == null) return false;
        for (String str : arr) {
            if (str == null) continue;
            if (str.toLowerCase().equals(val.toLowerCase()) || val.toLowerCase().equals(str.toLowerCase()))
                return true;
        }

        return false;
    }

    public static RSTile getCenterTile(RSArea area) {
        return area.polygon.npoints > 0 ? new RSTile((int) Math.round(avg(area.polygon.xpoints)), (int) Math.round(avg(area.polygon.ypoints)), area.getRandomTile().getPlane()) : null;
    }

    private static double avg(final int... nums) {
        long total = 0;
        for (int i : nums) {
            total += (long) i;
        }
        return (double) total / (double) nums.length;
    }

    public static String encode(String... args) {
        String str = "{";
        for (int i = 0; i < args.length - 1; i += 2) {
            if (i != 0)
                str += ", ";
            str += "\"" + args[i] + "\": \"" + args[i+1] + "\"";
        }
        str += "}";
        return str;
    }

    public static boolean isEnterAmountOpen() {
        return false;
    }

    public static boolean isBanned() {
        if (Login.getLoginState() == Login.STATE.WELCOMESCREEN || Interfaces.isInterfaceValid(50)) {
            Login.login();
            RSInterface rsInterface = InterfaceUtil.get(378, 6);
            if (rsInterface != null) {
                rsInterface.click();
            }
        } else if (Login.getLoginState() != Login.STATE.INGAME) {
            if (WorldHop.getWorld() != (Global.bag.get("world", 301) - 300)) {
                script.setStatus("SWITCHING WORLDS");
                WorldHop.switchWorld(Global.bag.get("world", 301) - 300);
            } else {
                //script.setStatus("ATTEMPTING TO LOG IN: " + LoginBranch.getLoginResponse());
                if (Global.bag.get("passwordFail", 0) > 5) {
                    return true;
                }

                if (Global.bag.get("email", "").equals("")) {
                    Login.login();
                } else {
                    //LoginThread thread = Global.bag.get("loginThread", null);
                    //if (thread != null) {
                    //    script.setStatus("Starting login thread.");
                    //    thread.running = true;
                    //}

                    Login07.LOGIN_MESSAGE result = Login07.login(Global.bag.get("email"), Global.bag.get("password"));
                    if (result != Login07.LOGIN_MESSAGE.SUCCESS) {
                        //if (thread != null) {
                        //    thread.running = false;
                        //}

                        String response = Login.getLoginResponse();
                        if (response.contains("Connection timed out") || response.contains("No reply from loginserver") || response.contains("Error conencting") || response.contains("No response from server") || response.contains("Login limit exceeded")) {
                            //script.setStatus("Login limit exceeded, switching worlds.");
                            //int f2pWord = WorldHop.getRandomF2pWorld() + 300;
                            //Global.bag.addOrUpdate("world", f2pWord);
                            //WorldHop.switchWorld(Global.bag.get("world", 301) - 300);
                            General.sleep(1*60*1000, 2*60*1000);
                        } else if (response.contains("Too many login attempts")) {
                            script.setStatus("Too many login attempts, waiting 3-6 minutes before attempting to login.");
                            General.sleep(3*60*1000, 6*60*1000);
                        } else if (response.contains("locked") || response.contains("disabled")){
                            return true;
                        } else {
                            if (result == Login07.LOGIN_MESSAGE.INVALID) {
                                Global.bag.addOrUpdate("passwordFail", Global.bag.get("passwordFail", 0) + 1);
                            }
                            script.setStatus("Password Login failed: " + response + ". Failed attempts: " + Global.bag.get("passwordFail", 0));
                            General.sleep(15000, 25000);
                            //Global.bag.addOrUpdate("passwordFail", Global.bag.get("passwordFail", 0) + 1);
                        }
                    } else {
                        Global.bag.addOrUpdate("passwordFail", 0);
                    }
                }
            }
        }

        if (Login.getLoginResponse().contains("Your account has been dis") || Login.getLoginResponse().contains("Account locked as")) {
            return true;
        }

        return false;
    }

    public static int getWildernessLevel() {
        RSInterface rsInterface = InterfaceUtil.get(90, 33);
        if (rsInterface == null) return 0;

        String text = rsInterface.getText();
        if (text == null) return 0;

        try {
            return Integer.parseInt(text.replace("Level: ", ""));
        } catch(Exception e) {
            return 0;
        }
    }

    public static void updatePosition(BaseScript script) {
        RSTile lastPosition = Global.bag.get("lastPosition", null);
        if (lastPosition != null) {
            if (lastPosition.distanceTo(Player.getPosition()) > 10) {
                Global.bag.addOrUpdate("lastPosition", Player.getPosition());
                lastPosition = Global.bag.get("lastPosition");
                script.update("{\"type\": \"CLIENT_UPDATE\", \"clientdata\": {\"clientid\": \"" + Global.bag.get("clientid", "") + "\", \"location\": \"" + lastPosition.getX() + "," + lastPosition.getY() + "," + lastPosition.getPlane() + "\"}}");
            }
        } else {
            Global.bag.addOrUpdate("lastPosition", Player.getPosition());
        }
    }

    public static ArrayList toArrayList(Object... arr) {
        return new ArrayList(Arrays.asList(arr));

    }
}
