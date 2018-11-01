package scripts.api.interaction.prefabs;

import org.tribot.api2007.types.RSNPC;
import scripts.api.interaction.PositionableInteractor;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Spencer on 1/27/2017.
 */
public class ObjectPrefab extends PositionableInteractor<RSNPC, ObjectPrefab> {

    @Override
    public List<Callable<Boolean>> getChain() {
        return chain;
    }
}
