package scripts.api.util;

import org.tribot.api.input.Mouse;
import org.tribot.api2007.Game;
import scripts.api.data.Bag;
import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.types.generic.Filter;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.types.RSInterface;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Spencer on 8/10/2016.
 */
public class InterfaceUtil {
    private static Bag bag = new Bag();

    public static boolean isUptext(final String... texts) {
        String uptext = Game.getUptext();
        if (uptext == null || uptext.equals("")) return false;
        for (String text : texts) {
            if (uptext.contains(text)) return true;
        }

        return false;
    }

    public static boolean inBounds(final RSInterface rsInterface) {
        if (rsInterface == null) return false;
        Rectangle bounds = rsInterface.getAbsoluteBounds();
        return isOpen(rsInterface) && bounds != null && bounds.contains(Mouse.getPos());
    }

    public static boolean isClosed(final int... xpath) {
        return !isOpen(xpath);
    }

    public static boolean isOpen(final int... xpath) {
        return isOpen(get(xpath));
    }

    public static boolean isOpen(final RSInterface rsInterface) {
        return rsInterface != null && !rsInterface.isHidden();
    }

    public static RSInterface get(final int... xpath) {
        return xpath.length == 0 ? null : get(Interfaces.get(xpath[0]), Arrays.copyOfRange(xpath, 1, xpath.length));
    }

    public static RSInterface get(final RSInterface rsInterface, final int... xpath) {
        return rsInterface == null || xpath == null || xpath.length == 0 ? rsInterface : get(rsInterface.getChild(xpath[0]), Arrays.copyOfRange(xpath, 1, xpath.length));
    }

    public static boolean click(final RSInterface rsInterface, String... options) {
        if (rsInterface == null) return false;

        return Clicking.click(options, rsInterface);
    }

    private static int[] getXPath(RSInterface rsInterface) {
        if (rsInterface == null) return new int[0];

        ArrayList<Integer> path = new ArrayList<>();

        RSInterface master = rsInterface;
        do {
            path.add(0, master.getIndex());
            master = master.getParent();
        } while (master != null);

        int[] arr = new int[path.size()];
        for (int i = 0; i < path.size(); i++) {
            arr[i] = path.get(i);
        }

        return arr;
    }

    public static int getMaster(RSInterface rsInterface) {
        int[] xpath = getXPath(rsInterface);
        return xpath.length > 0 ? xpath[0] : -1;
    }

    public static RSInterface[] find(Filter<RSInterface> filter) {
        return matches(filter, Interfaces.getAll());
    }

    private static RSInterface[] matches(Filter<RSInterface> filter, RSInterface[] interfaces) {
        ArrayList<RSInterface> matches = new ArrayList<>();
        for (RSInterface i : interfaces) {
            if (i != null) {
                if (filter.accept(i))
                    matches.add(i);

                RSInterface[] children = i.getChildren();
                if (children != null)
                    matches.addAll(Arrays.asList(matches(filter, children)));
            }
        }
        return matches.toArray(new RSInterface[matches.size()]);

    }

    public static boolean isOpen(String text) {
        return find(new Filter<RSInterface>() {
            @Override
            public boolean accept(RSInterface rsInterface) {
                String rsInterfaceText = rsInterface.getText();
                return rsInterfaceText != null && rsInterfaceText.contains(text) && !rsInterface.isHidden();
            }
        }).length > 0;
    }

    public static boolean isOpenCached(String text) {
        if (bag.get(text, new int[0]).length == 0) {
            RSInterface rsInterface = findFirst(new Filter<RSInterface>() {
                @Override
                public boolean accept(RSInterface rsInterface) {
                    String rsInterfaceText = rsInterface.getText();
                    return rsInterfaceText != null && rsInterfaceText.contains(text) && !rsInterface.isHidden() && rsInterface.isBeingDrawn();
                }
            });

            if (rsInterface != null) {
                bag.addOrUpdate(text, getXPath(rsInterface));
                return true;
            }

            return false;
        } else {
            return isOpen(bag.get(text, new int[0]));
        }

    }

    public static RSInterface findFirst(Filter<RSInterface> filter) {
        return matchFirst(filter, Interfaces.getAll());
    }

    public static RSInterface matchFirst(Filter<RSInterface> filter, RSInterface[] interfaces) {
        for (RSInterface rsInterface : interfaces) {
            if (rsInterface != null) {
                if (filter.accept(rsInterface))
                    return rsInterface;

                RSInterface[] children = rsInterface.getChildren();
                if (children != null) {
                    RSInterface match = matchFirst(filter, children);
                    if (match != null) return match;
                }
            }
        }

        return null;
    }

    public static RSInterface getWithText(String text) {
        RSInterface[] rsInterfaces = find(new Filter<RSInterface>() {
            @Override
            public boolean accept(RSInterface rsInterface) {
                return rsInterface.getText() != null && rsInterface.getText().contains(text) && !rsInterface.isHidden();
            }
        });

        return rsInterfaces.length > 0 ? rsInterfaces[0] : null;
    }

    public static RSInterface checkForAction(String action) {
        RSInterface[] rsInterfaces = find(new Filter<RSInterface>() {
            @Override
            public boolean accept(RSInterface rsInterface) {
                return rsInterface.getActions() != null && Utilities.contains(rsInterface.getActions(), action);
            }
        });

        return rsInterfaces.length > 0 ? rsInterfaces[0] : null;
    }

    public static RSInterface getActionFromChildren(String text, RSInterface rsInterface) {
        if (rsInterface == null) return null;
        final RSInterface[] rsInterfaces = rsInterface.getChildren();
        if (rsInterfaces == null || rsInterfaces.length == 0) return null;
        for (RSInterface r : rsInterfaces) {
            if (r != null && !r.isHidden()) {
                if (r.getActions() != null && r.getActions().length > 0)
                    if (stringArrayContains(r.getActions(), text)) {
                        return r;
                    }

                RSInterface rChild = getActionFromChildren(text, r);
                if (rChild != null) return rChild;
            }
        }

        return null;
    }

    private static boolean stringArrayContains(String[] array, String text) {
        if (array == null || array.length == 0) return false;

        for (String item : array) {
            if (item.contains(text)) return true;
        }

        return false;
    }

    public static RSInterface findWithAction(String... text) {
        for (String t : text) {
            if (bag.get(t, new int[] {}).length > 0) {
                General.println("Checking cached interface");
                return get(bag.get(t, new int[]{}));
            }

            RSInterface rsInterface = checkForAction(t);
            if (rsInterface != null) {
                bag.addOrUpdate(t, getXPath(rsInterface));
                return rsInterface;
            }
        }

        return null;
    }
}
