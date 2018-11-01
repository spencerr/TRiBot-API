package scripts.api.login;

/**
 * Created by Spencer on 4/21/2017.
 */
public class Login$3 extends org.tribot.api.types.generic.Condition {
    private final java.lang.String F;

    public boolean active() {
        org.tribot.api.General.sleep((int)100, (int)200);
        if (!org.tribot.api2007.Login.getLoginResponse().equals(this.F)) return true;
        return false;
    }

    public Login$3(java.lang.String string) {
        F = string;
    }
}
