package scripts.api.grandexchange;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.types.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GrandExchange {

    /**
     * Gets a list of all Slots
     *
     * @return List of type <SLOT>
     */
    public static List<SLOT> getSlots() {
        return new ArrayList<SLOT>(Arrays.asList(SLOT.values()));
    }

    /**
     * Gets a list of Slots with the specified status
     *
     * @return List of type <SLOT>
     */
    public static List<SLOT> getSlots(STATUS status) {

        List<SLOT> slots = new ArrayList<SLOT>();

        if (!isOpen())
            return slots;

        for (SLOT e : SLOT.values()) {

            if (status == STATUS.AVAILABLE_FOR_COLLECTION) {

                if (SLOT.getStatus(e) == STATUS.ABORTED || SLOT.getStatus(e) == STATUS.COMPLETE)
                    slots.add(e);

            } else if (SLOT.getStatus(e) == status) {

                slots.add(e);

            }

        }

        return slots;

    }

    /**
     * Gets a list of Slots with the specified status
     *
     * @return List of type <SLOT>
     */
    public static List<SLOT> getSlotsExcept(STATUS status) {

        List<SLOT> slots = new ArrayList<SLOT>();

        if (!isOpen())
            return slots;

        for (SLOT e : SLOT.values()) {

            if (status == STATUS.AVAILABLE_FOR_COLLECTION) {

                if (SLOT.getStatus(e) == STATUS.EMPTY || SLOT.getStatus(e) == STATUS.IN_PROGRESS)
                    slots.add(e);

            } else if (SLOT.getStatus(e) != status) {

                slots.add(e);

            }

        }

        return slots;

    }

    /**
     * Opens the Grand Exchange using the clerk.
     *
     * @return true if the Grand Exchange interface was successfully opened.
     */
    public static boolean open() {

        if (isOpen())
            return true;

        RSNPC[] npc = NPCs.findNearest("Grand Exchange Clerk");
        if (npc.length == 0)
            return false;

        if (DynamicClicking.clickRSNPC(npc[0], "Exchange Grand Exchange Clerk")) {

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(100);
                    return isOpen();
                }
            }, 5000);

        }

        return isOpen();

    }

    /**
     * Closes the Grand Exchange interface.
     *
     * @return true if it successfully closed Grand Exchange.
     */
    public static boolean close() {

        if (!isOpen())
            return true;

        RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.CLOSE_CHILD);
        if (child == null)
            return false;

        RSInterfaceComponent component = child.getChild(Constants.CLOSE_COMPONENT);
        if (component == null)
            return false;

        if (component.click()) {

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(100);
                    return !isOpen();
                }
            }, 5000);

        }

        return !isOpen();

    }

    /**
     * Checks if the Grand Exchange interface is open.
     *
     * @return true if the Grand Exchange interface is open.
     *
     */
    public static boolean isOpen() {
        return Interfaces.isInterfaceValid(Constants.MASTER);
    }

    /**
     * Selects the back menu inside the offer window.
     *
     * @return true if we went back to the main Grand Exchange interface.
     */
    public static boolean back() {

        if (!isOfferWindowOpen() && !isViewOfferWindowOpen())
            return true;

        RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.BACK_CHILD);
        if (child == null)
            return false;

        if (child.click()) {

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(100);
                    return !isOfferWindowOpen() && !isViewOfferWindowOpen();
                }
            }, 5000);

        }

        return !isOfferWindowOpen() && !isViewOfferWindowOpen();

    }

    /**
     * Sleeps until the items are collected.
     *
     * @param component
     * @return true if all items are collected
     */
    private static boolean sleepUntilItemCollected(RSInterfaceComponent component) {

        long timer = System.currentTimeMillis() + 3000;

        while (timer > System.currentTimeMillis()) {

            if (component == null)
                return false;

            String text = component.getText();
            if (text == null || text.isEmpty())
                return true;

            General.sleep(100);

        }

        return false;

    }

    /**
     * Sleeps until the items are collected
     *
     * @return true if all items are collected
     */
    private static boolean sleepUntilItemsCollected() {

        long timer = System.currentTimeMillis() + 3000;

        while (timer > System.currentTimeMillis()) {

            if (!canCollectItems())
                return true;

            General.sleep(100);

        }

        return !canCollectItems();

    }

    public static boolean canCollectItems() {

        if (!isOpen())
            return false;

        if (isOfferWindowOpen() || isViewOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.COLLECT_ITEM_CHILD);
            if (child == null)
                return false;

            RSInterfaceComponent[] components = child.getChildren();
            if (components == null || components.length == 0)
                return false;

            for (RSInterfaceComponent component : components) {

                String[] actions = component.getActions();

                if (actions != null)
                    return true;

            }

            return false;

        }

        RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.COLLECT_CHILD);
        if (child == null)
            return false;

        RSInterfaceComponent component = child.getChild(Constants.COLLECT_COMPONENT);

        return component != null && !component.isHidden();

    }

    /**
     * Collects items through the Grand Exchange interface with the specified
     * METHOD, will collect items from the main interface or the offer window
     * depending on which is open when this method is called.
     *
     * @return true if the items were collected.
     */
    public static boolean collect(METHOD method) {

        if (!isOpen())
            return false;

        if (!canCollectItems())
            return false;

        if (isOfferWindowOpen() || isViewOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.COLLECT_ITEM_CHILD);
            if (child == null)
                return false;

            RSInterfaceComponent[] components = child.getChildren();
            if (components == null || components.length == 0)
                return false;

            for (RSInterfaceComponent component : components) {

                if (method == METHOD.DEFAULT) {

                    if (component.click())
                        return sleepUntilItemCollected(component);

                } else {

                    String[] component_actions = component.getActions();

                    if (component_actions != null) {

                        for (String component_action : component_actions) {

                            if (component_action == null) continue;

                            for (String method_action : method.getActions()) {

                                if (method_action == null) continue;

                                if (component_action.equals(method_action)) {

                                    if (component.click(method_action))
                                        return sleepUntilItemCollected(component);

                                }

                            }

                        }

                    }

                }

            }

        } else {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.COLLECT_CHILD);
            if (child == null)
                return false;

            RSInterfaceComponent component = child.getChild(Constants.COLLECT_COMPONENT);
            if (component == null || component.isHidden())
                return false;

            if (method == METHOD.DEFAULT) {

                if (component.click())
                    return sleepUntilItemsCollected();

            } else {

                String[] component_actions = component.getActions();

                for (String component_action : component_actions) {

                    for (String method_action : method.getActions()) {

                        if (component_action.equals(method_action)) {

                            if (component.click(method_action))
                                return sleepUntilItemCollected(component);

                        }

                    }

                }

            }

        }

        return !canCollectItems();

    }

    /**
     * Returns whether the offer window is open
     *
     * @return true if it is open
     */
    public static boolean isOfferWindowOpen() {

        if (!isOpen())
            return false;

        RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);

        return child != null && !child.isHidden();

    }

    /**
     * Returns whether the view offer window is open
     *
     * @return true if it is open
     */
    public static boolean isViewOfferWindowOpen() {

        if (!isOpen())
            return false;

        RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);

        return child != null && !child.isHidden();

    }

    /**
     * Gets the item name in the offer window
     *
     * @return String
     */
    public static String getWindowName() {

        if (isOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
            if (child == null)
                return null;

            RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_ITEM_NAME);
            if (component == null)
                return null;

            String text = component.getText();
            if (text == null || text.length() == 0)
                return null;

            return text;

        } else if (isViewOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);
            if (child == null)
                return null;

            RSInterfaceComponent component = child.getChild(Constants.VIEW_OFFER_WINDOW_ITEM_NAME);
            if (component == null)
                return null;

            String text = component.getText();
            if (text == null || text.length() == 0)
                return null;

            return text;

        }

        return null;

    }

    /**
     * Gets the item quantity in the offer window
     *
     * @return int
     */
    public static int getWindowQuantity() {

        if (isOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
            if (child == null)
                return 0;

            RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_ITEM_QUANTITY);
            if (component == null)
                return 0;

            String text = component.getText();
            if (text == null || text.length() == 0)
                return 0;

            text = text.replaceAll(",", "");

            return Integer.parseInt(text);

        } else if (isViewOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);
            if (child == null)
                return 0;

            RSInterfaceComponent component = child.getChild(Constants.VIEW_OFFER_WINDOW_ITEM_QUANTITY);
            if (component == null)
                return 0;

            String text = component.getText();
            if (text == null || text.length() == 0)
                return 0;

            text = text.replaceAll(",", "");

            return Integer.parseInt(text);

        }

        return 0;

    }

    /**
     * Gets the price per item in the offer window
     *
     * @return int
     */
    public static int getWindowPrice() {

        if (isOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
            if (child == null)
                return 0;

            RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_ITEM_PRICE);
            if (component == null)
                return 0;

            String text = component.getText();
            if (text == null || text.length() == 0)
                return 0;

            text = text.replaceAll(",", "");
            text = text.replaceAll("coins", "");
            text = text.replaceAll("coin", "");
            text = text.trim();

            return Integer.parseInt(text);

        } else if (isViewOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);
            if (child == null)
                return 0;

            RSInterfaceComponent component = child.getChild(Constants.VIEW_OFFER_WINDOW_ITEM_PRICE);
            if (component == null)
                return 0;

            String text = component.getText();
            if (text == null || text.length() == 0)
                return 0;

            text = text.replaceAll(",", "");
            text = text.replaceAll("coins", "");
            text = text.replaceAll("coin", "");
            text = text.trim();

            return Integer.parseInt(text);

        }

        return 0;

    }

    /**
     * Gets the total value in the offer window
     *
     * @return int
     */
    public static int getWindowValue() {

        if (isOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
            if (child == null)
                return 0;

            RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_VALUE);
            if (component == null)
                return 0;

            String text = component.getText();
            if (text == null || text.length() == 0)
                return 0;

            text = text.replaceAll(",", "");
            text = text.replaceAll("coins", "");
            text = text.replaceAll("coin", "");
            text = text.trim();

            return Integer.parseInt(text);

        } else if (isViewOfferWindowOpen()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.VIEW_OFFER_WINDOW_CHILD);
            if (child == null)
                return 0;

            RSInterfaceComponent component = child.getChild(Constants.VIEW_OFFER_WINDOW_VALUE);
            if (component == null)
                return 0;

            String text = component.getText();
            if (text == null || text.length() == 0)
                return 0;

            text = text.replaceAll(",", "");
            text = text.replaceAll("coins", "");
            text = text.replaceAll("coin", "");
            text = text.trim();

            return Integer.parseInt(text);

        }

        return 0;

    }

    /**
     * Sets up a new offer in the offer window
     *
     * @param OFFER
     *            offer
     * @param String
     *            name
     * @param int
     *            quantity
     * @param int
     *            price
     * @return true if an offer was setup
     */
    public static boolean setUpOffer(OFFER offer, String name, int quantity, int price) {

        if (!isOfferWindowOpen() && !openOfferWindow(offer == OFFER.SELL))
            return false;

        if (!setItem(offer, name))
            return false;

        if (!setQuantity(name, quantity))
            return false;

        if (!setPrice(price))
            return false;

        return confirm();
    }

    /**
     * Sets up a new offer in the offer window
     *
     * @param OFFER
     *            offer
     * @param String
     *            name
     * @param int
     *            quantity
     * @param int
     *            price
     * @return true if an offer was setup
     */
    public static boolean setUpOffer(OFFER offer, String name, int quantity, PRICE price) {

        if (!isOfferWindowOpen() && !openOfferWindow(offer == OFFER.SELL))
            return false;

        if (!setItem(offer, name))
            return false;

        if (!setQuantity(name, quantity))
            return false;

        if (!setPrice(price))
            return false;

        return confirm();
    }

    /**
     * Checks if the window offer item is set
     *
     * @param name
     *            of the item
     * @return true if item is set
     */
    public static boolean isWindowItemSet(String name) {

        String item = getWindowName();
        if (item == null)
            return false;

        return item.equalsIgnoreCase(name);

    }

    /**
     * Sets the item in the window offer
     *
     * @param OFFER
     *            offer
     * @param String
     *            name
     * @return true if the item was set
     */
    public static boolean setItem(OFFER offer, String name) {

        if (isWindowItemSet(name))
            return true;

        if (offer == OFFER.BUY) {

            General.sleep(General.randomSD(1000, 200));

            if (!enterNameMenuOpen()) {

                RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
                if (child == null)
                    return false;

                RSInterfaceComponent component = child.getChild(Constants.CHOOSE_ITEM_COMPONENT);
                if (component == null)
                    return false;

                if (component.click()) {

                    Timing.waitCondition(new Condition() {
                        public boolean active() {
                            General.sleep(100);
                            return enterNameMenuOpen();
                        }
                    }, 5000);

                    General.sleep(General.randomSD(1000, 200));

                }

            }

            if (!getEnterAmountMenuText().contains(name)) {
                if (!getEnterAmountMenuText().equals("*")) {
                    Keyboard.holdKey('\b', (int) '\b', new scripts.api.pattern.Condition(() -> getEnterAmountMenuText().equals("*")));
                }

                Keyboard.typeString(name);
                General.sleep(General.randomSD(1000, 200));
            }

            return selectItem(name);

        } else if (offer == OFFER.SELL) {

            RSItem[] item = Inventory.find(name);

            if (item.length == 0)
                return false;

            if (item[0].click()) {

                Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(100);
                        return isWindowItemSet(name);
                    }
                }, 5000);

            }

        }

        return isWindowItemSet(name);

    }

    public static boolean selectItem(String name) {

        RSInterfaceChild child = Interfaces.get(Constants.ITEM_SELECTION_MASTER, Constants.ITEM_SELECTION_CHILD);
        if (child == null)
            return false;

        RSInterfaceComponent[] components = child.getChildren();
        if (components == null || components.length == 0)
            return false;

        for (int i = 0; i < components.length; i++) {

            String component_name = components[i].getText();

            if (component_name != null && component_name.equals(name)) {

                RSInterfaceComponent component_item = components[i - 1];
                if (component_item == null)
                    return false;

                if (!isViewable(child, component_item)) {
                    child.hover();
                    Mouse.scroll(isAbove(child, component_item), getTicks(child, component_item));
                } else if (component_item.click()) {

                    Timing.waitCondition(new Condition() {
                        public boolean active() {
                            General.sleep(100);
                            return isWindowItemSet(name);
                        }
                    }, 5000);

                    return isWindowItemSet(name);

                }

            }

        }

        return isWindowItemSet(name);

    }

    /**
     * Checks if the enter item name menu is open
     *
     * @return true if the menu is open
     */
    public static boolean enterNameMenuOpen() {

        RSInterfaceChild child = Interfaces.get(Constants.OFFER_WINDOW_ENTER_MENU_MASTER,
                Constants.OFFER_WINDOW_ENTER_ITEM_NAME_CHILD);
        return child != null && !child.isHidden();

    }

    /**
     * Sets the window item quantity, a quantity of less than or equal to 0 will
     * set the quantity to all.
     *
     *
     * @param String
     *            name
     * @param int
     *            quantity
     * @return true if the quantity was set
     */
    public static boolean setQuantity(String name, int quantity) {

        if (isWindowQuantitySet(name, quantity))
            return true;

        if (!enterAmountMenuUp()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
            if (child == null)
                return false;

            RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_QUANTITY_ENTER);
            if (component == null)
                return false;

            if (component.click()) {

                Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(100);
                        return enterAmountMenuUp();
                    }
                }, 5000);

            }

        }

        if (enterAmountMenuUp()) {

            Keyboard.typeSend(Integer.toString(quantity));

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(100);
                    return isWindowQuantitySet(name, quantity);
                }
            }, 5000);

        }

        return isWindowQuantitySet(name, quantity);

    }

    /**
     * Checks if the window quantity is set
     *
     * @param String
     *            name
     * @param int
     *            quantity
     * @return true if the quantity is set
     */
    public static boolean isWindowQuantitySet(String name, int quantity) {

        return (getWindowQuantity() == quantity || (quantity <= 0 && Inventory.getCount(name) == getWindowQuantity()));

    }

    /**
     * Sets the specified price of the item, a price less than or equal to 0
     * will set it as market price.
     *
     * @param int
     *            price
     * @return true if the price was set
     */
    public static boolean setPrice(int price) {

        if (isWindowPriceSet(price))
            return true;

        if (!enterAmountMenuUp()) {

            RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
            if (child == null)
                return false;

            RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_PRICE_ENTER);
            if (component == null)
                return false;

            if (component.click()) {

                Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(100);
                        return enterAmountMenuUp();
                    }
                }, 5000);

            }

        }

        if (enterAmountMenuUp()) {

            Keyboard.typeSend(Integer.toString(price));

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(100);
                    return isWindowPriceSet(price);
                }
            }, 5000);

        }

        return isWindowPriceSet(price);

    }

    /**
     * Sets the specified price of the item, a price less than or equal to 0
     * will set it as market price.
     *
     * @param int
     *            price
     * @return true if the price was set
     */
    public static boolean setPrice(PRICE price) {

        RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
        if (child == null)
            return false;

        RSInterfaceComponent component = child.getChild(price.getChild());
        if (component == null)
            return false;

        int loop = 1;

        if (price == PRICE.ADD_TEN_PERCENT || price == PRICE.SUBTRACT_TEN_PERCENT)
            loop++;

        if (price == PRICE.ADD_20_PERCENT || price == PRICE.SUBTRACT_20_PERCENT)
            loop += 2;

        if (price == PRICE.ADD_50_PERCENT || price == PRICE.SUBTRACT_50_PERCENT)
            loop += 5;

        if (price == PRICE.GUIDE_PRICE)
            loop = 0;

        for (int i = 0; i < loop; i++) {

            final int previous_price = getWindowPrice();

            if (component.click()) {

                Timing.waitCondition(new Condition() {
                    public boolean active() {
                        General.sleep(100);
                        return previous_price != getWindowPrice() || previous_price == 1;
                    }
                }, 5000);

            }

        }

        return true;

    }

    /**
     * Checks if the window quantity is set
     *
     * @param String
     *            name
     * @param int
     *            quantity
     * @return true if the quantity is set
     */
    public static boolean isWindowPriceSet(int price) {

        return getWindowPrice() == price || price <= 0;

    }

    /**
     * Checks if the enter item price menu is up
     *
     * @return true if the menu is open
     */
    public static boolean enterAmountMenuUp() {

        RSInterfaceChild child = Interfaces.get(Constants.OFFER_WINDOW_ENTER_MENU_MASTER,
                Constants.OFFER_WINDOW_ENTER_AMOUNT_CHILD);
        return child != null && !child.isHidden();

    }

    public static String getEnterAmountMenuText() {
        RSInterfaceChild child = Interfaces.get(Constants.OFFER_WINDOW_ENTER_MENU_MASTER,
                Constants.OFFER_WINDOW_ENTER_AMOUNT_CHILD);
        String text = child != null ? child.getText() : "";
        return text.substring(text.indexOf("?</col> ") + 7).trim();
    }

    /**
     * Clicks the confirm button
     *
     * @return true if the offer was confirmed
     */
    public static boolean confirm() {

        RSInterfaceChild child = Interfaces.get(Constants.MASTER, Constants.OFFER_WINDOW_CHILD);
        if (child == null)
            return false;

        RSInterfaceComponent component = child.getChild(Constants.OFFER_WINDOW_CONFIRM);
        if (component == null || component.isHidden())
            return false;

        if (component.click()) {

            Timing.waitCondition(new Condition() {
                public boolean active() {
                    General.sleep(100);
                    return !isOfferWindowOpen() && !isViewOfferWindowOpen();
                }
            }, 5000);

        }

        return !isOfferWindowOpen() && !isViewOfferWindowOpen();

    }


    public static boolean openOfferWindow(boolean sell) {
        RSGEOffer a1 = null;
        RSGEOffer[] var10;
        int a4 = (var10 = org.tribot.api2007.GrandExchange.getOffers()).length;
        int a3;
        int var10000 = a3 = 0;

        RSGEOffer var16;
        while(true) {
            if(var10000 >= a4) {
                var16 = a1;
                break;
            }

            RSGEOffer a2;
            if((a2 = var10[a3]).getStatus() == RSGEOffer.STATUS.EMPTY) {
                var16 = a1 = a2;
                break;
            }

            ++a3;
            var10000 = a3;
        }

        if(var16 == null) {
            return false;
        }

        RSInterfaceChild var12;
        if((var12 = Interfaces.get(465, 7 + a1.getIndex())) == null) {
            return false;
        }

        RSInterfaceComponent var14;
        if((var14 = var12.getChild(sell?4:3)) == null) {
            return false;
        }

        String[] var15;
        if((var15 = var14.getActions()) == null || var15.length == 0) {
            return false;
        }

        if(!var14.click("Create")) {
            return false;
        }

        return Timing.waitCondition(new Condition() {
            @Override
            public boolean active() {
                return org.tribot.api2007.GrandExchange.getWindowState() == org.tribot.api2007.GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW;
            }
        }, General.random(2500, 5000));
    }


    public static boolean isViewable(RSInterface parent, RSInterface child) {
        int offset = parent.getScrollY();
        Point parentPos = parent.getAbsolutePosition(), childPos = child.getAbsolutePosition();
        int relativeY = (int) (childPos.getY() - parentPos.getY()) - 2;
        return relativeY < offset || relativeY + offset < parent.getHeight() - (child.getHeight() / 2);
    }

    public static boolean isAbove(RSInterface parent, RSInterface child) {
        int offset = parent.getScrollY();
        Point parentPos = parent.getAbsolutePosition(), childPos = child.getAbsolutePosition();
        int relativeY = (int) (childPos.getY() - parentPos.getY()) - 2;
        return relativeY < offset;
    }

    public static int getTicks(RSInterface parent, RSInterface child) {
        int offset = parent.getScrollY();
        Point parentPos = parent.getAbsolutePosition(), childPos = child.getAbsolutePosition();
        return (int) Math.floor((childPos.getY() - parentPos.getY() - offset) / 45);
    }

}


