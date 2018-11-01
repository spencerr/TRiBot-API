package scripts.api.interaction;

import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api2007.Player;
import scripts.api.pattern.BooleanLambda;
import scripts.api.util.Movement;
import scripts.api.webwalker.local.walker_engine.interaction_handling.InteractionHelper;

/**
 * Created by Spencer on 1/27/2017.
 */
public abstract class PositionableInteractor<T extends Positionable & Clickable07, S> extends Interactor<T, S> {

    public S turnTo() {
        chain.add(() -> InteractionHelper.focusCamera(clickable07));
        return (S) this;
    }

    public S walkTo() {
        chain.add(() -> Movement.walkTo((Positionable) clickable07, () -> Player.getPosition().distanceTo((Positionable) clickable07) < 3 && Movement.canReach((Positionable) clickable07)));
        return (S) this;
    }

    public S walkTo(BooleanLambda lambda) {
        chain.add(() -> Movement.walkTo((Positionable) clickable07, () -> lambda.active() || (Player.getPosition().distanceTo((Positionable) clickable07) < 3 && Movement.canReach((Positionable) clickable07))));

        return (S) this;
    }
}
