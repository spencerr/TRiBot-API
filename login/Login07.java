package scripts.api.login;

import org.tribot.api2007.Banking;
import scripts.api.util.Global;
import scripts.api.util.GrandExchange;
import scripts.api.webwalker.shared.helpers.BankHelper;
import scripts.api.world.WorldHop;
import org.tribot.api.General;
import org.tribot.api.Screen;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.colour.ColourPoint;
import org.tribot.api.types.colour.Tolerance;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Game;
import org.tribot.api2007.Login;

import java.awt.*;

/**
 * Created by Spencer on 4/21/2017.
 */
public class Login07 {

    public enum LOGIN_MESSAGE {
        ENTER,
        INVALID,
        CONNECTING,
        ALREADY_LOGGED_IN,
        ERROR_CONNECTING,
        WORLD_FULL,
        NOT_MEMBER,
        UPDATED,
        UPDATE_IN_PROGRESS,
        PVP_WORLD,
        NEW_USER,
        TIMED_OUT,
        LOGIN_LIMIT_EXCEEDED,
        NO_REPLY,
        BANNED,
        MEM_WORLD,
        SERVER_OFFLINE,
        TOO_MANY_ATTEMPTS,
        STANDING_IN_MEMBERS,
        INVALID_SKILL_TOTAL,
        LOCKED,
        TRUE,
        FALSE,
        SUCCESS,
        FAIL
    }

    public static LOGIN_MESSAGE login() {
        return login(Global.bag.get("email", ""), Global.bag.get("password", ""));
    }

    public static LOGIN_MESSAGE login(String username, String password) {
        long a = Timing.currentTimeMillis();
        int[] a2 = new int[1];
        boolean[] a3 = new boolean[1];
        LOGIN_MESSAGE result = LOGIN_MESSAGE.FAIL;
        while (Timing.currentTimeMillis() - a < 600000 && a2[0] < 5) {
            General.sleep_definite((long)200);
            Login.STATE loginState = Login.getLoginState();
            switch (un()[loginState.ordinal()]) {
                case 3: {
                    return LOGIN_MESSAGE.SUCCESS;
                }
                case 2: {
                    handleWelcomeScreen();
                    break;
                }
                case 1: {
                    result = attemptLogin(username, password, a2, a3);
                }
            }
        }

        if (a2[0] >= 5) {
            return LOGIN_MESSAGE.INVALID;
        }

        if (Game.getGameState() != 30 || Login.getLoginState() != Login.STATE.INGAME) {
            if (result == LOGIN_MESSAGE.INVALID)
                return LOGIN_MESSAGE.INVALID;
            else
                return LOGIN_MESSAGE.FAIL;
        }
        return LOGIN_MESSAGE.SUCCESS;
    }

    static int[] a;
    private static int[] un() {
        if (a != null) {
            return a;
        }

        a = new int[Login.STATE.values().length];
        try {
            a[Login.STATE.INGAME.ordinal()] = 3;
        } catch (java.lang.NoSuchFieldError v2) {

        } finally {
            try {
                a[Login.STATE.LOGINSCREEN.ordinal()] = 1;
            } catch (java.lang.NoSuchFieldError v4) {

            } finally {
                try {
                    a[Login.STATE.UNKNOWN.ordinal()] = 4;
                } catch (java.lang.NoSuchFieldError v6) {

                } finally {
                    try {
                        a[Login.STATE.WELCOMESCREEN.ordinal()] = 2;
                    } catch (java.lang.NoSuchFieldError v8) {

                    }
                }
            }
        }

        return a;
    }

    private static boolean handleWelcomeScreen() {
        org.tribot.api2007.types.RSInterfaceChild rSInterfaceChild = org.tribot.api2007.Interfaces.get(378, 6);
        if (rSInterfaceChild == null || !rSInterfaceChild.isBeingDrawn()) return false;
        return rSInterfaceChild.click() && Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep(30, 50);
                if (Game.getGameState() != 30) return false;
                return Login.getLoginState() == Login.STATE.INGAME;
            }
        }, (long) 6000);
    }

    private static LOGIN_MESSAGE attemptLogin(String a, String a1, int[] a2, boolean[] a3) {
        if(org.tribot.api2007.Screen.getColorAt(392, 284).equals(new Color(11, 11, 11))) {
            Mouse.clickBox(395, 275, 529, 307, 1);
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    if (!org.tribot.api2007.Screen.getColorAt((int)392, (int)284).equals(new java.awt.Color(11, 11, 11))) return true;
                    return false;
                }
            }, 4000L);
            General.sleep(500, 700);
        }

        if(Screen.getColourAt(new Point(437,290)).equals(new Color(255,255,255))){
            Keyboard.pressEnter();
        }

        if (hasInvalidPassword()) {
            String var10 = Login.getLoginResponse();
            Mouse.clickBox(315, 260, 449, 293, 1);
            if(Timing.waitCondition(new Login$3(var10), (long)General.random(2000, 3000))) {
                ++a2[0];
            }

            General.sleep(500, 700);
            return LOGIN_MESSAGE.INVALID;
        } else {
            //mg var4 = mg.tF();
            LOGIN_MESSAGE var5;
            if((var5 = getLoginMessage()) != null) {
                int var10000;
                switch(loginMessageValues()[var5.ordinal()]) {
                    case 3:
                        a3[0] = false;
                        return LOGIN_MESSAGE.FALSE;
                    case 7:
                        return LOGIN_MESSAGE.FALSE;
                    case 9:
                        General.println("A RuneScape update is in progress. Waiting 4-5 minutes.");
                        General.sleep((long)(General.random(4, 5) * '\uea60'));
                    case 8:
                        General.sleep(5000, 8000);
                        General.println("RuneScape has updated. Restarting client in 5-6 minutes.");
                        return LOGIN_MESSAGE.FALSE;
                    case 10:
                        Mouse.clickBox(234, 304, 372, 338, 1);
                        General.sleep(600, 720);
                        break;
                    case 11:
                        Mouse.clickBox(317, 308, 447, 335, 1);
                        General.sleep(600, 720);
                        break;
                    case 15:
                        a2[0] = 100;
                        return LOGIN_MESSAGE.FALSE;
                    case 16:
                    case 17:
                        boolean var6 = var5 != LOGIN_MESSAGE.MEM_WORLD;

                        int var7;
                        for(var10000 = var7 = 0; var10000 < 10; var10000 = var7) {
                            int var8 = WorldHop.getRandomF2pWorld();
                            WorldHop.switchWorld(var8);
                            if(Timing.waitCondition(new Login$6(var8), (long)General.random(1800, 2486))) {
                                break;
                            }

                            ++var7;
                        }

                        Mouse.clickBox(236, 306, 366, 335, 1);
                        if(Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                if (dA() != LOGIN_MESSAGE.CONNECTING) return false;
                                return true;
                            }
                        }, 4000L)) {
                        Timing.waitCondition(new Condition() {
                            @Override
                            public boolean active() {
                                if (dA() == LOGIN_MESSAGE.CONNECTING) return false;
                                return true;
                            }
                        }, 10000L);

                        if(getLoginMessage() == LOGIN_MESSAGE.INVALID) {
                            ++a2[0];
                        }

                        if (Login.getLoginState() == Login.STATE.WELCOMESCREEN) {
                            return LOGIN_MESSAGE.TRUE;
                        }
                    }
                    break;
                    case 18:
                        if(!a3[0]) {
                            General.sleep(4000, 5000);
                        }

                        a3[0] = true;
                    case 4:
                    case 5:
                    case 6:
                    case 12:
                    case 13:
                    case 14:
                        if(!a3[0]) {
                            General.sleep(30000, 60000);
                        }

                        a3[0] = true;
                    case 1:
                    case 2:
                        if(var5 == LOGIN_MESSAGE.INVALID || var5 == LOGIN_MESSAGE.ENTER) {
                            a3[0] = false;
                        }

                        ColourPoint[] var12 = Screen.findColours(new Color(255, 255, 255), 311, 253 - 12, 560, 267 - 12, new Tolerance(0));
                        ColourPoint[] var13 = Screen.findColours(new Color(255, 255, 255), 343, 273 - 12, 560, 278 - 12, new Tolerance(0));
                        if(var12.length > 0) {
                            Mouse.clickBox(330, 256 - 12, 490, 260 - 12, 1);
                            General.sleep((long)General.randomSD(0, 1000, 400, 50));
                            if(!ao(new Color(255, 255, 255), new Tolerance(0), 311, 253 - 12, 560, 267 - 12, 5000)) {
                                return LOGIN_MESSAGE.FALSE;
                            }

                            General.sleep((long)General.randomSD(0, 1000, 200, 23));
                        }

                        if(var13.length > 0) {
                            Mouse.clickBox(375, 262 - 12, 490, 275 - 12, 1);
                            General.sleep((long)General.randomSD(0, 1000, 400, 50));
                            if(!ao(new Color(255, 255, 255), new Tolerance(0), 343, 273 - 12, 560, 278 - 12, 5000)) {
                                return LOGIN_MESSAGE.FALSE;
                            }

                            General.sleep((long)General.randomSD(0, 1000, 200, 23));
                        }

                        int var9;
                        for(var10000 = var9 = 0; var10000 < 5; var10000 = var9) {
                            General.sleep(50, 55);
                            if(Login.getLoginState() != Login.STATE.LOGINSCREEN) {
                                return LOGIN_MESSAGE.FALSE;
                            }

                            ++var9;
                        }

                        Mouse.clickBox(330, 256 - 12, 490, 260 - 12, 1);
                        var10000 = 0;
                        General.sleep(300, 750);

                        for(var9 = 0; var10000 < 5; var10000 = var9) {
                            General.sleep(50, 55);
                            if(Login.getLoginState() != Login.STATE.LOGINSCREEN) {
                                return LOGIN_MESSAGE.FALSE;
                            }

                            ++var9;
                        }

                        Keyboard.typeString(a);
                        if(Login.getLoginState() != Login.STATE.LOGINSCREEN) {
                            return LOGIN_MESSAGE.FALSE;
                        }

                        Keyboard.pressEnter();
                        General.sleep(300, 750);
                        if(Login.getLoginState() != Login.STATE.LOGINSCREEN) {
                            return LOGIN_MESSAGE.FALSE;
                        }

                        if(org.tribot.api2007.Screen.getColorAt(66, 225).equals(new Color(0, 0, 0))) {
                            return LOGIN_MESSAGE.FALSE;
                        }

                        Keyboard.typeString(a1);
                        if(Login.getLoginState() != Login.STATE.LOGINSCREEN) {
                            return LOGIN_MESSAGE.FALSE;
                        }

                        Keyboard.pressEnter();
                        if(In(a2) == LOGIN_MESSAGE.TRUE) {
                            return LOGIN_MESSAGE.FALSE;
                        }
                        break;
                    case 19:
                        General.println("We are currently standing in a members-only area. Attempting to switch to a members world.");
                        WorldHop.switchWorld(WorldHop.getRandomMembersWorld());
                        General.sleep(300, 600);
                        return Mn();
                    case 20:
                        General.println("We have a skill total less than what is required for this world. Switching worlds...");
                        WorldHop.switchWorld(WorldHop.getRandomMembersWorld());
                        General.sleep(300, 600);
                        return Mn();
                    case 21:
                        a2[0] = 100;
                        return LOGIN_MESSAGE.FALSE;
                }
            }

            return LOGIN_MESSAGE.FALSE;
        }
    }

    private static LOGIN_MESSAGE Mn() {
        Mouse.clickBox((int)235, (int)305, (int)369, (int)337, (int)1);
        return In(new int[1]);
    }

    public static boolean in() {
        return hasInvalidPassword();
    }

    public static LOGIN_MESSAGE dA() {
        return getLoginMessage();
    }

    private static LOGIN_MESSAGE In(int[] a) {
        if (!Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                if (dA() != LOGIN_MESSAGE.CONNECTING) return false;
                return true;
            }
        }, (long) 4000)) return LOGIN_MESSAGE.FALSE;
        Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                General.sleep((int)80, (int)120);
                if (dA() != LOGIN_MESSAGE.CONNECTING) return true;
                if (in()) return true;
                return false;
            }
        }, (long) 10000);
        if (getLoginMessage() == LOGIN_MESSAGE.INVALID) {
            int[] arrn = a;
            arrn[0] = arrn[0] + 1;
        }
        if (Login.getLoginState() != Login.STATE.WELCOMESCREEN) return LOGIN_MESSAGE.FALSE;
        return LOGIN_MESSAGE.TRUE;
    }

    private static boolean ao(java.awt.Color a, Tolerance a1, int a2, int a3, int a4, int a5, int a6) {
        long var7 = Timing.currentTimeMillis();
        Keyboard.holdKey('\b', (int) '\b', new Login$9(a, a2, a3, a4, a5, a1, var7, a6));
        if (Timing.timeFromMark(var7) >= (long)a6) return false;
        return true;
    }

    private static boolean hasInvalidPassword() {
        return Screen.findColours(new java.awt.Color(255, 255, 0), 212, 204, 558, 243, new Tolerance(5)).length == 1166;
    }

    private static LOGIN_MESSAGE getLoginMessage() {
        if(Login.getLoginState() != Login.STATE.LOGINSCREEN) {
            return null;
        } else {
            String var10000;
            String var0;
            label184: {
                String var1;
                switch((var1 = var0 = General.stripFormatting(Login.getLoginResponse())).hashCode()) {
                    case -2042274460:
                        if(var1.equals("Connection timed out. Please try using a different world.")) {
                            return LOGIN_MESSAGE.SERVER_OFFLINE;
                        }

                        var10000 = var0;
                        break label184;
                    case -1969387968:
                        if(var1.equals("The server is being updated. Please wait 1 minute and try again.")) {
                            return LOGIN_MESSAGE.UPDATE_IN_PROGRESS;
                        }

                        var10000 = var0;
                        break label184;
                    case -1794717686:
                        if(var1.equals("Invalid username/email or password.")) {
                            return LOGIN_MESSAGE.INVALID;
                        }

                        var10000 = var0;
                        break label184;
                    case -1739595701:
                        if(var1.equals("Connection timed out.")) {
                            return LOGIN_MESSAGE.TIMED_OUT;
                        }

                        var10000 = var0;
                        break label184;
                    case -1607383443:
                        if(var1.equals("Unable to connect. Login server offline.")) {
                            return LOGIN_MESSAGE.SERVER_OFFLINE;
                        }

                        var10000 = var0;
                        break label184;
                    case -1562554836:
                        if(var1.equals("You need a members account to login to this world. Please try using a different world.")) {
                            return LOGIN_MESSAGE.NOT_MEMBER;
                        }

                        var10000 = var0;
                        break label184;
                    case -1377462022:
                        if(var1.equals("You are standing in a members-only area. To play on this world move to a free area first")) {
                            return LOGIN_MESSAGE.STANDING_IN_MEMBERS;
                        }

                        var10000 = var0;
                        break label184;
                    case -1063255762:
                        if(var1.equals("Connecting to server...")) {
                            return LOGIN_MESSAGE.CONNECTING;
                        }

                        var10000 = var0;
                        break label184;
                    case -978842397:
                        if(var1.equals("You need a members account to login to this world. Please subscribe, or use a different world.")) {
                            return LOGIN_MESSAGE.MEM_WORLD;
                        }

                        var10000 = var0;
                        break label184;
                    case -884600119:
                        if(!var1.equals("Your account is already logged in.")) {
                            var10000 = var0;
                            break label184;
                        }

                        return LOGIN_MESSAGE.ALREADY_LOGGED_IN;
                    case -657177561:
                        if(var1.equals("No reply from loginserver. Please wait 1 minute and try again.")) {
                            return LOGIN_MESSAGE.NO_REPLY;
                        }

                        var10000 = var0;
                        break label184;
                    case -36220208:
                        if(var1.equals("Login limit exceeded. Too many connections from your address.")) {
                            return LOGIN_MESSAGE.LOGIN_LIMIT_EXCEEDED;
                        }

                        var10000 = var0;
                        break label184;
                    case 128933960:
                        if(!var1.equals("Please enter your username/email address.")) {
                            var10000 = var0;
                            break label184;
                        }

                        return LOGIN_MESSAGE.ENTER;
                    case 262698144:
                        if(var1.equals("RuneScape has been updated!")) {
                            return LOGIN_MESSAGE.UPDATED;
                        }

                        var10000 = var0;
                        break label184;
                    case 526652327:
                        if(var1.equals("Too many login attempts. Please wait a few minutes before trying again.")) {
                            return LOGIN_MESSAGE.TOO_MANY_ATTEMPTS;
                        }

                        var10000 = var0;
                        break label184;
                    case 594916394:
                        if(!var1.equals("This world is full. Please use a different world.")) {
                            var10000 = var0;
                            break label184;
                        }
                        break;
                    case 657136709:
                        if(!var1.equals("Your account is already logged in. Try again in 60 secs...")) {
                            var10000 = var0;
                            break label184;
                        }

                        return LOGIN_MESSAGE.ALREADY_LOGGED_IN;
                    case 728860833:
                        if(!var1.equals("Enter your username/email & password.")) {
                            var10000 = var0;
                            break label184;
                        }

                        return LOGIN_MESSAGE.ENTER;
                    case 868279161:
                        if(!var1.equals("This world is full.")) {
                            var10000 = var0;
                            break label184;
                        }
                        break;
                    case 940000314:
                        if(var1.equals("Account locked as we suspect it has been stolen. Press \'recover a locked account\' on front page.")) {
                            return LOGIN_MESSAGE.LOCKED;
                        }

                        var10000 = var0;
                        break label184;
                    case 1192612775:
                        if(var1.equals("Your account has been disabled. Please check your message-centre for details.")) {
                            return LOGIN_MESSAGE.BANNED;
                        }

                        var10000 = var0;
                        break label184;
                    case 1409359726:
                        if(var1.equals("No response from server. Please try using a different world.")) {
                            return LOGIN_MESSAGE.NO_REPLY;
                        }

                        var10000 = var0;
                        break label184;
                    case 1868564182:
                        if(var1.equals("Error connecting to server.")) {
                            return LOGIN_MESSAGE.ERROR_CONNECTING;
                        }

                        var10000 = var0;
                        break label184;
                    case 2005214542:
                        if(var1.equals("RuneScape has been updated! Please reload this page.")) {
                            return LOGIN_MESSAGE.UPDATED;
                        }

                        var10000 = var0;
                        break label184;
                    default:
                        var10000 = var0;
                        break label184;
                }

                return LOGIN_MESSAGE.WORLD_FULL;
            }

            if(!var10000.contains("PvP world") && !var0.contains("High Risk")) {
                if(var0.contains("You need a skill total")) {
                    return LOGIN_MESSAGE.INVALID_SKILL_TOTAL;
                } else {
                    return null;
                }
            } else {
                return LOGIN_MESSAGE.PVP_WORLD;
            }
        }
    }

    public static int[] loginMessageValues() {
        int[] values = new int[LOGIN_MESSAGE.values().length];
        for (int i = 0; i < LOGIN_MESSAGE.values().length; i++) {
            values[LOGIN_MESSAGE.values()[i].ordinal()] = LOGIN_MESSAGE.values()[i].ordinal() + 1;
        }

        return values;
    }

    public static boolean logout() {
        if (Banking.isBankScreenOpen() || Banking.isDepositBoxOpen()) {
            BankHelper.close();
        }

        if (GrandExchange.isExchangeOpen()) {
            GrandExchange.close();
        }

        return Login.logout();
    }
}
