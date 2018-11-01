package scripts.api.items;

import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.BankItemEntity;
import scripts.api.entityselector.finders.prefabs.ItemEntity;

/**
 * Created by Spencer on 10/28/2017.
 */
public enum Logs implements Item {

    NORMAL(1511, "Logs"), OAK(1521, "Oak logs"), WILLOW(1519, "Willow logs"), MAPLE(1517, "Maple logs"), YEW(1515, "Yew logs"), MAGIC(1513, "Magic logs");

    int id;
    String name;
    boolean consumable = true;

    Logs(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isConsumable() {
        return consumable;
    }

    @Override
    public boolean has() {
        return has(1);
    }

    @Override
    public boolean has(int count) {
        return Entities.find(ItemEntity::new).idEquals(getId()).hasCount(count).getFirstResult() != null;
    }

    @Override
    public boolean hasInBank() {
        return hasInBank(1);
    }

    @Override
    public boolean hasInBank(int count) {
        return Entities.find(BankItemEntity::new).idEquals(getId()).hasCount(count).getFirstResult() != null;
    }
}
