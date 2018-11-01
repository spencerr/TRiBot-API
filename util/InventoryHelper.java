package scripts.api.util;

import org.tribot.api2007.types.RSItem;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.ItemEntity;
import scripts.api.items.Item;

import java.util.ArrayList;

/**
 * Created by Spencer on 10/28/2017.
 */
public class InventoryHelper {

    public static boolean hasItems(int... items) {
        for (int item : items) {
            if (Entities.find(ItemEntity::new).idEquals(item).hasCount(1).getFirstResult() == null) return false;
        }

        return true;
    }

    public static boolean hasItems(ArrayList<Item> items) {
        return items.stream().allMatch(Item::has);
    }

    public static boolean hasItem(Item item) {
        return item.has();
    }

    public static int getCount(int id) {
        ArrayList<RSItem> items = Entities.find(ItemEntity::new).idEquals(id).getResultList();
        int count = 0;
        for (RSItem item : items) {
            count += item.getStack();
        }

        return count;
    }

}
