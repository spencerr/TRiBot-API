package scripts.api.items;

/**
 * Created by Spencer on 10/8/2017.
 */
public interface Item {

    int getId();
    String getName();

    boolean isConsumable();
    boolean has();
    boolean has(int count);
    boolean hasInBank();
    boolean hasInBank(int count);
}
