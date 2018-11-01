package scripts.api.login;

/**
 * Created by Spencer on 4/21/2017.
 */
public class Login$6
        extends org.tribot.api.types.generic.Condition
        {
private final /* synthetic */ int F;

public Login$6(int n) {
        F = n;
        }

public boolean active() {
        org.tribot.api.General.sleep((int)200, (int)350);
        if (org.tribot.api2007.WorldHopper.getWorld() != this.F) return false;
        return true;
        }
        }
