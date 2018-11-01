package scripts.api.interaction.prefabs;

import org.tribot.api2007.types.RSGroundItem;
import scripts.api.interaction.PositionableInteractor;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Spencer on 1/27/2017.
 */
public class GroundItemPrefab extends PositionableInteractor<RSGroundItem, GroundItemPrefab> {

    @Override
    public List<Callable<Boolean>> getChain() {
        return chain;
    }
}
