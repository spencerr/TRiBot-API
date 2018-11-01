package scripts.api.interaction.prefabs;

import org.tribot.api2007.types.RSPlayer;
import scripts.api.interaction.PositionableInteractor;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Spencer on 1/27/2017.
 */
public class PlayerPrefab extends PositionableInteractor<RSPlayer, PlayerPrefab> {

    @Override
    public List<Callable<Boolean>> getChain() {
        return chain;
    }
}
