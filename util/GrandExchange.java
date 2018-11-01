package scripts.api.util;

import org.tribot.api.General;
import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSGEOffer;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSTile;
import scripts.api.entityselector.Entities;
import scripts.api.entityselector.finders.prefabs.InterfaceEntity;
import scripts.api.grandexchange.Constants;
import scripts.api.grandexchange.METHOD;
import scripts.api.grandexchange.OFFER;
import scripts.api.grandexchange.PRICE;
import scripts.api.interaction.Interact;
import scripts.api.interaction.prefabs.InterfacePrefab;
import scripts.api.items.Item;
import scripts.api.webwalker.local.walker_engine.interaction_handling.AccurateMouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Spencer on 10/31/2017.
 */
public class GrandExchange {

    public static final RSArea EXCHANGE_AREA = new RSArea(new RSTile(3165, 3487, 0), 10);

    public static boolean isExchangeOpen() {
        return Entities.find(InterfaceEntity::new).inMaster(org.tribot.api2007.GrandExchange.INTERFACE_EXCHANGE_ID).isNotHidden().getFirstResult() != null;
    }

    public static boolean isCreateOrderOpen() {
        return org.tribot.api2007.GrandExchange.getWindowState() == org.tribot.api2007.GrandExchange.WINDOW_STATE.NEW_OFFER_WINDOW;
    }

    public static boolean isOrderOpen() {
        return org.tribot.api2007.GrandExchange.getWindowState() == org.tribot.api2007.GrandExchange.WINDOW_STATE.OFFER_WINDOW;
    }

    public static boolean isViewOrdersOpen() {
        return org.tribot.api2007.GrandExchange.getWindowState() == org.tribot.api2007.GrandExchange.WINDOW_STATE.SELECTION_WINDOW;
    }

    public static boolean hasCompleteOffers() {
        for (RSGEOffer offer : getOffers()) {
            if (offer.getStatus() == RSGEOffer.STATUS.COMPLETED)
                return true;
        }

        return false;
    }

    public static RSGEOffer getOfferFor(String name) {
        for (RSGEOffer offer : getOffers()) {
            if (offer.getItemName() != null && offer.getItemName().equals(name))
                return offer;
        }

        return null;
    }

    public static boolean hasOfferFor(String name) {
        for (RSGEOffer offer : getOffers()) {
            if (offer.getItemName() != null && offer.getItemName().equals(name))
                return true;
        }

        return false;
    }

    public static boolean hasOfferFor(int id) {
        for (RSGEOffer offer : getOffers()) {
            if (offer.getStatus() == RSGEOffer.STATUS.COMPLETED && offer.getItemID() == id)
                return true;
        }

        return false;
    }

    public static boolean hasCompleteOfferFor(int id) {
        for (RSGEOffer offer : getOffers()) {
            if (offer.getStatus() == RSGEOffer.STATUS.COMPLETED && offer.getItemID() == id)
                return true;
        }

        return false;
    }

    public static boolean createOffer(Item item, int amount, OFFER type) {
        if (Global.getAccountBag().get("failedToBuy" + item.getName(), false)) {
            Global.getAccountBag().remove("failedToBuy" + item.getName());
            return scripts.api.grandexchange.GrandExchange.setUpOffer(type, item.getName(), amount, PRICE.ADD_50_PERCENT);
        }

        if (Global.getAccountBag().get("failedToSell" + item.getName(), false)) {
            Global.getAccountBag().remove("failedToSell" + item.getName());
            return scripts.api.grandexchange.GrandExchange.setUpOffer(type, item.getName(), amount, PRICE.SUBTRACT_50_PERCENT);
        }

        return scripts.api.grandexchange.GrandExchange.setUpOffer(type, item.getName(), amount, type == OFFER.BUY ? PRICE.ADD_20_PERCENT : PRICE.SUBTRACT_20_PERCENT);
    }

    public static ArrayList<RSGEOffer> getOffers() {
        return new ArrayList<>(Arrays.asList(org.tribot.api2007.GrandExchange.getOffers()));
    }

    public static boolean isOffersComplete() {
        for (RSGEOffer offer : getOffers()) {
            if (offer.getStatus() != RSGEOffer.STATUS.COMPLETED && offer.getStatus() != RSGEOffer.STATUS.EMPTY)
                return false;
        }

        return true;
    }

    public static boolean claimAll(METHOD method) {
        return scripts.api.grandexchange.GrandExchange.collect(method);
    }

    public static boolean cancelOffer(Item item) {
        ArrayList<RSGEOffer> offers = getOffers();
        Optional<RSGEOffer> optionalOffer = offers.stream().filter(rsgeOffer -> rsgeOffer.getItemID() == item.getId()).findFirst();

        if (optionalOffer.isPresent()) {
            RSGEOffer offer = optionalOffer.get();
            if (offer.getStatus() != RSGEOffer.STATUS.CANCELLED) {
                RSInterface offerInterface = Entities.find(InterfaceEntity::new)
                        .inMasterAndChild(Constants.MASTER, 7 + offer.getIndex()).getFirstResult();

                Interact.with(InterfacePrefab::new)
                        .set(offerInterface)
                        .action("Abort")
                        .waitFor(() -> getOffers().stream().anyMatch(rsgeOffer -> rsgeOffer.getItemID() == item.getId() && rsgeOffer.getStatus() == RSGEOffer.STATUS.CANCELLED), 1250, 1750)
                        .execute();

            }

            return claimCanceledOffer(offer);
        }

        return false;
    }

    public static boolean claimCanceledOffer(RSGEOffer offer) {
        if (offer == null) return false;

        RSInterface offerInterface = Entities.find(InterfaceEntity::new)
                .inMasterAndChild(Constants.MASTER, 7 + offer.getIndex()).getFirstResult();

        if (AccurateMouse.click(offerInterface, "View offer")) {
            if (Timing.waitCondition(General.random(1250, 1750), scripts.api.grandexchange.GrandExchange::isViewOfferWindowOpen)) {
                return scripts.api.grandexchange.GrandExchange.collect(METHOD.NOTES);
            }
        }

        return false;
    }


    public static boolean close() {
        return scripts.api.grandexchange.GrandExchange.close();
    }
}
