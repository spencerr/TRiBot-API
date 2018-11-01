package scripts.api.items;

import org.tribot.api2007.Skills;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.BankItemEntity;
import scripts.api.entityselector.finders.prefabs.ItemEntity;

/**
 * Created by Spencer on 10/28/2017.
 */
public enum Fletching implements Item {
    ARROW_SHAFT(52, "Arrow shaft", 1, 5),
    UNSTRUNG_SHORTBOW(50, "Unstrung shortbow", 5, 5), UNSTRUNG_LONGBOW(48, "Unstrung longbow", 10, 10),
    UNSTRUNG_OAK_SHORTBOW(54, "Unstrung oak shortbow", 20, 16.5), UNSTRUNG_OAK_LONGBOW(56, "Unstrung oak longbow", 25, 25),
    UNSTRUNG_WILLOW_SHORTBOW(60, "Unstrung willow shortbow", 35, 33.3), UNSTRUNG_WILLOW_LONGBOW(58, "Unstrung willow longbow", 40, 41.5),
    UNSTRUNG_MAPLE_SHORTBOW(64, "Unstrung maple shortbow", 50, 50), UNSTRUNG_MAPLE_LONGBOW(62, "Unstrung maple longbow", 55, 58.3),
    UNSTRUNG_YEW_SHORTBOW(68, "Unstrung yew shortbow", 65, 67.5), UNSTRUNG_YEW_LONGBOW(66, "Unstrung yew longbow", 70, 75),
    UNSTRUNG_MAGIC_SHORTBOW(72, "Unstrung magic shortbow", 80, 83.3), UNSTRUNG_MAGIC_LONGBOW(70, "Unstrung magic longbow", 85, 91.5);

    int id, levelReq;
    double exp;
    String name;
    boolean consumable = true;
    Fletching(int id, String name, int levelReq) {
        this.id = id;
        this.name = name;
        this.levelReq = levelReq;
    }

    Fletching(int id, String name, int levelReq, double exp) {
        this.id = id;
        this.name = name;
        this.levelReq = levelReq;
        this.exp = exp;
    }

    @Override
    public int getId() {
        return id;
    }

    public int getLevelReq() {
        return levelReq;
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

    public int getRequiredToLevel(int level) {
        int requiredExp = Skills.getXPToLevel(Skills.SKILLS.FLETCHING, level);
        return (int) Math.ceil(requiredExp / exp);
    }
}
