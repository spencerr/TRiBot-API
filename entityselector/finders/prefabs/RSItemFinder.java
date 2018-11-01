package scripts.api.entityselector.finders.prefabs;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSItem;
import scripts.api.entityselector.finders.Finder;

/**
 * @author Laniax
 */
public abstract class RSItemFinder<T extends Finder> extends Finder<RSItem, T> {

    /**
     * {@link Filters.Items#idEquals}
     */
    @SuppressWarnings("unchecked")
    public T idEquals(int... id) {
        filters.add(Filters.Items.idEquals(id));
        return (T) this;
    }

    /**
     * {@link Filters.Items#idNotEquals}
     */
    @SuppressWarnings("unchecked")
    public T idNotEquals(int... id) {
        filters.add(Filters.Items.idNotEquals(id));
        return (T) this;
    }

    /**
     * {@link Filters.Items#nameNotEquals}
     */
    @SuppressWarnings("unchecked")
    public T nameNotEquals(String... names) {
        filters.add(Filters.Items.nameNotEquals(names));
        return (T) this;
    }

    /**
     * {@link Filters.Items#nameEquals}
     */
    @SuppressWarnings("unchecked")
    public T nameEquals(String... names) {
        filters.add(Filters.Items.nameEquals(names));
        return (T) this;
    }

    /**
     * {@link Filters.Items#nameContains}
     */
    @SuppressWarnings("unchecked")
    public T nameContains(String... names) {
        filters.add(Filters.Items.nameContains(names));
        return (T) this;
    }

    /**
     * {@link Filters.Items#nameNotContains}
     */
    @SuppressWarnings("unchecked")
    public T nameNotContains(String... names) {
        filters.add(Filters.Items.nameNotContains(names));
        return (T) this;
    }

    /**
     * {@link Filters.Items#actionsContains}
     */
    @SuppressWarnings("unchecked")
    public T actionsContains(String... actions) {
        filters.add(Filters.Items.actionsContains(actions));
        return (T) this;
    }

    /**
     * {@link Filters.Items#actionsNotContains}
     */
    @SuppressWarnings("unchecked")
    public T actionsNotContains(String... actions) {
        filters.add(Filters.Items.actionsNotContains(actions));
        return (T) this;
    }

    /**
     * {@link Filters.Items#actionsEquals}
     */
    @SuppressWarnings("unchecked")
    public T actionsEquals(String... actions) {
        filters.add(Filters.Items.actionsEquals(actions));
        return (T) this;
    }

    /**
     * {@link Filters.Items#actionsNotEquals}
     */
    @SuppressWarnings("unchecked")
    public T actionsNotEquals(String... actions) {
        filters.add(Filters.Items.actionsNotEquals(actions));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T hasCount(int count) {
        filters.add(new Filter<RSItem>() {
            @Override
            public boolean accept(RSItem rsItem) {
                return rsItem.getStack() >= count;
            }
        });
        return (T) this;
    }
}