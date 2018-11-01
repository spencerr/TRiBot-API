package scripts.api.login;

import org.tribot.api.types.colour.Tolerance;

/**
 * Created by Spencer on 4/21/2017.
 */
public class Login$9 extends org.tribot.api.types.generic.Condition {
    private final int M;
    private final int e;
    private final Tolerance i;
    private final long D;
    private long f;
    private final java.awt.Color c;
    private final int J;
    private final int a;
    private final int F;

    public Login$9(java.awt.Color color, int n, int n2, int n3, int n4, Tolerance tolerance, long l, int n5) {
        c = color;
        a = n;
        J = n2;
        e = n3;
        F = n4;
        i = tolerance;
        D = l;
        M = n5;
        f = 0;
        }

    public boolean active() {
        if (org.tribot.api.Timing.timeFromMark(this.D) > (long)this.M) return true;
        return this.fn();
    }

    private boolean fn() {
        if (f > 0) {
            return org.tribot.api.Timing.currentTimeMillis() >= f;
        }

        if (org.tribot.api.Screen.findColour(c, a, J, e, F, i) != null) return false;
        org.tribot.api.Timing.waitNextFrame((long)25, (long)4000);
        if (org.tribot.api.Screen.findColour(c, a, J, e, F, i) != null) return false;
        f = org.tribot.api.Timing.currentTimeMillis() + (long)org.tribot.api.General.random(250, 350);
        return false;
    }
}

