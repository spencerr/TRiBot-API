package scripts.api.entityselector.finders.prefabs;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Players;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSPlayer;
import scripts.api.entityselector.finders.PositionableFinder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Laniax
 */
public class PlayerEntity extends PositionableFinder<RSPlayer, PlayerEntity> {

    /**
     * {@link Filters.Players#inArea}
     */
    public PlayerEntity inArea(RSArea area) {
        filters.add(Filters.Players.inArea(area));
        return this;
    }

    /**
     * {@link Filters.Players#notInArea}
     */
    public PlayerEntity notInArea(RSArea area) {
        filters.add(Filters.Players.notInArea(area));
        return this;
    }

    /**
     * {@link Filters.Players#nameContains}
     */
    public PlayerEntity nameContains(String... names) {
        filters.add(Filters.Players.nameContains(names));
        return this;
    }

    /**
     * {@link Filters.Players#nameNotContains}
     */
    public PlayerEntity nameNotContains(String... names) {
        filters.add(Filters.Players.nameNotContains(names));
        return this;
    }

    /**
     * {@link Filters.Players#nameEquals}
     */
    public PlayerEntity nameEquals(String... names) {
        filters.add(Filters.Players.nameEquals(names));
        return this;
    }

    /**
     * {@link Filters.Players#nameNotEquals}
     */
    public PlayerEntity nameNotEquals(String... names) {
        filters.add(Filters.Players.nameNotEquals(names));
        return this;
    }

    /**
     * {@link Filters.Players#tileEquals}
     */
    public PlayerEntity tileEquals(Positionable positionable) {
        filters.add(Filters.Players.tileEquals(positionable));
        return this;
    }

    /**
     * {@link Filters.Players#tileNotEquals}
     */
    public PlayerEntity tileNotEquals(Positionable positionable) {
        filters.add(Filters.Players.tileNotEquals(positionable));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public RSPlayer[] getResults() {

        Filter<RSPlayer> filter = super.buildFilter();

        RSPlayer[] players = Players.getAll(filter);

        if (super.shouldSortResults() && players.length > 1)
            super.sortByDistance(players);

        return players;
    }

    @Override
    public ArrayList<RSPlayer> getResultList() {
        return new ArrayList<>(Arrays.asList(getResults()));
    }
}