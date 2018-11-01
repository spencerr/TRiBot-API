package scripts.api.interaction.prefabs;

import org.tribot.api2007.types.RSInterface;
import scripts.api.interaction.Interactor;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Spencer on 1/27/2017.
 */
public class InterfacePrefab extends Interactor<RSInterface, InterfacePrefab> {

    @Override
    public List<Callable<Boolean>> getChain() {
        return chain;
    }
}
