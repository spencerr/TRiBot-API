package scripts.api.definitions;

import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import scripts.api.pattern.Equalable;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Spencer on 9/29/2017.
 */
public enum Rocks implements Equalable {

    CLAY(434, 6705, 6589),
    COPPER(436, 4645, 4510, 3776),
    TIN(438, 53, 57),
    IRON(440, 2576),
    SILVER(442, 7366),
    COAL(453, 7580),
    GOLD(444, 8885, 8128),
    MITHRIL(447, -22239),
    ADAMANTITE(449, 21662),
    RUNITE(451, -31437);

    private int[] colors;
    private int itemId;
    private HashSet<Integer> ids = new HashSet<>();

    Rocks(int itemId, int... colors) {
        this.colors = colors;
        this.itemId = itemId;
    }

    public int[] getColors() {
        return colors;
    }

    public List<Integer> getColorList() {
        return IntStream.of(getColors()).boxed().collect(Collectors.toList());
    }

    public int getItemId() {
        return itemId;
    }

    public HashSet<Integer> getIds() {
        return ids;
    }

    @Override
    public boolean is(Object obj) {
        if (obj instanceof RSObject) {
            RSObject rsObject = (RSObject) obj;

            int id = rsObject.getID();
            if (ids.contains(id)) return true;

            RSObjectDefinition def = rsObject.getDefinition();
            if (def != null) {
                short[] colors = def.getModifiedColors();
                if (IntStream.range(0, colors.length).mapToObj(i -> colors[i]).anyMatch(c -> getColorList().contains((int) c))) {
                    ids.add(id);
                    return true;
                }
            }
        }

        return false;
    }
}
