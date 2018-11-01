package scripts.api.settings;

import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSInterface;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;
import scripts.api.pattern.BooleanLambda;
import scripts.api.util.InterfaceUtil;

/**
 * Created by Spencer on 8/18/2017.
 */
public class ControlSettings {

    public static boolean closeKeybinds() {
        if (!isKeybindsOpen()) return true;

        RSInterface closeButton = Entities.find(InterfaceEntity::new)
                .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, 1, 11)
                .getFirstResult();

        return Interact.with(InterfacePrefab::new)
                .set(closeButton)
                .action()
                .waitFor(() -> !isKeybindsOpen(), 750, 1250)
                .execute();
    }

    public static boolean openKeybinds() {
        if (isKeybindsOpen()) return true;

        if (OptionTab.CONTROLS.open()) {
            RSInterface keybinding = Entities.find(InterfaceEntity::new)
                    .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, 63)
                    .getFirstResult();

            Interact.with(InterfacePrefab::new)
                    .set(keybinding)
                    .action()
                    .waitFor(ControlSettings::isKeybindsOpen, 750, 1250)
                    .execute();
        }

        return isKeybindsOpen();
    }

    public static boolean isKeybindsOpen() {
        return Entities.find(InterfaceEntity::new)
                .inMaster(121)
                .getFirstResult() != null;
    }

    public static boolean setUseEscape(boolean useEscape) {
        if (useEscape != isUsingEscape()) {
            if (openKeybinds()) {
                RSInterface escapeButton = Entities.find(InterfaceEntity::new)
                        .inMasterAndChild(121, 103)
                        .getFirstResult();

                Interact.with(InterfacePrefab::new)
                        .set(escapeButton)
                        .action()
                        .waitFor(() -> useEscape == isUsingEscape(), 750, 1250)
                        .execute();

            }
        }

        return useEscape == isUsingEscape();
    }

    public static boolean isUsingEscape() {
        return (Game.getSetting(1224) & -2147483648) == -2147483648;
    }

    public enum ControlOptions {
        TWO_MOUSE_BUTTONS(57, () -> Game.getSetting(170) == 0),
        MOUSE_CAMERA(59, () -> (Game.getSetting(1055) & 32) != 32),
        FOLLOWER_OPTIONS(61,  () -> (Game.getSetting(1055) & 262144) == 262144),
        SHIFT_DROP(65, () -> (Game.getSetting(1055) & 131072) == 131072);

        int component;
        BooleanLambda lambda;

        ControlOptions(int component, BooleanLambda lambda) {
            this.component = component;
            this.lambda = lambda;
        }

        public boolean isEnabled() {
            return lambda != null && lambda.active();
        }

        public boolean set(boolean enable) {
            if (enable == isEnabled()) return true;

            if (OptionTab.CONTROLS.open()) {
                RSInterface button = Entities.find(InterfaceEntity::new)
                        .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, component)
                        .getFirstResult();

                return Interact.with(InterfacePrefab::new)
                        .set(button)
                        .action()
                        .waitFor(() -> enable == lambda.active(), 750, 1250)
                        .execute();
            }

            return false;
        }
    }

    public enum AttackOptions {
        DEPENDS_ON_COMBAT, ALWAYS_RIGHT_CLICK, LEFT_CLICK, HIDDEN
    }

    public static AttackOptions getPlayerAttackOption() {
        return AttackOptions.values()[Game.getSetting(1107)];
    }

    public static AttackOptions getNPCAttackOption() {
        return AttackOptions.values()[Game.getSetting(1306)];
    }

    public static boolean setPlayerAttackOption(AttackOptions option) {
        if (option == getPlayerAttackOption()) return true;

        if (OptionTab.CONTROLS.open()) {
            RSInterface attackOption = Entities.find(InterfaceEntity::new)
                    .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, 67)
                    .getFirstResult();

            if (Interact.with(InterfacePrefab::new)
                    .set(attackOption)
                    .action()
                    .waitFor(() -> InterfaceUtil.isOpen(Settings.OPTION_TAB_INTERFACE, 83, 1 + option.ordinal()), 750, 1250)
                    .execute()) {

                RSInterface optionToSelect = Entities.find(InterfaceEntity::new)
                        .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, 83, 1 + option.ordinal())
                        .getFirstResult();

                Interact.with(InterfacePrefab::new)
                        .set(optionToSelect)
                        .action()
                        .waitFor(() -> getPlayerAttackOption() == option, 1250, 1750)
                        .execute();

            }
        }

        return option == getPlayerAttackOption();
    }

    public static boolean setNPCAttackOption(AttackOptions option) {
        if (option == getNPCAttackOption()) return true;

        if (OptionTab.CONTROLS.open()) {
            RSInterface attackOption = Entities.find(InterfaceEntity::new)
                    .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, 68)
                    .getFirstResult();

            if (Interact.with(InterfacePrefab::new)
                    .set(attackOption)
                    .action()
                    .waitFor(() -> InterfaceUtil.isOpen(Settings.OPTION_TAB_INTERFACE, 84, 1 + option.ordinal()), 750, 1250)
                    .execute()) {

                RSInterface optionToSelect = Entities.find(InterfaceEntity::new)
                        .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, 84, 1 + option.ordinal())
                        .getFirstResult();

                Interact.with(InterfacePrefab::new)
                        .set(optionToSelect)
                        .action()
                        .waitFor(() -> getNPCAttackOption() == option, 1250, 1750)
                        .execute();

            }
        }

        return option == getNPCAttackOption();
    }

}
