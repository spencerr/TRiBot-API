package scripts.api.breaking;

import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Login;
import scripts.api.util.Timing;
import scripts.api.webwalker.shared.Pair;
import scripts.api.webwalker.shared.helpers.BankHelper;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Spencer on 10/24/2017.
 */
public class BreakManager {

    public static ArrayList<Account> accounts = new ArrayList<>();

    public static Account getActiveAccount() {
        for (Account acc : accounts)
            if (acc.active) return acc;

        Calendar now = Scheduler.getNow(), today = Scheduler.getToday();

        boolean generateSchedule = true;
        for (Account acc : accounts) {
            if (acc.schedule.containsKey(today)) {
                generateSchedule = false;
            }
        }

        if (generateSchedule) {
            Scheduler.generateSchedule(today);
            return getActiveAccount();
        }


        for (Account acc : accounts) {
            if (!acc.schedule.containsKey(today)) continue;

            for (Pair<Long, Long> pair : acc.schedule.get(today).playTimes) {
                if (Scheduler.inside(now, pair.getKey(), pair.getValue())) {
                    acc.active = true;
                    return acc;
                }
            }
        }

        return null;
    }

    public static Account getNextActiveAccount() {
        Calendar now = Scheduler.getNow(), today = Scheduler.getToday();
        long nowTime = Scheduler.toDayTime(now);

        Account next = null;
        long closest = -1;
        for (Account acc : accounts) {
            for (Pair<Long, Long> pair : acc.schedule.get(today).playTimes) {
                if (pair.getKey() > nowTime && (closest == -1 || pair.getKey() < closest)) {
                    next = acc;
                    closest = pair.getKey();
                }
            }
        }

        return next;
    }

    public static boolean isBreaking() {
        Account acc = getActiveAccount();
        return acc == null;
    }

    public static boolean shouldBreak() {

        Account acc = getActiveAccount();

        if (acc == null || !acc.active) return false;

        Calendar now = Scheduler.getNow(), today = Scheduler.getToday();
        if (!acc.schedule.containsKey(today)) return false;

        for (Pair<Long, Long> pair : acc.schedule.get(today).playTimes) {
            if (Scheduler.inside(now, pair.getKey(), pair.getValue()))
                return false;
        }

        return true;

    }

    public static void switchAccounts() {
        while (Login.getLoginState() != Login.STATE.LOGINSCREEN) {
            Login.logout();
            Timing.waitCondition(General.random(1250, 3500), () -> Login.getLoginState() == Login.STATE.LOGINSCREEN);
        }

        for (Account acc : accounts) {
            acc.active = false;
        }
    }

    public static long getNextPlayTime() {
        Calendar now = Scheduler.getNow(), today = Scheduler.getToday();
        long nowTime = Scheduler.toDayTime(now);

        long nextPlayTime = -1;
        for (Account acc : accounts) {
            if (!acc.schedule.containsKey(today)) continue;
            for (Pair<Long, Long> pair : acc.schedule.get(today).playTimes) {
                if (pair.getKey() >= nowTime && (nextPlayTime == -1 || pair.getKey() <= nextPlayTime)) {
                    nextPlayTime = pair.getKey();
                }
            }
        }

        return nextPlayTime;
    }

    public static long getNextBreakTime() {
        Calendar now = Scheduler.getNow(), today = Scheduler.getToday();
        long nowTime = Scheduler.toDayTime(now);

        long nextBreakTime = -1;
        for (Account acc : accounts) {
            if (!acc.schedule.containsKey(today)) continue;
            if (acc.active) {
                for (Pair<Long, Long> pair : acc.schedule.get(today).playTimes) {
                    if (pair.getValue() >= nowTime && (nextBreakTime == -1 || pair.getValue() <= nextBreakTime)) {
                        nextBreakTime = pair.getValue();
                    }
                }
            }
        }

        return nextBreakTime;
    }

    public static void startBreak() {
        if (Banking.isBankScreenOpen() || Banking.isDepositBoxOpen()) {
            BankHelper.close();
            Timing.waitCondition(General.random(1250, 1750), () -> !Banking.isBankScreenOpen() && !Banking.isDepositBoxOpen());
        }

        while (Login.getLoginState() != Login.STATE.LOGINSCREEN) {
            Login.logout();
        }

        for (Account acc : accounts) acc.active = false;
    }

}
