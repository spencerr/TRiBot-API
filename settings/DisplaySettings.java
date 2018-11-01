package scripts.api.settings;

import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.Options;
import org.tribot.api2007.types.RSInterface;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;
import scripts.api.pattern.BooleanLambda;

/**
 * Created by Spencer on 8/17/2017.
 */
public class DisplaySettings {

    public static int getZoomPosition() {
        return Game.getViewportScale();
    }

    public static void setZoomPosition(int percent) {

    }

    public static ScreenBrightness getScreenBrightness() {
        return ScreenBrightness.values()[Game.getSetting(166) - 1];
    }

    public static boolean setScreenBrightness(ScreenBrightness brightness) {
        ScreenBrightness current = getScreenBrightness();
        if (current == brightness) return true;

        if (GameTab.open(GameTab.TABS.OPTIONS) && OptionTab.DISPLAY.open()) {
            return brightness.select();
        }

        return false;
    }


    public enum ScreenBrightness {
        VERY_LOW(1), LOW(2), MEDIUM(3), HIGH(4);

        int setting;
        ScreenBrightness(int setting) {
            this.setting = setting;
        }

        public boolean select() {
            RSInterface slider = Entities.find(InterfaceEntity::new)
                    .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, Settings.LIGHTING_COMPONENT + ordinal())
                    .getFirstResult();

            return Interact.with(InterfacePrefab::new)
                    .set(slider)
                    .action()
                    .waitFor(() -> getScreenBrightness() == this, 750, 1250)
                    .execute();
        }
    }

    public static boolean isAdvancedOptionsOpen() {
        return Entities.find(InterfaceEntity::new)
                .inMaster(60)
                .getFirstResult() != null;
    }

    public static boolean closeAdvancedOptions() {
        if (!isAdvancedOptionsOpen()) return true;

        RSInterface x = Entities.find(InterfaceEntity::new)
                .inMasterAndChild(Settings.ADVANCED_OPTIONS_MASTER, 2, 11)
                .getFirstResult();

        return Interact.with(InterfacePrefab::new)
                .set(x)
                .action()
                .waitFor(() -> !isAdvancedOptionsOpen(), 750, 1250)
                .execute();
    }

    public static boolean openAdvancedOptions() {
        if (isAdvancedOptionsOpen()) return true;

        if (GameTab.open(GameTab.TABS.OPTIONS) && OptionTab.DISPLAY.open()) {
            RSInterface buttton = Entities.find(InterfaceEntity::new)
                    .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, Settings.ADVANCED_OPTIONS_BUTTON)
                    .getFirstResult();

            return Interact.with(InterfacePrefab::new)
                    .set(buttton)
                    .action()
                    .waitFor(DisplaySettings::isAdvancedOptionsOpen, 750, 1250)
                    .execute();
        }

        return false;
    }

    public enum AdvancedOptions {
        TRANSPARENT_SIDE_PANEL(4, () -> (Game.getSetting(1055) & 1024) == 1024),
        REMAINING_XP(6, () -> (Game.getSetting(427) & 2) == 2),
        REMOVE_ROOFS(8, () -> !Options.isRemoveRoofsOn()),
        DATA_ORBS(10, () -> (Game.getSetting(1055) & 8) == 8),
        TRANSPARENT_CHATBOX(12, () -> (Game.getSetting(1055) & 512) == 512),
        SIDE_PANELS(15, () -> (Game.getSetting(1055) & 256) == 256);

        int component;
        BooleanLambda lambda;
        AdvancedOptions(int component, BooleanLambda lambda) {
            this.component = component;
            this.lambda = lambda;
        }

        public boolean isOn() {
            return lambda != null && lambda.active();
        }

        public boolean set(boolean turnOn) {
            boolean current = isOn();
            if (current == turnOn) return true;

            if (openAdvancedOptions()) {
                RSInterface option = Entities.find(InterfaceEntity::new)
                        .inMasterAndChild(Settings.ADVANCED_OPTIONS_MASTER, component)
                        .getFirstResult();

                Interact.with(InterfacePrefab::new)
                        .set(option)
                        .action()
                        .waitFor(this::isOn, 750, 1250)
                        .execute();
            }

            return isOn();
        }
    }

}
