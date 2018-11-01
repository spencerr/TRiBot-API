package scripts.api.interaction;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.util.abc.ABCProperties;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.*;
import scripts.api.antiban.Antiban;
import scripts.api.data.Bag;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.ItemEntity;
import scripts.api.interaction.prefabs.RSItemPrefab;
import scripts.api.pattern.BooleanLambda;
import scripts.api.pattern.Condition;
import scripts.api.pattern.Consumable;
import scripts.api.settings.GameTab;
import scripts.api.util.InventoryUtil;
import scripts.api.util.Movement;
import scripts.api.util.Spell;
import scripts.api.walking.PathWalker;
import scripts.api.webwalker.local.walker_engine.interaction_handling.AccurateMouse;
import scripts.api.webwalker.local.walker_engine.interaction_handling.InteractionHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Created by Spencer on 1/27/2017.
 */
public abstract class Interactor<T extends Clickable07, S> extends InteractionResult<T> implements Supplier<S> {

    protected final List<Callable<Boolean>> chain;
    protected Clickable07 clickable07;
    protected long reactionTime;

    protected Bag bag = new Bag();

    protected Interactor() {
        chain = new ArrayList<>();
    }

    /**
     * Apply a lambda as a custom filter. Example usage:
     * {@code .custom((obj) -> obj.isOnScreen()}
     * Which would only returns obj's that are on screen.
     * @param lambda <T> the lambda to execute
     * @return
     */
    @SuppressWarnings("unchecked")
    public S custom(Callable<Boolean> lambda) {
        chain.add(lambda);

        return (S)this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public S get() {
        return (S)this;
    }

    public S set(Clickable07 positionable) {
        this.clickable07 = positionable;
        return (S) this;
    }

    public S hover(Clickable07 toHover, String... actions) {
        chain.add(() -> {
            if (!InteractionHelper.isOnScreenAndClickable(toHover)) {
                if (toHover instanceof RSObject || toHover instanceof RSCharacter || toHover instanceof RSGroundItem) {
                    InteractionHelper.quickFocus(toHover);
                }
            }

            return AccurateMouse.hover(toHover, actions);
        });

        return (S) this;
    }

    public S hover(String... options) {
        chain.add(() -> {
            if (!InteractionHelper.isOnScreenAndClickable(clickable07)) {
                if (clickable07 instanceof RSObject || clickable07 instanceof RSCharacter || clickable07 instanceof RSGroundItem) {
                    InteractionHelper.quickFocus(clickable07);
                }
            }

            return AccurateMouse.hover(clickable07, options);
        });

        return (S) this;
    }

    public S hover() {
        chain.add(() -> {
            if (!InteractionHelper.isOnScreenAndClickable(clickable07)) {
                if (clickable07 instanceof RSObject || clickable07 instanceof RSCharacter || clickable07 instanceof RSGroundItem) {
                    InteractionHelper.quickFocus(clickable07);
                }
            }

            return AccurateMouse.hover(clickable07);
        });

        return (S) this;
    }

    public S then(Callable<Boolean> toDo) {
        chain.add(toDo);
        return (S) this;
    }

    public S then(Consumable toDo) {
        chain.add(() -> {
            toDo.consume();
            return true;
        });
        return (S) this;
    }

    public S useOn(String item) {
        chain.add(() -> {
            if (!InteractionHelper.isOnScreenAndClickable(clickable07)) {
                if (clickable07 instanceof RSObject || clickable07 instanceof RSCharacter || clickable07 instanceof RSGroundItem) {
                    RSTile position = ((Positionable) clickable07).getPosition();

                    InteractionHelper.quickFocus(clickable07);

                    if (!InteractionHelper.isOnScreenAndClickable(clickable07) || Player.getPosition().distanceTo((Positionable) clickable07) > 7) {
                        Movement.walkTo(position);
                    }
                }
            }

            if (GameTab.TABS.INVENTORY.open()) {
                if (!InventoryUtil.isUsing(item)) {
                    RSItem realItem = Entities.find(ItemEntity::new)
                            .nameContains(item)
                            .getFirstResult();

                    Interact.with(RSItemPrefab::new)
                            .set(realItem)
                            .action("Use")
                            .waitFor(() -> InventoryUtil.isUsing(item), 750, 1250)
                            .execute();
                }
            }

            String clickableName = "";
            if (clickable07 instanceof RSObject) clickableName = ((RSObject) clickable07).getDefinition().getName();
            if (clickable07 instanceof RSCharacter) clickableName = ((RSCharacter) clickable07).getName();
            if (clickable07 instanceof RSGroundItem) clickableName = ((RSGroundItem) clickable07).getDefinition().getName();
            if (clickable07 instanceof RSItem) clickableName = ((RSItem) clickable07).getDefinition().getName();

            General.println("Attempting to Use " + item + " -> " + clickableName);

            return InventoryUtil.isUsing(item) && AccurateMouse.click(clickable07, "Use " + item);
        });

        return (S) this;
    }

    public S useSpell(RSTile safeSpot, Spell spell) {
        chain.add(() -> {

            if (!Player.getPosition().equals(safeSpot)) {
                PathWalker.walkToClickable(safeSpot, true);
            }

            if (!InteractionHelper.isOnScreenAndClickable(clickable07)) {
                InteractionHelper.quickFocus(clickable07);
            }

            return InteractionHelper.isOnScreenAndClickable(clickable07) && spell.select() && AccurateMouse.click(clickable07, "Cast " + spell.getName());
        });

        return (S) this;
    }

    public S rangedAction(RSTile safeSpot, String... action) {
        chain.add(() -> {

            if (!Player.getPosition().equals(safeSpot)) {
                PathWalker.walkToClickable(safeSpot, true);
            }

            if (!InteractionHelper.isOnScreenAndClickable(clickable07)) {
                InteractionHelper.quickFocus(clickable07);
            }

            return InteractionHelper.isOnScreenAndClickable(clickable07) && AccurateMouse.click(clickable07, action);
        });

        return (S) this;
    }

    public S action(String... action) {
        chain.add(() -> {
            if (clickable07 instanceof RSObject || clickable07 instanceof RSCharacter || clickable07 instanceof RSGroundItem) {

                if (Player.getPosition().distanceTo((Positionable) clickable07) < 6 && !InteractionHelper.isOnScreenAndClickable(clickable07)) {
                    InteractionHelper.quickFocus(clickable07);
                }

                if (!InteractionHelper.isOnScreenAndClickable(clickable07) || !PathFinding.canReach((Positionable) clickable07, clickable07 instanceof RSObject)) {
                    PathWalker.walkToClickable((Positionable) clickable07, false);
                }

                if (!InteractionHelper.isOnScreenAndClickable(clickable07)) {
                    InteractionHelper.quickFocus(clickable07);
                }

                return InteractionHelper.isOnScreenAndClickable(clickable07) && AccurateMouse.click(clickable07, action);
            }

            return AccurateMouse.click(clickable07, action);
        });

        return (S) this;
    }

    public S waitFor(BooleanLambda lambda, int shortTime, int longTime) {
        chain.add(() -> Timing.waitCondition(new org.tribot.api.types.generic.Condition() {
            @Override
            public boolean active() {
                General.sleep(125, 300);
                return lambda.active();
            }
        }, General.random(shortTime, longTime)));

        return (S) this;
    }

    public S waitForMovement(BooleanLambda lambda) {
        chain.add(() -> {
            Condition movementWait = Movement.getWalkingCondition(lambda);
            while (!movementWait.active()) {
                General.sleep(125, 300);
            }

            return lambda.active();
        });

        return (S) this;
    }

    public S waitForAnimation(BooleanLambda lambda, int animationLength) {
        chain.add(() -> {
            Condition movementWait = Movement.animationWait(lambda, animationLength);
            while (!movementWait.active()) {
                General.sleep(125, 300);
            }

            return lambda.active();
        });

        return (S) this;
    }

    public S waitForAnimation(BooleanLambda lambda, int animationLength, Consumable consumable) {
        chain.add(() -> {
            Condition movementWait = Movement.animationWait(lambda, animationLength);
            while (!movementWait.active()) {
                if (consumable != null && Player.getAnimation() != -1) {
                    consumable.consume();
                }

                General.sleep(125, 300);
            }

            return lambda.active();
        });

        return (S) this;
    }

    public S isNull(Consumable consumable) {
        chain.add(() -> {
            if (clickable07 == null) {
                consumable.consume();
                return false;
            }

            return true;
        });

        return (S) this;
    }

    public S setTimer() {
        chain.add(() -> {
            Antiban.setWaitingSince();
            return true;
        });
        return (S) this;
    }

    public S react() {
        chain.add(() -> {
            Antiban.get().performReactionTimeWait();
            return true;
        });
        return (S) this;
    }

    public S updateTrackers() {
        chain.add(() -> {
            final ABCProperties props = Antiban.get().getProperties();
            props.setWaitingTime(2000);
            props.setUnderAttack(true);
            props.setWaitingFixed(false);

            bag.addOrUpdate("combatStartTime", Timing.currentTimeMillis());

            Antiban.get().generateTrackers();
            return true;
        });

        return (S) this;
    }

    public S abc2() {
        chain.add(() -> {
            Antiban.setWaitingSince();
            Antiban.setLastCombatTime();

            Antiban.get().performReactionTimeWait();
            return true;
        });

        return (S) this;
    }
}