package scripts.api.settings;

import org.tribot.api2007.Game;
import org.tribot.api2007.GameTab;
import org.tribot.api2007.types.RSInterface;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;
import scripts.api.pattern.BooleanLambda;

/**
 * Created by Spencer on 8/17/2017.
 */
public class HouseSettings {

    private static final int OPTIONS_INTERFACE_MASTER = 261;
    private static final int HOUSE_INTERFACE_MASTER = 370;
    private static final int HOUSE_OPTION_OPEN_BUTTON = 75;
    private static final int HOUSE_INTERFACE_CLOSE_BUTTON = 17;


    public static boolean isOpen() {
        return Entities.find(InterfaceEntity::new)
                .inMaster(370)
                .getFirstResult() != null;
    }

    public static boolean isClosed() {
        return !isOpen();
    }


    public static boolean open() {
        if (isOpen()) return true;
        if (GameTab.open(GameTab.TABS.OPTIONS)) {
            RSInterface openButton = Entities.find(InterfaceEntity::new)
                    .inMasterAndChild(OPTIONS_INTERFACE_MASTER, HOUSE_OPTION_OPEN_BUTTON)
                    .getFirstResult();

            Interact.with(InterfacePrefab::new)
                    .set(openButton)
                    .action()
                    .waitFor(HouseSettings::isOpen, 750, 1250)
                    .execute();
        }

        return isOpen();
    }

    public static boolean close() {
        if (isClosed()) return true;

        RSInterface closeButton = Entities.find(InterfaceEntity::new)
                .inMasterAndChild(HOUSE_INTERFACE_MASTER, HOUSE_INTERFACE_CLOSE_BUTTON)
                .getFirstResult();

        return Interact.with(InterfacePrefab::new)
                .set(closeButton)
                .action()
                .waitFor(HouseSettings::isClosed, 750, 1250)
                .execute();
    }

    public enum HouseOptions {
        BUILDING_MODE(5, 6, () -> Game.getSetting(-1) == 1),
        RENDER_DOORS(8, 9, () -> (Game.getSetting(1046) & -2147483648) == -2147483648),
        TELEPORT_INSIDE(11, 12, () -> (Game.getSetting(1047) & 8388608) != 8388608),
        EXPELL_GUESTS(13, () -> false),
        LEAVE_HOUSE(14, () -> false),
        CALL_SERVANT(15, () -> false),
        EXIT(17, () -> false);

        int on, off, button;
        BooleanLambda lambda;
        HouseOptions(int on, int off, BooleanLambda lambda) {
            this.on = on;
            this.off = off;
            this.lambda = lambda;
        }

        HouseOptions(int button, BooleanLambda lambda) {
            this.button = button;
            this.lambda = lambda;
        }

        public boolean isEnabled() {
            return lambda != null && lambda.active();
        }

        public boolean set(boolean turnOn) {
            boolean current = isEnabled();
            if (current == turnOn) return true;

            if (HouseSettings.open()) {
                RSInterface option = Entities.find(InterfaceEntity::new)
                        .inMasterAndChild(HOUSE_INTERFACE_MASTER, turnOn ? on : off)
                        .getFirstResult();

                Interact.with(InterfacePrefab::new)
                        .set(option)
                        .action()
                        .waitFor(this::isEnabled, 750, 1250)
                        .execute();
            }

            return isEnabled();
        }

        public boolean click() {
            if (HouseSettings.open()) {
                RSInterface interfaceButton = Entities.find(InterfaceEntity::new)
                        .inMasterAndChild(HOUSE_INTERFACE_MASTER, button)
                        .getFirstResult();

                return Interact.with(InterfacePrefab::new)
                        .set(interfaceButton)
                        .action()
                        .waitFor(lambda, 750, 1250)
                        .execute();
            }

            return false;
        }
    }
}
