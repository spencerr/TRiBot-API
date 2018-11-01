package scripts.api.interaction.prefabs;

import org.tribot.api2007.types.RSItem;
import scripts.api.interaction.Interactor;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Spencer on 1/27/2017.
 */
public class RSItemPrefab extends Interactor<RSItem, RSItemPrefab> {

    @Override
    public List<Callable<Boolean>> getChain() {
        return chain;
    }
}
