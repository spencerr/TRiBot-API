package scripts.api.entityselector.finders.prefabs;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Banking;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import scripts.api.entityselector.Entities;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Laniax
 */
public class BankItemEntity extends RSItemFinder<BankItemEntity> {

    /**
     * {@inheritDoc}
     */
    @Override
    public RSItem[] getResults() {

        // Let's check if the bank is open and loaded.
        RSInterface amountOfItemsInterface = Entities.find(InterfaceEntity::new)
                .inMasterAndChild(12, 5)
                .isNotHidden()
                .getFirstResult();

        if (amountOfItemsInterface == null) // bank isn't open
            return new RSItem[0];

        int amountOfItems;

        try {
            amountOfItems = Integer.parseInt(amountOfItemsInterface.getText());
        } catch (NumberFormatException e) {
            return new RSItem[0]; // interface child id probably changed.
        }

        Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {

                if (Banking.getAll().length != amountOfItems) {
                    General.sleep(50);
                    return false;
                }

                return true;
            }
        }, General.random(500, 1000));

        Filter<RSItem> filter = super.buildFilter();

        if (filter != null)
            return Banking.find(filter);

        return Banking.getAll();
    }

    @Override
    public ArrayList<RSItem> getResultList() {
        return new ArrayList<>(Arrays.asList(getResults()));
    }
}