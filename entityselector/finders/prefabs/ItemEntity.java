package scripts.api.entityselector.finders.prefabs;

import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Laniax
 */
public class ItemEntity extends RSItemFinder<ItemEntity> {

    /**
     * {@inheritDoc}
     */
    public RSItem[] getResults() {

        Filter<RSItem> filter = super.buildFilter();

        if (filter != null)
            return Inventory.find(filter); // Remarkably, the Inventory class is the only class without a #getAll(filter) method.

        return Inventory.getAll();
    }

    @Override
    public ArrayList<RSItem> getResultList() {
        return new ArrayList<>(Arrays.asList(getResults()));
    }
}