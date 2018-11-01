package scripts.api.settings;

import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.types.RSInterface;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;

/**
 * Created by Spencer on 8/17/2017.
 */

public enum Sound {
    MUSIC(168, 24), EFFECTS(169, 30), AREA_EFFECTS(872, 36);

    int setting;
    int component;
    Sound(int setting, int component) {
        this.setting = setting;
        this.component = component;
    }

    public Level getLevel() {
        int result = Game.getSetting(setting) - 1;
        return result >= 0 && result <= Level.values().length - 1 ? Level.values()[Game.getSetting(setting) - 1] : Level.HIGH;
    }

    public boolean setLevel(Level level) {
        Level currentLevel = getLevel();
        if (level != currentLevel) {
            if (GameTab.open(GameTab.TABS.OPTIONS) && OptionTab.SOUND.open()) {
                RSInterface slider = Entities.find(InterfaceEntity::new)
                        .inMasterAndChild(scripts.api.settings.Settings.OPTION_INTERFACE_MASTER, component + level.getIndex())
                        .getFirstResult();

                return Interact.with(InterfacePrefab::new)
                        .set(slider)
                        .action()
                        .waitFor(() -> getLevel() == level, 750, 1250)
                        .execute();
            }
        }

        return getLevel() == level;
    }


    public enum Level {
        HIGH(3), MEDIUM(2), LOW(1), OFF(0);

        int index;
        Level(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }
    }
}



