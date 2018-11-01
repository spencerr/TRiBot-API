package scripts.api.entityselector;

import java.util.function.Supplier;

/**
 * A easy to use Entity finder.
 * This wraps around tribot's native filters in a very user friendly way.
 *
 * @author Laniax
 */
public class Entities {

    /**
     * Find entities of a given type.
     * @param supplier the type to search for, as a supplier. Ie. {@code NpcEntity::new or PlayerEntity::new} etc
     * @param <T>
     * @return the finder. use {@code #getResults() or #getFirstResult()) on it to get the actual results.
     */
    public static <T> T find(Supplier<T> supplier) {

        return supplier.get();
    }
}