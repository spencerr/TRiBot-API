package scripts.api.settings;

import org.tribot.api2007.GameTab;
import org.tribot.api2007.types.RSInterface;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;
import scripts.api.util.InterfaceUtil;

/**
 * Created by Spencer on 8/17/2017.
 */
public enum OptionTab {
    DISPLAY(0), SOUND(2), CHAT(4), CONTROLS(6), ANY(-1);
    int component;
    OptionTab(int component) {
        this.component = component;
    }

    public int getComponent() {
        return component;
    }

    public boolean isOpen() {
        if (component == -1) return true;
        RSInterface button = InterfaceUtil.get(Settings.OPTION_INTERFACE_MASTER, Settings.OPTION_TAB_INTERFACE, getComponent());
        return GameTab.getOpen() == GameTab.TABS.OPTIONS && button != null && button.getTextureID() == 762;
    }

    public boolean open() {
        Settings.closeAll();

        if (isOpen()) return true;
        RSInterface button = Entities.find(InterfaceEntity::new)
                .inMasterAndChild(Settings.OPTION_INTERFACE_MASTER, Settings.OPTION_TAB_INTERFACE, getComponent())
                .getFirstResult();

        return GameTab.open(GameTab.TABS.OPTIONS) && Interact.with(InterfacePrefab::new)
                .set(button)
                .action()
                .waitFor(this::isOpen, 750, 1250)
                .execute();
    }
}
