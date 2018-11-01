package scripts.api.util;

import scripts.api.pattern.BooleanLambda;
import org.tribot.api.General;
import org.tribot.api.types.generic.Condition;

/**
 * Created by Spencer on 7/29/2017.
 */
public class Timing {

    private static final long DEFAULT_TIME = 50;

    public static boolean waitCondition(long length, BooleanLambda lambda) {
       return waitCondition(length, DEFAULT_TIME, lambda);
    }

    public static boolean waitCondition(long length, long time, BooleanLambda lambda) {
        long start = currentTimeMillis();

        while (currentTimeMillis() - start < length) {
            if (lambda.active()) return true;
            General.sleep(time);
        }

        return lambda.active();
    }

    public static long currentTimeMillis() {
        return org.tribot.api.Timing.currentTimeMillis();
    }

    public static boolean waitCondition(long length, Condition condition) {
        return waitCondition(length, condition::active);
    }

    public static boolean waitCondition(long length, long time, Condition condition) {
        return waitCondition(length, time, condition::active);
    }
}
