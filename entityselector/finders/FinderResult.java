package scripts.api.entityselector.finders;

import org.tribot.api.General;

import java.util.ArrayList;

/**
 * @author Laniax
 */
public abstract class FinderResult<T> {

    /**
     * Returns an array with all the results. If no filters were set, this will return ALL the current entities we can find.
     * @return Empty if no results found, never null.
     */
    public abstract T[] getResults();
    public abstract ArrayList<T> getResultList();

    /**
     * Returns the first index of the results, if it exists.
     * @return the first result or null if there were none.
     */
    public T getFirstResult() {

        T[] results = this.getResults();

        if (results.length > 0)
            return results[0];

        return null;
    }

    public T getSecondResult() {
        T[] results = this.getResults();
        if (results.length > 1)
            return results[1];

        return null;
    }

    public T getRandom() {
        T[] results = this.getResults();
        if (results.length > 1)
            return results[General.random(0, results.length - 1)];

        return null;
    }
}