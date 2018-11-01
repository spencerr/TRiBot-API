package scripts.api.util;

import org.tribot.api2007.Skills;
import org.tribot.api2007.types.RSInterface;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;
import scripts.api.items.GeneralItems;
import scripts.api.items.Item;
import scripts.api.items.Rune;
import scripts.api.settings.GameTab;

import java.util.HashMap;

public class Magic {

    public enum SpellBook {
        STANDARD, ANCIENTS, LUNAR, ARCEUUS
    }

    public enum StandardSpell implements Spell {
        /*HOME_TELEPORT(0), WIND_STRIKE(1, Rune.AIR, 1, Rune.MIND, 1), CONFUSE(3, Rune.BODY, 1, Rune.EARTH, 2, Rune.WATER, 3),
        ENCHANT_CROSSBOW_BOLT_OPAL(4, Rune.COSMIC, 1, Rune.AIR, 2), WATER_STRIKE(5, Rune.MIND, 1, Rune.WATER, 1, Rune.AIR, 1),
        LVL_1_ENCHANT(7, Rune.COSMIC, 1, Rune.WATER, 1), ENCHANT_CROSSBOW_BOLT_SAPPHIRE(7, Rune.COSMIC, 1, Rune.WATER, 1),
        EARTH_STRIKE(9, Rune.MIND, 1, Rune.EARTH, 2, Rune.AIR, 1), WEAKEN(11, Rune.BODY, 1, Rune.EARTH, 2, Rune.WATER, 3),
        ENCHANT_CROSSBOW_BOLT_JADE(14, Rune.COSMIC, 1, Rune.EARTH, 2), BONES_TO_BANANAS(15, Rune.NATURE, 1, Rune.EARTH, 2, Rune.WATER, 2),
        WIND_BOLT(17, Rune.CHAOS, 1, Rune.AIR, 2), CURSE(19, Rune.BODY, 1, Rune.EARTH, 3, Rune.WATER, 2), BIND(20, Rune.NATURE, 2, Rune.EARTH, 3, Rune.WATER, 3),
        LOW_LEVEL_ALCHEMY(21, Rune.NATURE, 1, Rune.FIRE, 3), WATER_BOLT(23, Rune.CHAOS, 1, Rune.WATER, 2, Rune.AIR, 2),
        ENCHANT_CROSSBOW_BOLT_PEARL(24, Rune.COSMIC, 1, Rune.WATER, 2), VARROCK_TELEPORT(25, Rune.LAW, 1, Rune.FIRE, 1, Rune.AIR, 3),
        LVL_2_ENCHANT(27, Rune.COSMIC, 1, Rune.AIR, 3), ENCHANT_CROSSBOW_BOLT_EMERALD(27, Rune.NATURE, 1, Rune.COSMIC, 1, Rune.AIR, 3),
        EARTH_BOLT(29, Rune.CHAOS, 1, Rune.EARTH, 3, Rune.AIR, 2), ENCHANT_CROSSBOW_BOLT_RED_TOPAZ(29, Rune.COSMIC, 1, Rune.FIRE, 2),
        LUMBRIDGE_TELEPORT(31, Rune.LAW, 1, Rune.EARTH, 1, Rune.AIR, 3), TELEKINETIC_GRAB(33, Rune.LAW, 1, Rune.AIR, 1),
        FIRE_BOLT(35, Rune.CHAOS, 1, Rune.FIRE, 4, Rune.AIR, 3)*/

        HOME_TELEPORT("Home Teleport", 0, Rune.NONE, 0),
        WIND_STRIKE("Wind Strike", 1, Rune.MIND, 1, Rune.AIR, 1, 5.5),
        CONFUSE("Confuse", 3, Rune.BODY, 1, Rune.EARTH, 2, Rune.WATER, 3, 13),
        ENCHANT_CROSSBOW_BOLT_OPAL("Enchant Crossbow Bolt (Opal)", 4, Rune.COSMIC, 1, Rune.AIR, 2, 9),
        WATER_STRIKE("Water Strike", 5, Rune.MIND, 1, Rune.WATER, 1, Rune.AIR, 1, 7.5),
        LVL_1_ENCHANT("Lvl-1 Enchant", 7, Rune.COSMIC, 1, Rune.WATER, 1, 17.5),
        ENCHANT_CROSSBOW_BOLT_SAPPHIRE("Enchant Crossbow Bolt (Sapphire)", 7, Rune.COSMIC, 1, Rune.WATER, 1, 17),
        EARTH_STRIKE("Earth Strike", 9, Rune.MIND, 1, Rune.EARTH, 2, Rune.AIR, 1, 9.5),
        WEAKEN("Weaken", 11, Rune.BODY, 1, Rune.EARTH, 2, Rune.WATER, 3, 21),
        FIRE_STRIKE("Fire Strike", 13, Rune.MIND, 1, Rune.FIRE, 3, Rune.AIR, 2, 11.5),
        ENCHANT_CROSSBOW_BOLT_JADE("Enchant Crossbow Bolt (Jade)", 14, Rune.COSMIC, 1, Rune.EARTH, 2, 19),
        BONES_TO_BANANAS("Bones to Bananas", 15, Rune.NATURE, 1, Rune.EARTH, 2, Rune.WATER, 2, 25),
        WIND_BOLT("Wind Bolts", 17, Rune.CHAOS, 1, Rune.AIR, 2, 13.5),
        CURSE("Curse", 19, Rune.BODY, 1, Rune.EARTH, 3, Rune.WATER, 2, 29),
        BIND("Bind", 20, Rune.NATURE, 2, Rune.EARTH, 3, Rune.WATER, 3, 30),
        LOW_LEVEL_ALCHEMY("Low Level Alchemy", 21, Rune.NATURE, 1, Rune.FIRE, 3, 31),
        WATER_BOLT("Water Bolt", 23, Rune.CHAOS, 1, Rune.WATER, 2, Rune.AIR, 2, 16.5),
        ENCHANT_CROSSBOW_BOLT_PEARL("Enchant Crossbow Bolt (Pearl)", 24, Rune.COSMIC, 1, Rune.WATER, 2, 29),
        VARROCK_TELEPORT("Varrock Teleport", 25, Rune.LAW, 1, Rune.FIRE, 1, Rune.AIR, 3, 35),
        LVL_2_ENCHANT("Lvl-2 Enchant", 27, Rune.COSMIC, 1, Rune.AIR, 3, 37),
        ENCHANT_CROSSBOW_BOLT_EMERALD("Enchant Crossbow Bolt (Emerald)", 27, Rune.NATURE, 1, Rune.COSMIC, 1, Rune.AIR, 3, 37),
        EARTH_BOLT("Earth Bolt", 29, Rune.CHAOS, 1, Rune.EARTH, 1, Rune.AIR, 3, 19.5),
        ENCHANT_CROSSBOW_BOLT_RED_TOPAZ("Enchant Crossbow Bolt (Red Topaz)", 29, Rune.COSMIC, 1, Rune.FIRE, 2, 33),
        LUMBRIDGE_TELEPORT("Lumbridge Teleport", 31, Rune.LAW, 1, Rune.EARTH, 1, Rune.AIR, 3, 41),
        TELEKINETIC_GRAB("Telekinetic Grab", 33, Rune.LAW, 1, Rune.AIR, 1, 43),
        FIRE_BOLT("Fire Bolt", 35, Rune.CHAOS, 1, Rune.FIRE, 4, Rune.AIR, 3, 22.5),
        FALADOR_TELEPORT("Falador Teleport", 37, Rune.LAW, 1, Rune.WATER, 1, Rune.AIR, 3, 47),
        CRUMBLE_UNDEAD("Crumble Undead", 39, Rune.CHAOS, 1, Rune.EARTH, 2, Rune.AIR, 2, 24.5),
        TELEPORT_TO_HOUSE("Teleport to House", 40, Rune.LAW, 1, Rune.EARTH, 1, Rune.AIR, 1, 30),
        WIND_BLAST("Wind Blast", 41, Rune.DEATH, 1, Rune.AIR, 3, 25.5),
        SUPERHEAT_ITEM("Superheat Item", 43, Rune.NATURE, 1, Rune.FIRE, 4, 53),
        CAMELOT_TELEPORT("Camelot Teleport", 45, Rune.LAW, 1, Rune.AIR, 5, 55.5),
        WATER_BLAST("Water Blast", 47, Rune.DEATH, 1, Rune.WATER, 3, Rune.AIR, 3, 28.5),
        LVL_3_ENCHANT("Lvl-3 Enchant", 49, Rune.COSMIC, 1, Rune.FIRE, 5, 59),
        ENCHANT_CROSSBOW_BOLT_RUBY("Enchant Crossbow Bolt (Ruby)", 49, Rune.BLOOD, 1, Rune.COSMIC, 1, Rune.FIRE, 5, 59),
        IBAN_BLAST("Iban Blast", 50, Rune.DEATH, 1, Rune.FIRE, 5, 30),
        SNARE("Snare", 50, Rune.NATURE, 3, Rune.EARTH, 4, Rune.WATER, 4, 60),
        MAGIC_DART("Magic Dart", 50, Rune.DEATH, 1, Rune.MIND, 4, 30),
        ARDOUGNE_TELEPORT("Ardougne Teleport", 51, Rune.LAW, 2, Rune.WATER, 2, 61),
        EARTH_BLAST("Earth Blast", 53, Rune.DEATH, 1, Rune.EARTH, 4, Rune.AIR, 3, 31.5),
        HIGH_LEVEL_ALCHEMY("High Level Alchemy", 55, Rune.NATURE, 1, Rune.FIRE, 5, 65),
        CHARGE_WATER_ORB("Charge Water Orb", 56, Rune.COSMIC, 3, Rune.WATER, 30, GeneralItems.UNPOWERED_ORB, 1, 56),
        LVL_4_ENCHANT("Lvl-4 Enchant", 57, Rune.COSMIC, 1, Rune.EARTH, 10, 67),
        ENCHANT_CROSSBOW_BOLT_DIAMOND("Enchant Crossbow Bolt (Diamond)", 57, Rune.LAW, 2, Rune.COSMIC, 1, Rune.EARTH, 10, 67),
        WATCHTOWER_TELEPORT("Watchtower Teleport", 58, Rune.LAW, 2, Rune.EARTH, 2, 68),
        FIRE_BLAST("Fire Blast", 59, Rune.DEATH, 1, Rune.FIRE, 5, Rune.AIR, 4, 34.5),
        CHARGE_EARTH_ORB("Charge Earth Orb", 60, Rune.COSMIC, 3, Rune.EARTH, 30, GeneralItems.UNPOWERED_ORB, 1, 70),
        BONES_TO_PEACHES("Bones to Peaches", 60, Rune.NATURE, 2, Rune.EARTH, 2, Rune.WATER, 4, 65),
        SARADOMIN_STRIKE("Saradomin Strike", 60, Rune.BLOOD, 2, Rune.FIRE, 2, Rune.AIR, 4, 61),
        CLAWS_OF_GUTHIX("Claws of Guthix", 60, Rune.BLOOD, 2, Rune.FIRE, 1, Rune.AIR, 4, 61),
        FLAMES_OF_ZAMORAK("Flames of Zamorak", 60, Rune.BLOOD, 2, Rune.FIRE, 4, Rune.AIR, 1, 61),
        TROLLHEIM_TELEPORT("Trollheim Teleport", 61, Rune.LAW, 2, Rune.FIRE, 2, 68),
        WIND_WAVE("Wind Wave", 62, Rune.BLOOD, 1, Rune.AIR, 5, 36),
        CHARGE_FIRE_ORB("Chrage Fire Orb", 63, Rune.COSMIC, 3, Rune.FIRE, 30, GeneralItems.UNPOWERED_ORB, 1, 73),
        TELEPORT_TO_APE_ATOLL("Teleport to Ape Atoll", 64, Rune.LAW, 2, Rune.FIRE, 2, Rune.WATER, 2, GeneralItems.BANANA, 1, 74),
        WATER_WAVE("Water Wave", 65, Rune.BLOOD, 1, Rune.WATER, 7, Rune.AIR, 5, 37.5),
        CHARGE_AIR_ORB("Charge Air Orb", 66, Rune.COSMIC, 3, Rune.AIR, 30, GeneralItems.UNPOWERED_ORB, 1, 76),
        VULNERABILITY("Vulnerability", 66, Rune.SOUL, 1, Rune.EARTH, 5, Rune.WATER, 5, 76),
        LVL_5_ENCHANT("Lvl-5 Enchant", 68, Rune.COSMIC, 1, Rune.EARTH, 15, Rune.WATER, 15, 78),
        ENCHANT_CROSSBOW_BOLT_DRAGONSTONE("Enchant Crossbow Bolt (Dragonstone)", 68, Rune.SOUL, 1, Rune.COSMIC, 1, Rune.EARTH, 12, 78),
        TELEPORT_TO_KOUREND("Teleport to Kourend", 69, Rune.SOUL, 2, Rune.LAW, 2, Rune.FIRE, 5, Rune.WATER, 4, 82),
        EARTH_WAVE("Earth Wave", 70, Rune.BLOOD, 1, Rune.EARTH, 7, Rune.AIR, 5, 40),
        ENFEEBLE("Enfeeble", 73, Rune.SOUL, 1, Rune.EARTH, 8, Rune.WATER, 8, 83),
        TELEOTHER_LUMBRIDGE("Teleother Lumbridge", 74, Rune.SOUL, 1, Rune.LAW, 1, Rune.EARTH, 1, 84),
        FIRE_WAVE("Fire Wave", 75, Rune.BLOOD, 1, Rune.FIRE, 7, Rune.AIR, 5, 42.5),
        ENTANGLE("Entangle", 79, Rune.NATURE, 4, Rune.EARTH, 5, Rune.WATER, 5, 89),
        STUN("Stun", 80, Rune.SOUL, 1, Rune.EARTH, 12, Rune.WATER, 12, 90),
        CHARGE("Charge", 80, Rune.BLOOD, 3, Rune.FIRE, 3, Rune.AIR, 3, 180),
        TELEOTHER_FALADOR("Teleother Falador", 82, Rune.SOUL, 1, Rune.LAW, 1, Rune.WATER, 1, 92),
        TELE_BLOCK("Tele Block", 85, Rune.LAW, 1, Rune.DEATH, 1, Rune.CHAOS, 1, 80),
        TELEPORT_TO_BOUNTY_TARGET("Teleport to Bounty Target", 85, Rune.LAW, 1, Rune.DEATH, 1, Rune.CHAOS, 1, 45),
        LVL_6_ENCHANT("Lvl-6 Enchant", 87, Rune.COSMIC, 1, Rune.FIRE, 20, Rune.EARTH, 20, 97),
        ENCHANT_CROSSBOW_BOLT_ONYX("Enchant Crossbow Bolt (Onyx)", 87, Rune.DEATH, 1, Rune.COSMIC, 1, Rune.FIRE, 20, 97),
        TELEOTHER_CAMELOT("Teleother Camelot", 90, Rune.SOUL, 2, Rune.LAW, 1, 100),
        LVL_7_ENCHANT("Lvl-7 Enchant", 93, Rune.SOUL, 20, Rune.BLOOD, 20, Rune.COSMIC, 1, 110);

        String name;
        int level;
        double exp;
        HashMap<Item, Integer> items = new HashMap<>();
        StandardSpell(String name, int level, Object... args) {
            this.name = name;
            this.level = level;
            for (int i = 0; i < args.length; i += 2) {
                if (args[i] instanceof Item)
                    this.items.put((Item) args[i], (int) args[i + 1]);
                else if (args[i] instanceof Integer)
                    exp = (double) (int) args[i];
                else
                    exp = (double) args[i];
            }
        }

        public boolean isSelected() {
            return this == getSelectedSpell();
        }

        public boolean select() {
            if (isSelected() && canCast()) return true;

            if (GameTab.TABS.MAGIC.open()) {

                RSInterface spell = Entities.find(InterfaceEntity::new)
                        .componentNameContains(name)
                        .getFirstResult();

                return Interact.with(InterfacePrefab::new)
                        .set(spell)
                        .action("Cast", "Reanimate")
                        .execute();
            }

            return false;
        }

        public String getName() {
            return name;
        }

        public boolean canCast() {
            for (Item item : items.keySet()) {
                if (item.getId() == -1) continue;
                if (!item.has(items.get(item))) return false;
            }

            return getLevel() >= level;
        }
    }

    public static int getLevel() {
        return Skills.SKILLS.MAGIC.getCurrentLevel();
    }

    public static int getActualLevel() {
        return Skills.SKILLS.MAGIC.getActualLevel();
    }

    public static Spell getSelectedSpell() {
        if (!org.tribot.api2007.Magic.isSpellSelected()) return null;

        String spellName = org.tribot.api2007.Magic.getSelectedSpellName();

        for (Spell spell : StandardSpell.values()) {
            if (spell.getName().equalsIgnoreCase(spellName))
                return spell;
        }

        return null;
    }

}
