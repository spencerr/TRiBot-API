package scripts.api.settings;

import org.tribot.api2007.Game;
import org.tribot.api2007.types.RSInterface;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;
import scripts.api.pattern.BooleanLambda;

/**
 * Created by Spencer on 8/17/2017.
 */

public enum ChatSettings {
    CHAT_EFFECTS(42, () -> Game.getSetting(171) == 1),
    SPLIT_PRIVATE_CHAT(44, () -> Game.getSetting(287) == 1),
    HIDE_PRIVATE_CHAT(46, null),
    PROFANITY_FILTER(48, () -> Game.getSetting(1074) == 1),
    LOGIN_NOTIFICATIONS(52, () -> (Game.getSetting(1055) & 128) != 128);

    int component;
    BooleanLambda lambda;

    ChatSettings(int component, BooleanLambda lambda) {
        this.component = component;
        this.lambda = lambda;
    }

    public boolean isEnabled() {
        return lambda != null && lambda.active();
    }

    public boolean set(boolean enable) {
        if (enable == isEnabled()) return true;

        if (OptionTab.CHAT.open()) {
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
