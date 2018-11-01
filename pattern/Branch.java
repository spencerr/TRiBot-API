package scripts.api.pattern;

import scripts.api.util.Global;
import org.tribot.script.Script;

import java.util.ArrayList;

/**
 * Created by Spencer on 7/29/2017.
 */
public abstract class Branch {

    private ArrayList<Branch> branches = new ArrayList<>();

    public BaseScript getBaseScript() {
        return Global.script;
    }

    public Script getScript() {
        return (Script) Global.script;
    }

    public abstract void execute();

    public abstract boolean valid();
}
