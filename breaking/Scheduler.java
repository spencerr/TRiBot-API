package scripts.api.breaking;

import scripts.api.util.Global;
import scripts.api.webwalker.shared.Pair;

import java.util.*;

/**
 * Created by Spencer on 10/26/2017.
 */
public class Scheduler {
    private static final int SECONDS_PER_MINUTE = 60, MINUTES_PER_HOUR = 60, HOURS_PER_DAY = 24;
    private static final int SECOND = 1000, MINUTE = SECONDS_PER_MINUTE * SECOND, HOUR = MINUTES_PER_HOUR * MINUTE, DAY = HOURS_PER_DAY * HOUR;

    private static final int MIN_PLAY_TIME = 5 * HOUR, MAX_ADDITIONAL_TIME = 3 * HOUR;
    private static final int MIN_SESSION_TIME = HOUR / 2, MAX_ADDITIONAL_SESSION_TIME = HOUR;

    public static String timeToString(Long time) {
        if (time == -1) return "Tomorrow";
        int hour = (int) Math.floor(time / HOUR);
        int minute = (int) Math.floor((time - (hour * HOUR)) / MINUTE);
        int second = (int) Math.floor((time - (hour * HOUR) - (minute * MINUTE)) / SECOND);
        return hour + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second);
    }

    public static boolean inside(Calendar cal, long start, long end) {
        long dayTime = toDayTime(cal);
        return start < dayTime && end > dayTime;
    }

    public static boolean timeFilled(ArrayList<Account> accounts, long time, long endTime, Calendar date) {
        for (Account account : accounts) {
            if (!account.schedule.containsKey(date)) continue;
            for (Pair<Long, Long> t : account.schedule.get(date).playTimes) {
                if ((time - t.getKey() > 0 && time - t.getValue() < 0))
                    return true;
                if ((endTime - t.getKey() > 0 && endTime - t.getValue() < 0))
                    return true;
                if (time - t.getKey() < 0 && endTime - t.getValue() > 0)
                    return true;
            }
        }

        return false;
    }

    public static boolean nearby(Schedule schedule, long time) {
        for (Pair<Long, Long> t : schedule.playTimes) {
            if (Math.abs(time - t.getKey()) <= 0.5 * HOUR || Math.abs(time - t.getValue()) <= 0.5 * HOUR)
                return true;
        }

        return false;
    }

    public static void generateSchedule(Calendar date) {
        if (Global.getBag().get("debug", false)) {
            for (Account account : BreakManager.accounts) {
                account.schedule.put(date, new Schedule(date, DAY));
                account.schedule.get(date).playTimes.add(new Pair<>(0L, (long) DAY));
            }

            return;
        }

        for (Account account : BreakManager.accounts) {
            long seed = Global.getFullSeed(account.username, date);
            Random rand = new Random(seed);

            int gens = 0;
            long playTime = MIN_PLAY_TIME + rand.nextInt(MAX_ADDITIONAL_TIME);

            account.schedule.put(date, new Schedule(date, playTime));

            while (getTotalPlayTime(account.schedule.get(date)) < playTime && gens < 500) {
                long time = nextLong(rand, DAY - (MIN_SESSION_TIME + MAX_ADDITIONAL_SESSION_TIME));
                long endTime = time + MIN_SESSION_TIME + nextLong(rand, MAX_ADDITIONAL_SESSION_TIME);

                if (!timeFilled(BreakManager.accounts, time, endTime, date) && !nearby(account.schedule.get(date), time) && !nearby(account.schedule.get(date), endTime)) {
                    account.schedule.get(date).playTimes.add(new Pair<>(time, endTime));
                }

                gens++;
            }

            account.schedule.keySet().forEach(calendar -> Collections.sort(account.schedule.get(calendar).playTimes, (o1, o2) -> (int) (o1.getKey() - o2.getKey())));
        }
    }

    public static long getTotalPlayTime(Schedule schedule) {
        long time = 0;
        for (Pair<Long, Long> pair : schedule.playTimes) {
            time += pair.getValue() - pair.getKey();
        }

        return time;
    }

    public static long toDayTime(Calendar cal) {
        return cal.get(Calendar.HOUR_OF_DAY) * HOUR + cal.get(Calendar.MINUTE) * MINUTE + cal.get(Calendar.SECOND) * SECOND;
    }

    private static long nextLong(Random rand, long max) {
        return (long) Math.abs(rand.nextDouble() * max);
    }

    public static Calendar getToday() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));
        return cal;
    }

    public static Calendar getNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        return cal;
    }

    public static Calendar getTomorrow() {
        Calendar cal = getToday();
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal;
    }

}
