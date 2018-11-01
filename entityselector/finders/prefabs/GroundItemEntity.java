package scripts.api.entityselector.finders.prefabs;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.GroundItems;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSGroundItem;
import scripts.api.entityselector.finders.PositionableFinder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Laniax
 */
public class GroundItemEntity extends PositionableFinder<RSGroundItem, GroundItemEntity> {

    /**
     * {@link Filters.GroundItems#idEquals}
     */
    public GroundItemEntity idEquals(int... id) {
        filters.add(Filters.GroundItems.idEquals(id));
        return this;
    }

    /**
     * {@link Filters.GroundItems#idNotEquals}
     */
    public GroundItemEntity idNotEquals(int... id) {
        filters.add(Filters.GroundItems.idNotEquals(id));
        return this;
    }

    /**
     * {@link Filters.GroundItems#nameNotEquals}
     */
    public GroundItemEntity nameNotEquals(String... names) {
        filters.add(Filters.GroundItems.nameNotEquals(names));
        return this;
    }

    /**
     * {@link Filters.GroundItems#nameEquals}
     */
    public GroundItemEntity nameEquals(String... names) {
        filters.add(Filters.GroundItems.nameEquals(names));
        return this;
    }

    /**
     * {@link Filters.GroundItems#nameContains}
     */
    public GroundItemEntity nameContains(String... names) {
        filters.add(Filters.GroundItems.nameContains(names));
        return this;
    }

    /**
     * {@link Filters.GroundItems#nameNotContains}
     */
    public GroundItemEntity nameNotContains(String... names) {
        filters.add(Filters.GroundItems.nameNotContains(names));
        return this;
    }

    /**
     * {@link Filters.GroundItems#actionsContains}
     */
    public GroundItemEntity actionsContains(String... actions) {
        filters.add(Filters.GroundItems.actionsContains(actions));
        return this;
    }

    /**
     * {@link Filters.GroundItems#actionsNotContains}
     */
    public GroundItemEntity actionsNotContains(String... actions) {
        filters.add(Filters.GroundItems.actionsNotContains(actions));
        return this;
    }

    /**
     * {@link Filters.GroundItems#actionsEquals}
     */
    public GroundItemEntity actionsEquals(String... actions) {
        filters.add(Filters.GroundItems.actionsEquals(actions));
        return this;
    }

    /**
     * {@link Filters.GroundItems#actionsNotEquals}
     */
    public GroundItemEntity actionsNotEquals(String... actions) {
        filters.add(Filters.GroundItems.actionsNotEquals(actions));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public RSGroundItem[] getResults() {

        Filter<RSGroundItem> filter = super.buildFilter();

        RSGroundItem[] items = GroundItems.getAll(filter);

        if (super.shouldSortResults() && items.length > 1)
            super.sortByDistance(items);

        return items;
    }

    @Override
    public ArrayList<RSGroundItem> getResultList() {
        return new ArrayList<>(Arrays.asList(getResults()));
    }
}