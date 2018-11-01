package scripts.api.settings;

import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;

/**
 * Created by Spencer on 9/23/2017.
 */
public class GameTab {

    public enum TABS {
        COMBAT(1, "Combat Options"),
        STATS(2, "Stats"),
        QUESTS(3, "Minigames", "Achievement Diaries", "Kourend Tasks", "Quest List"),
        INVENTORY(27, "Inventory"),
        EQUIPMENT(4, "Worn Equipment"),
        PRAYERS(5, "Prayer"),
        MAGIC(6, "Magic"),
        CLAN(7, "Clan Chat"),
        FRIENDS(8, "Friends List"),
        IGNORE(9, "Ignore List"),
        LOGOUT(0, "Logout"),
        OPTIONS(10, "Options"),
        EMOTES(11, "Emotes"),
        MUSIC(12, "Music Player");

        public final int f_key;
        public final java.lang.String[] names;

        TABS(int f_key, String... names) {
            this.f_key = f_key;
            this.names = names;
        }

        public boolean isOpen() {
            return getOpen() == this;
        }

        public boolean open() {
            return open(false);
        }

        public boolean open(boolean use_f_key) {
            if (isOpen()) return true;

            if (f_key > 0 && use_f_key) {
                if (f_key == 27) {
                    Keyboard.typeKeys('\u001b');
                } else {
                    Keyboard.pressFunctionKey(f_key);
                }

                return true;
            }

            return Interact.with(InterfacePrefab::new)
                    .set(getTabInterface())
                    .action(names)
                    .execute();

            /*
            if (getOpen() == this) return true;
            if (f_key > 0 && General.useAntiBanCompliance()) {
                if (f_key == 27) {
                    Keyboard.typeKeys('\u001b');
                } else {
                    Keyboard.pressFunctionKey(f_key);
                }

                return true;
            }

            RSInterfaceChild[] children = Interfaces.getChildren(548);
            if (children == null) return false;

            for (RSInterfaceChild child : children) {
                String[] actions;
                String action;
                if (child == null || (actions = child.getActions()) == null || actions.length < 1 || (action = actions[0]) == null || !action.equals(name))
                    continue;

                if (!Arrays.asList(secondary_names).contains(action)) return false;

                if (child.isHidden()) continue;
                return AccurateMouse.click(child);
            }

            return false;*/
        }

        public RSInterface getTabInterface() {
            return Entities.find(InterfaceEntity::new)
                    .inMaster(548)
                    .actionContains(names)
                    .getFirstResult();
        }

        public boolean hover() {
            return isHovering() || Interact.with(InterfacePrefab::new)
                    .set(getTabInterface())
                    .hover()
                    .execute();

            /*for (RSInterfaceChild child : children) {
                String[] actions;
                String action;
                if (child == null || (actions = child.getActions()) == null || actions.length < 1 || (action = actions[0]) == null || !action.equals(name))
                    continue;

                if (!action.equals(name)) {
                    if (secondary_names == null) return false;
                    if (!Arrays.asList(secondary_names).contains(action)) return false;
                }

                if (child.isHidden()) continue;
                return AccurateMouse.hover(child);
            }

            return false;*/
        }

        public boolean isHovering() {
            RSInterface tab = getTabInterface();
            return tab != null && tab.getAbsoluteBounds().contains(Mouse.getPos());
        }
    }

    public static TABS getOpen() {
        if (Banking.isBankScreenOpen()) return TABS.INVENTORY;
        if (Interfaces.isInterfaceValid(335)) return TABS.INVENTORY;
        if (Interfaces.isInterfaceValid(85)) return TABS.INVENTORY;
        if (Interfaces.isInterfaceValid(464)) return TABS.INVENTORY;

        RSInterfaceChild[] children = Interfaces.getChildren(548);
        if (children == null) return TABS.INVENTORY;
        for (RSInterfaceChild child : children) {
            String[] actions;
            String action;
            if (child != null && (actions = child.getActions()) != null && actions.length > 0 && (action = actions[0]) != null) {
                for (TABS tab : TABS.values()) {
                    for (String secondary_name : tab.names) {
                        if (secondary_name.equals(action)) {
                            if (child.getTextureID() == -1) break;
                            return tab;
                        }
                    }
                }
            }
        }

        return TABS.INVENTORY;
    }

}
