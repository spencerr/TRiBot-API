package scripts.api.settings;

import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSInterface;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;

/**
 * Created by Spencer on 8/17/2017.
 */
public class Settings {
    public static final int OPTION_INTERFACE_MASTER = 261;
    public static final int OPTION_TAB_INTERFACE = 1;
    public static final int LIGHTING_COMPONENT = 15;

    public static final int ADVANCED_OPTIONS_MASTER = 60;
    public static final int ADVANCED_OPTIONS_BUTTON = 21;

    public static boolean isAcceptAidEnabled() {
        return Game.getSetting(427) == 1;
    }

    public static boolean setAcceptAid(boolean enable) {
        if (isAcceptAidEnabled() == enable) return true;

        if (OptionTab.ANY.open()) {
            RSInterface acceptAid = Entities.find(InterfaceEntity::new)
                    .inMasterAndChild(OPTION_INTERFACE_MASTER, 70)
                    .getFirstResult();

            Interact.with(InterfacePrefab::new)
                    .set(acceptAid)
                    .action()
                    .waitFor(() -> isAcceptAidEnabled() == enable, 750, 1250)
                    .execute();
        }

        return isAcceptAidEnabled() == enable;
    }

    public static boolean isRunEnabled() {
        return false;
    }

    public static boolean setRun(boolean enable) {
        if (isRunEnabled() == enable) return true;

        if (OptionTab.ANY.open()) {
            RSInterface run = Entities.find(InterfaceEntity::new)
                    .inMasterAndChild(OPTION_INTERFACE_MASTER, 72)
                    .getFirstResult();

            Interact.with(InterfacePrefab::new)
                    .set(run)
                    .action()
                    .waitFor(() -> isRunEnabled() == enable, 750, 1250)
                    .execute();
        }

        return isRunEnabled() == enable;
    }

    public static boolean closeAll() {
        return HouseSettings.close() && ControlSettings.closeKeybinds() && DisplaySettings.closeAdvancedOptions();
    }
}
