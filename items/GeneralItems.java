package scripts.api.items;

import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.BankItemEntity;
import scripts.api.entityselector.finders.prefabs.ItemEntity;

/**
 * Created by Spencer on 10/8/2017.
 */
public enum GeneralItems implements Item {
    UNPOWERED_ORB(-1, "Unpowered orb"), BANANA(-1, "Banana"), KNIFE(946, "Knife"), COINS(995, "Coins"), NOTHING(-1, "");

    int id;
    String name;
    boolean consumable = true;

    GeneralItems(int id, String name) {
        this.id = id;
        this.name = name;
    }

    GeneralItems(int id, String name, boolean consumable) {
        this.id = id;
        this.name = name;
        this.consumable = consumable;
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
