package scripts.api.entityselector.finders;

import org.tribot.api.interfaces.Clickable07;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.Sorting;
import org.tribot.api2007.Game;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSTile;

/**
 * @author Laniax
 */
public abstract class PositionableFinder<T extends Positionable & Clickable07, S> extends Finder<T, S> {

    private boolean sortAscending;
    private Positionable sortPositionable;

    /**
     * Generates a filter which will only return entities who are inside buildings.
     * ONLY WORKS IF THE ENTITY IS INSIDE THE CURRENTLY LOADED REGION
     * @return
     */
    @SuppressWarnings("unchecked")
    public S isIndoors() {

        PositionableFinder self = this;

        filters.add(new Filter<T>() {
            @Override
            public boolean accept(T t) {
                return self.isInsideBuilding(t);
            }
        });

        return (S)this;
    }

    /**
     * Generates a filter which will only return entities who are outside buildings.
     * ONLY WORKS IF THE ENTITY IS INSIDE THE CURRENTLY LOADED REGION
     * @return
     */
    @SuppressWarnings("unchecked")
    public S isOutdoors() {

        PositionableFinder self = this;

        filters.add(new Filter<T>() {
            @Override
            public boolean accept(T t) {
                return !self.isInsideBuilding(t);
            }
        });

        return (S)this;
    }

    private boolean isInsideBuilding(Positionable positionable) {

        RSTile tile = positionable.getPosition();

        if (tile == null)
            return false;

        tile = tile.toLocalTile();

        int x = tile.getX();
        int y = tile.getY();

        if (x > 0 && x < 104 && y > 0 && y < 104)
            return Game.getSceneFlags()[Game.getPlane()][x][y] >= 4;

        return false;
    }

    /**
     * {@link Sorting#sortByDistance(Positionable[], Positionable, boolean)}
     *
     * Defaults to Player.getPosition() and true (ascending).
     */
    @SuppressWarnings("unchecked")
    public S sortByDistance() {
        sortByDistance(true);
        return (S)this;
    }

    /**
     * {@link Sorting#sortByDistance(Positionable[], Positionable, boolean)}
     *
     * Defaults to Player.getPosition().
     */
    @SuppressWarnings("unchecked")
    public S sortByDistance(boolean ascending) {
        setSortParameters(Player.getPosition(), ascending);
        return (S)this;
    }

    /**
     * {@link Sorting#sortByDistance(Positionable[], Positionable, boolean)}
     *
     * Defaults to true (ascending)
     */
    @SuppressWarnings("unchecked")
    public S sortByDistance(Positionable positionable) {
        setSortParameters(positionable, true);
        return (S)this;
    }

    /**
     * {@link Sorting#sortByDistance(Positionable[], Positionable, boolean)}
     */
    @SuppressWarnings("unchecked")
    public S sortByDistance(Positionable positionable, boolean ascending) {
        setSortParameters(positionable, ascending);
        return (S)this;
    }

    protected boolean shouldSortResults() {
        return this.sortPositionable != null;
    }

    private void setSortParameters(Positionable positionable, boolean ascending) {
        this.sortAscending = ascending;
        this.sortPositionable = positionable;
    }

    protected void sortByDistance(Positionable[] entities) {
        Sorting.sortByDistance(entities, this.sortPositionable, this.sortAscending);
        //return entities;
    }
}