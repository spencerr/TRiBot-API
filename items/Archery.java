package scripts.api.items;

import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.BankItemEntity;
import scripts.api.entityselector.finders.prefabs.ItemEntity;

/**
 * Created by Spencer on 10/28/2017.
 */
public enum Archery implements Item {
    SHORTBOW, LONGBOW, OAK_SHORTBOW, OAK_LONGBOW, WILLOW_SHORTBOW, WILLOW_LONGBOW, MAPLE_SHORTBOW, MAPLE_LONGBOW, YEW_SHORTBOW, YEW_LONGBOW, MAGIC_SHORTBOW, MAGIC_LONGBOW;

    int id;
    String name;
    boolean consumable = false;

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
