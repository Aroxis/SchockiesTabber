package scripts.tabber.helpers;

import java.util.ArrayList;
import java.util.HashSet;

public final class HouseHelper {

    private static ArrayList<String> ownerNames = new ArrayList<>();
    private static HashSet<String> blackListedNames = new HashSet<String>();
    private static String lastReturnedName = "";

    public HouseHelper() {

    }

    public static void setOwnerNames(ArrayList<String> names) {
        ownerNames = names;
    }

    public static String getOwnerName() {
        if (!lastReturnedName.isEmpty() && !blackListedNames.contains(lastReturnedName) && ownerNames.contains(lastReturnedName)) {
            return lastReturnedName;
        } else {
            for (String name : ownerNames) {
                if (!blackListedNames.contains(name)) {
                    lastReturnedName = name;
                    return name;
                }
            }

            return "";
        }
    }

    public static void blacklistOwner(String name) {
        if (!name.isEmpty()) {
            blackListedNames.add(name);
        }
    }

    public static void blacklistOwner() {
        if (!getOwnerName().isEmpty()) {
            String ownerName = getOwnerName();
            blackListedNames.add(ownerName);
        }
    }
}
