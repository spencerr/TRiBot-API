package scripts.api.items;

import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.BankItemEntity;
import scripts.api.entityselector.finders.prefabs.ItemEntity;

/**
 * Created by Spencer on 10/8/2017.
 */
public enum Rune implements Item {
    AIR(-1, "Air rune"), MIND(-1, "Mind rune"), WATER(-1, "Water rune"), EARTH(-1, "Earth rune"),
    FIRE(-1, "Fire rune"), BODY(-1, "Body rune"), COSMIC(-1, "Cosmic rune"), CHAOS(-1, "Chaos rune"),
    NATURE(-1, "Nature rune"), LAW(-1, "Law rune"), DEATH(-1, "Death rune"), ASTRAL(-1, "Astral rune"),
    BLOOD(-1, "Blood rune"), SOUL(-1, "Soul rune"), MIST(-1, "Mist rune"), DUST(-1, "Dust rune"),
    MUD(-1, "Mud rune"), SMOKE(-1, "Smoke rune"), STEAM(-1, "Steam rune"), LAVA(-1, "Law rune"), NONE(-1, null);

    int id;
    String name;
    boolean consumable = true;
    Rune(int id, String name) {
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
