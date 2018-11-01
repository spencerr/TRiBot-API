package scripts.api.interaction;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by Spencer on 1/27/2017.
 */
public abstract class InteractionResult<T> {

    /**
     * Returns an array with all the results. If no filters were set, this will return ALL the current entities we can find.
     * @return Empty if no results found, never null.
     */
    public abstract List<Callable<Boolean>> getChain();

    /**
     * Returns the first index of the results, if it exists.
     * @return the first result or null if there were none.
     */
    public boolean execute() {

        List<Callable<Boolean>> chain = this.getChain();

        for (Callable<Boolean> callable : chain) {
            try {
                if (!callable.call())
                    return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }
}
