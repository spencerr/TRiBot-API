package scripts.api.util;

import scripts.api.breaking.Account;
import scripts.api.breaking.BreakManager;
import scripts.api.data.Bag;
import scripts.api.pattern.BaseScript;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Spencer on 8/12/2017.
 */
public class Global {

    public static BaseScript script;
    public static Bag bag = new Bag();
    public static ACamera camera = new ACamera();

    public static Bag getBag() {
        return bag;
    }

    public static Bag getAccountBag() {
        Account account = Global.bag.get("email", "").equals("") ? BreakManager.getActiveAccount() : null;
        return account == null ? bag : account.bag;
    }

    public static int srand(int high) {
        Account account = Global.bag.get("email", "").equals("") ? BreakManager.getActiveAccount() : null;

        if (account != null) {
            Random rand = new Random(seed(account.username + getStackSeed()));
            return rand.nextInt(high);
        }

        Random rand = new Random(seed(getStackSeed()));
        return rand.nextInt(high);
    }

    public static long getFullSeed() {
        return getFullSeed(bag.get("email", ""));
    }

    public static long getFullSeed(String account) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return getFullSeed(account, calendar);
    }


    public static long getFullSeed(String account, Calendar calendar) {
        return seed(account + calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static String getStackSeed() {
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        return elements[3].getMethodName();
    }

    public static long seed(String seed) {
        if (seed == null) {
            return seed("r4nD0mS3eD");
        }

        return seed.hashCode();

        /*long hash = 0;
        for (char c : seed.toCharArray()) {
            hash = 31L * hash + c;
        }
        return Math.abs(hash);*/
    }

}
