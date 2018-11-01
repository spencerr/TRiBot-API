/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scripts.api.util;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import scripts.api.pattern.BooleanLambda;
import scripts.api.pattern.Condition;

/**
 *
 * @author Spencer
 */
public class InventoryUtil {
    public static boolean interact(String item, String... option) {
        GameTab.open(GameTab.TABS.INVENTORY);
        final int lastCount = Inventory.getCount(item);
        if (lastCount > 0) {
            RSItem i = getFirst(item);
            if (i != null) {
                if (i.click(option)) {
                    return new Condition(() -> InventoryUtil.getCount(item) != lastCount).execute(1250, 2000);
                }
            }
        }

        return false;
    }

    public static boolean interact(int[] items, String option, BooleanLambda lambda, int t1, int t2) {
        GameTab.open(GameTab.TABS.INVENTORY);
        if (Inventory.getCount(items) > 0) {
            RSItem i = getFirst(items);
            if (i != null) {
                if (Clicking.click(option, i)) {
                    return new Condition(lambda).execute(t1, t2);
                }
            }
        }

        return new Condition(lambda).execute(t1, t2);
    }

    public static boolean interact(int item, String option, BooleanLambda lambda, int t1, int t2) {
        GameTab.open(GameTab.TABS.INVENTORY);
        if (Inventory.getCount(item) > 0) {
            RSItem i = getFirst(item);
            if (i != null) {
                if (Clicking.click(option, i)) {
                    return new Condition(lambda).execute(t1, t2);
                }
            }
        }

        return new Condition(lambda).execute(t1, t2);
    }

    public static boolean interact(String item, BooleanLambda lamba, int t1, int t2, String... option) {
        GameTab.open(GameTab.TABS.INVENTORY);

        if (Inventory.getCount(item) > 0) {
            RSItem i = getFirst(item);
            if (i != null) {
                if (i.click(option))
                    return Timing.waitCondition(new Condition(lamba), General.random(t1, t2));
            }
        }

        return Timing.waitCondition(new Condition(lamba), General.random(t1, t2));
    }

    public static RSItem getFirst(int... ids) {
        RSItem[] items = Inventory.find(ids);
        if (items != null && items.length > 0)
            return items[0];
        return null;
    }
    
    public static RSItem getFirst(String... item) {
        RSItem[] items = Inventory.find(item);
        if (items != null && items.length > 0)
            return items[0];
        return null;
    }
    
    public static boolean isUsing(String item) {
        return (Game.getUptext() != null && Game.getUptext().contains("Use " + item + " ->"));
    }

    public static int getCount(String... items) {
        int count = 0;
        for (String item : items) {
            count += Inventory.getCount(item);
        }

        return count;
    }

    public static int getCount(int... items) {
        int count = 0;
        for (int item : items) {
            count += Inventory.getCount(item);
        }

        return count;
    }
}
