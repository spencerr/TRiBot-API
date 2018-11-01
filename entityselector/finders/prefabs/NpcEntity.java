package scripts.api.entityselector.finders.prefabs;

import org.tribot.api.interfaces.Positionable;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSNPC;
import scripts.api.entityselector.finders.PositionableFinder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Laniax
 */
public class NpcEntity extends PositionableFinder<RSNPC, NpcEntity> {

    /**
     * {@link Filters.NPCs#idEquals}
     */
    public NpcEntity idEquals(int... id) {
        filters.add(Filters.NPCs.idEquals(id));
        return this;
    }

    /**
     * {@link Filters.NPCs#idNotEquals}
     */
    public NpcEntity idNotEquals(int... id) {
        filters.add(Filters.NPCs.idNotEquals(id));
        return this;
    }

    /**
     * {@link Filters.NPCs#actionsContains}
     */
    public NpcEntity actionsContains(String... actions) {
        filters.add(Filters.NPCs.actionsContains(actions));
        return this;
    }

    /**
     * {@link Filters.NPCs#actionsEquals}
     */
    public NpcEntity actionsEquals(String... actions) {
        filters.add(Filters.NPCs.actionsEquals(actions));
        return this;
    }

    /**
     * {@link Filters.NPCs#actionsNotContains}
     */
    public NpcEntity actionsNotContains(String... actions) {
        filters.add(Filters.NPCs.actionsNotContains(actions));
        return this;
    }

    /**
     * {@link Filters.NPCs#actionsNotEquals}
     */
    public NpcEntity actionsNotEquals(String... actions) {
        filters.add(Filters.NPCs.actionsNotEquals(actions));
        return this;
    }

    /**
     * {@link Filters.NPCs#inArea}
     */
    public NpcEntity inArea(RSArea area) {
        filters.add(Filters.NPCs.inArea(area));
        return this;
    }

    /**
     * {@link Filters.NPCs#notInArea}
     */
    public NpcEntity notInArea(RSArea area) {
        filters.add(Filters.NPCs.notInArea(area));
        return this;
    }

    /**
     * {@link Filters.NPCs#modelIndexCount}
     */
    public NpcEntity modelIndexCount(int... counts) {
        filters.add(Filters.NPCs.modelIndexCount(counts));
        return this;
    }

    /**
     * {@link Filters.NPCs#modelVertexCount}
     */
    public NpcEntity modelVertexCount(int... counts) {
        filters.add(Filters.NPCs.modelVertexCount(counts));
        return this;
    }

    /**
     * {@link Filters.NPCs#nameContains}
     */
    public NpcEntity nameContains(String... names) {
        filters.add(Filters.NPCs.nameContains(names));
        return this;
    }

    /**
     * {@link Filters.NPCs#nameNotContains}
     */
    public NpcEntity nameNotContains(String... names) {
        filters.add(Filters.NPCs.nameNotContains(names));
        return this;
    }

    /**
     * {@link Filters.NPCs#nameEquals}
     */
    public NpcEntity nameEquals(String... names) {
        filters.add(Filters.NPCs.nameEquals(names));
        return this;
    }

    /**
     * {@link Filters.NPCs#nameNotEquals}
     */
    public NpcEntity nameNotEquals(String... names) {
        filters.add(Filters.NPCs.nameNotEquals(names));
        return this;
    }

    /**
     * {@link Filters.NPCs#tileEquals}
     */
    public NpcEntity tileEquals(Positionable positionable) {
        filters.add(Filters.NPCs.tileEquals(positionable));
        return this;
    }

    /**
     * {@link Filters.NPCs#tileNotEquals}
     */
    public NpcEntity tileNotEquals(Positionable positionable) {
        filters.add(Filters.NPCs.tileNotEquals(positionable));
        return this;
    }

    public NpcEntity notInCombat() {
        filters.add(new Filter<RSNPC>() {
            @Override
            public boolean accept(RSNPC rsnpc) {
                return !rsnpc.isInCombat();
            }
        });

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public RSNPC[] getResults() {

        Filter<RSNPC> filter = super.buildFilter();

        RSNPC[] npcs = NPCs.getAll(filter);

        if (super.shouldSortResults() && npcs.length > 1)
            super.sortByDistance(npcs);

        return npcs;
    }

    @Override
    public ArrayList<RSNPC> getResultList() {
        return new ArrayList<>(Arrays.asList(getResults()));
    }
}