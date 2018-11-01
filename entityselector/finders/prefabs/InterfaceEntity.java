package scripts.api.entityselector.finders.prefabs;

import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceMaster;
import scripts.api.entityselector.finders.FinderResult;
import scripts.api.entityselector.finders.Utils;
import scripts.api.util.InterfaceUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Laniax
 */
public class InterfaceEntity extends FinderResult<RSInterface> {

    /**
     * Since there are no interface filters, we have to make our own.
     */

    private List<Predicate<RSInterface>> filters;

    private Integer selectedMaster;
    private Integer[] selectedChild;
    private Integer selectedComponent;

    public InterfaceEntity() {
        filters = new ArrayList<>();
    }

    /**
     * Finds all the interfaces contained inside the specified master.
     * @param master
     */
    public InterfaceEntity inMaster(int master) {
        this.selectedMaster = master;
        return this;
    }

    /**
     * Finds all the interfaces contained inside the specified master and children.
     * @param master
     */
    public InterfaceEntity inMasterAndChild(int master, Integer... child) {
        this.selectedMaster = master;
        this.selectedChild = child;
        return this;
    }

    public InterfaceEntity custom(Predicate<RSInterface> predicate) {
        filters.add(predicate);
        return this;
    }

    /**
     * Finds all the interfaces whose text equals one of the specified texts.
     * Case insensitive.
     *
     * @param text
     * @return
     */
    public InterfaceEntity componentNameEquals(String... text) {

        filters.add((rsInterface) -> {

            String interfaceText = rsInterface.getComponentName();

            if (interfaceText == null)
                return false;

            List searchList = Utils.stringListToLowercase(Arrays.asList(text));

            return searchList.contains(interfaceText.toLowerCase());
        });

        return this;
    }

    /**
     * Finds all the interfaces whose text do not equal any of the specified texts.
     * Case insensitive.
     *
     * @param text
     * @return
     */
    public InterfaceEntity componentNameNotEquals(String... text) {

        filters.add((rsInterface) -> {

            String interfaceText = rsInterface.getComponentName();

            if (interfaceText == null)
                return false;

            List searchList = Utils.stringListToLowercase(Arrays.asList(text));

            return !searchList.contains(interfaceText.toLowerCase());
        });

        return this;
    }

    /**
     * Finds all the interfaces whose text contain one of the specified texts.
     * Case insensitive.
     *
     * @param text
     * @return
     */
    public InterfaceEntity componentNameContains(String... text) {

        filters.add((rsInterface) -> {

            String interfaceText = rsInterface.getComponentName();

            if (interfaceText == null)
                return false;

            interfaceText = interfaceText.toLowerCase();

            for (String t : text) {
                if (interfaceText.contains(t.toLowerCase()))
                    return true;
            }

            return false;
        });

        return this;
    }

    /**
     * Finds all the interfaces whose text do not contain any of the specified texts.
     * Case insensitive.
     *
     * @param text
     * @return
     */
    public InterfaceEntity componentNameNotContains(String... text) {

        filters.add((rsInterface) -> {

            String interfaceText = rsInterface.getComponentName();

            if (interfaceText == null)
                return false;

            interfaceText = interfaceText.toLowerCase();

            for (String t : text) {
                if (interfaceText.contains(t.toLowerCase()))
                    return false;
            }

            return true;
        });

        return this;
    }


    /**
     * Finds all the interfaces whose text equals one of the specified texts.
     * Case insensitive.
     *
     * @param text
     * @return
     */
    public InterfaceEntity textEquals(String... text) {

        filters.add((rsInterface) -> {

            String interfaceText = rsInterface.getText();

            if (interfaceText == null)
                return false;

            List searchList = Utils.stringListToLowercase(Arrays.asList(text));

            return searchList.contains(interfaceText.toLowerCase());
        });

        return this;
    }

    /**
     * Finds all the interfaces whose text do not equal any of the specified texts.
     * Case insensitive.
     *
     * @param text
     * @return
     */
    public InterfaceEntity textNotEquals(String... text) {

        filters.add((rsInterface) -> {

            String interfaceText = rsInterface.getText();

            if (interfaceText == null)
                return false;

            List searchList = Utils.stringListToLowercase(Arrays.asList(text));

            return !searchList.contains(interfaceText.toLowerCase());
        });

        return this;
    }

    /**
     * Finds all the interfaces whose text contain one of the specified texts.
     * Case insensitive.
     *
     * @param text
     * @return
     */
    public InterfaceEntity textContains(String... text) {

        filters.add((rsInterface) -> {

            String interfaceText = rsInterface.getText();

            if (interfaceText == null)
                return false;

            interfaceText = interfaceText.toLowerCase();

            for (String t : text) {
                if (interfaceText.contains(t.toLowerCase()))
                    return true;
            }

            return false;
        });

        return this;
    }

    /**
     * Finds all the interfaces whose text do not contain any of the specified texts.
     * Case insensitive.
     *
     * @param text
     * @return
     */
    public InterfaceEntity textNotContains(String... text) {

        filters.add((rsInterface) -> {

            String interfaceText = rsInterface.getText();

            if (interfaceText == null)
                return false;

            interfaceText = interfaceText.toLowerCase();

            for (String t : text) {
                if (interfaceText.contains(t.toLowerCase()))
                    return false;
            }

            return true;
        });

        return this;
    }

    /**
     * Finds all the interfaces whose action equals one of the specified actions.
     * Case insensitive.
     *
     * @param actions
     * @return
     */
    public InterfaceEntity actionEquals(String... actions) {

        filters.add((rsInterface) -> {

            String[] interfaceActions = rsInterface.getActions();

            if (interfaceActions == null)
                return false;

            List searchList = Utils.stringListToLowercase(Arrays.asList(actions));

            for (String action : interfaceActions) {

                // Weird, looks like RSInterface#getActions() might return an array with null values.
                if (action == null)
                    continue;

                if (searchList.contains(action.toLowerCase()))
                    return true;
            }

            return false;
        });

        return this;
    }

    /**
     * Finds all the interfaces whose actions are not equal to any of the specified actions.
     * Case insensitive.
     *
     * @param actions
     * @return
     */
    public InterfaceEntity actionNotEquals(String... actions) {

        filters.add((rsInterface) -> {

            String[] interfaceActions = rsInterface.getActions();

            if (interfaceActions == null)
                return false;

            List searchList = Utils.stringListToLowercase(Arrays.asList(actions));

            for (String action : interfaceActions) {

                // Weird, looks like RSInterface#getActions() might return an array with null values.
                if (action == null)
                    continue;

                if (searchList.contains(action.toLowerCase()))
                    return false;
            }

            return true;
        });

        return this;
    }

    /**
     * Finds all the interfaces whose action contains one of the specified actions.
     * Case insensitive.
     *
     * @param actions
     * @return
     */
    public InterfaceEntity actionContains(String... actions) {

        filters.add((rsInterface) -> {

            String[] interfaceActions = rsInterface.getActions();

            if (interfaceActions == null || interfaceActions.length == 0)
                return false;

            for (String interfaceAction : interfaceActions) {

                // Weird, looks like RSInterface#getActions() might return an array with null values.
                if (interfaceAction == null)
                    continue;

                String checkAction = interfaceAction.toLowerCase();

                for (String action : actions) {

                    if (checkAction.contains(action.toLowerCase()))
                        return true;

                }

            }

            return false;
        });

        return this;
    }

    /**
     * Finds all the interfaces whose action do not contain any of the specified actions.
     * Case insensitive.
     *
     * @param actions
     * @return
     */
    public InterfaceEntity actionNotContains(String... actions) {

        filters.add((rsInterface) -> {

            String[] interfaceActions = rsInterface.getActions();

            if (interfaceActions == null)
                return false;

            for (String interfaceAction : interfaceActions) {

                // Weird, looks like RSInterface#getActions() might return an array with null values.
                if (interfaceAction == null)
                    continue;

                for (String action : actions) {

                    if (interfaceAction.toLowerCase().contains(action.toLowerCase()))
                        return false;

                }

            }

            return true;
        });

        return this;
    }

    /**
     * Finds all the interfaces whose text colour equals any of the specified colours.
     *
     * @param colours
     * @return
     */
    public InterfaceEntity textColourEquals(int... colours) {

        filters.add((rsInterface) -> {

            int textColour = rsInterface.getTextColour();

            return Arrays.stream(colours).anyMatch(i -> i == textColour);
        });

        return this;
    }

    /**
     * Finds all the interfaces whose text colour does not equals any of the specified colours.
     *
     * @param colours
     * @return
     */
    public InterfaceEntity textColourNotEquals(int... colours) {

        filters.add((rsInterface) -> {

            int textColour = rsInterface.getTextColour();

            return !Arrays.stream(colours).anyMatch(i -> i == textColour);
        });

        return this;
    }

    /**
     * Finds all the interfaces whose texture id equals any of the specified ids.
     *
     * @param textureIds
     * @return
     */
    public InterfaceEntity textureIdEquals(int... textureIds) {

        filters.add((rsInterface) -> {

            int textureId = rsInterface.getTextureID();

            return Arrays.stream(textureIds).anyMatch(i -> i == textureId);
        });

        return this;
    }

    /**
     * Finds all the interfaces whose texture id does not equal any of the specified ids.
     *
     * @param textureIds
     * @return
     */
    public InterfaceEntity textureIdNotEquals(int... textureIds) {

        filters.add((rsInterface) -> {

            int textureId = rsInterface.getTextureID();

            return !Arrays.stream(textureIds).anyMatch(i -> i == textureId);
        });

        return this;
    }

    /**
     * Finds all interfaces which are hidden.
     * @return
     */
    public InterfaceEntity isHidden() {

        filters.add(RSInterface::isHidden);

        return this;
    }

    /**
     * Finds all interfaces which are hidden.
     * @return
     */
    public InterfaceEntity isNotHidden() {

        filters.add((rsInterface) -> !rsInterface.isHidden());

        return this;
    }

    /**
     * Finds all interfaces which are hidden.
     * {@link RSInterface#isHidden(boolean)}
     * @param recursive
     * @return
     */
    public InterfaceEntity isHidden(boolean recursive) {

        filters.add((rsInterface) -> rsInterface.isHidden(recursive));

        return this;
    }

    /**
     * Finds all interfaces which are hidden.
     * {@link RSInterface#isHidden(boolean)}
     * @param recursive
     * @return
     */
    public InterfaceEntity isNotHidden(boolean recursive) {

        filters.add((rsInterface) -> !rsInterface.isHidden(recursive));

        return this;
    }

    /**
     * Finds all interfaces which are being drawn.
     * @return
     */
    public InterfaceEntity isBeingDrawn() {

        filters.add(RSInterface::isBeingDrawn);

        return this;
    }

    /**
     * Finds all interfaces which are not drawn.
     * @return
     */
    public InterfaceEntity isNotBeingDrawn() {

        filters.add((rsInterface) -> !rsInterface.isBeingDrawn());

        return this;
    }

    /**
     * Finds all interfaces which are valid.
     * @return
     */
    public InterfaceEntity isValid() {

        filters.add((rsInterface) -> Interfaces.isInterfaceValid(rsInterface.getIndex()));

        return this;
    }

    /**
     * Finds all interfaces which are not valid.
     * @return
     */
    public InterfaceEntity isNotValid() {

        filters.add((rsInterface) -> !Interfaces.isInterfaceValid(rsInterface.getIndex()));

        return this;
    }

    private List<RSInterface> addAllSubInterfaces(List<RSInterface> list, RSInterface parent) {

        // While recursing the getChildren call would be the cleanest.
        // Doing type checks and adding collections is approx 40-50% faster.

        if (parent instanceof RSInterfaceMaster) {
            RSInterface[] children = ((RSInterfaceMaster)parent).getChildren();

            if (children != null) {

                for (RSInterface child : children) {

                    list.add(child);

                    addAllSubInterfaces(list, child);
                }
            }
        }

        if (parent instanceof RSInterfaceChild) {
            RSInterface[] components = ((RSInterfaceChild)parent).getChildren();

            if (components != null) {
                list.addAll(Arrays.asList(components));
            }
        }

        return list;
    }

    /**
     * {@inheritDoc}
     */
    public RSInterface[] getResults() {

        // First we determine the payload.
        // ie. the interfaces to search over.

        List<RSInterface> payload = new ArrayList<>();

        // If a master was set
        if (selectedMaster != null) {

            // If a child was set as well. We will search over the child and its components.
            if (selectedChild != null) {
                RSInterface child = InterfaceUtil.get(Interfaces.get(selectedMaster), Arrays.stream(selectedChild).mapToInt(Integer::intValue).toArray());
                if (child != null) {
                    payload.add(child);
                    addAllSubInterfaces(payload, child);
                }

            } else {
                // Only a master was set? fine. We search over it, its children and the children's components.
                RSInterface master = Interfaces.get(selectedMaster);

                if (master != null) {

                    payload.add(master);

                    addAllSubInterfaces(payload, master);
                }
            }
        }

        // If we didn't specify a master and optionally a child. We simply search over everything
        if (selectedMaster == null) {

            for (RSInterface master : Interfaces.getAll()) {

                payload.add(master);

                addAllSubInterfaces(payload, master);
            }
        }

        // We have a payload. Lets apply all our filters.
        Stream<RSInterface> stream;

        if (filters.size() > 0) {
            stream = payload.stream().filter(
                    filters.stream().reduce(Predicate::and).orElse(t -> false)
            );
        } else {
            stream = payload.stream();
        }

        // And return the gold ;)
        return stream.toArray(RSInterface[]::new);
    }

    @Override
    public ArrayList<RSInterface> getResultList() {
        return new ArrayList<>(Arrays.asList(getResults()));
    }
}