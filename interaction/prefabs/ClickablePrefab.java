package scripts.api.interaction.prefabs;

import org.tribot.api.interfaces.Clickable07;
import scripts.api.interaction.Interactor;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Spencer on 9/16/2017.
 */
public class ClickablePrefab extends Interactor<Clickable07, ClickablePrefab> {

    @Override
    public List<Callable<Boolean>> getChain() {
        return chain;
    }

}
