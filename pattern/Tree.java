package scripts.api.pattern;

import org.tribot.script.Script;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Spencer on 7/29/2017.
 */
public abstract class Tree extends Script {

    protected void onStart() {
        this.setLoginBotState(false);
        this.setAIAntibanState(false);
        Thread.getAllStackTraces().keySet().stream().filter(thread -> thread.getName().contains("Antiban") || thread.getName().contains("Fatigue")).forEach(Thread::suspend);
    }

    protected ArrayList<Branch> branches = new ArrayList<>();

    public void addBranch(Branch... branches) {
        Collections.addAll(this.branches, branches);
    }
}
