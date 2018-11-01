package scripts.api.entityselector.finders;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Laniax
 */
public class Utils {

    /**
     * Converts all values inside a list of strings to lowercase.
     * @param list
     * @return
     */
    public static List<String> stringListToLowercase(List<String> list) {

        return list.stream().map(String::toLowerCase).collect(Collectors.toList());
    }
}