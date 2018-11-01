package scripts.api.interaction;

import org.tribot.api.interfaces.Clickable07;
import scripts.api.interaction.prefabs.ClickablePrefab;

import java.util.function.Supplier;

/**
 * Created by Spencer on 1/27/2017.
 */
public class Interact {

    public static <T> T with(Supplier<T> supplier) {
        return supplier.get();
    }

    public static Interactor with(Clickable07 clickable) {
        Supplier<ClickablePrefab> s = ClickablePrefab::new;
        s.get().clickable07 = clickable;
        return (Interactor) s;
    }

}
