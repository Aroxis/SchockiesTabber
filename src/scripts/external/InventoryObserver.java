package scripts.external;

import org.tribot.api.General;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

import java.util.HashMap;
import java.util.HashSet;


/**
 * Observer class for listening to inventory event changes
 *
 * Not very pretty, just did it for shits and giggles.
 * @author Sea Shepard
 *
 */
public final class InventoryObserver extends Thread {

    /** Our listeners we have to notify */
    private static HashSet<InventoryListener> listeners;

    /** How long between inventory checks */
    private static final int SLEEP = 600; //~1 game tick (real is 650)

    /** How our inventory was last tick.  */
    private static HashMap<Integer, Integer> last_inventory;


    /**
     * Just some initialization stuff.
     */
    public InventoryObserver() {
        last_inventory = populateInventoryMap();
        listeners = new HashSet<InventoryListener>();
        start();
    }

    /**
     * Adds a listener to the event queue
     * @param listener
     */
    public static void addListener(InventoryListener listener) {
        listeners.add(listener);
    }
    @Override
    public void run() {
        while(true) {

            //See if we have added anything
            HashMap<Integer, Integer> currentInventory = populateInventoryMap();
            for(Integer id : currentInventory.keySet()) {
                if(last_inventory.containsKey(id)) {
                    if(currentInventory.get(id) > last_inventory.get(id)) {
                        int additionCount = currentInventory.get(id) - last_inventory.get(id);
                        General.println("Item added, ID: " + id + " COUNT - " + additionCount);
                        notifyListenersOfAddition(id, additionCount);
                    }
                    //No else, this will be dealt with in the next loop.
                } else {
                    int additionCount = currentInventory.get(id);
                    General.println("Item added, ID: " + id + " COUNT - " + additionCount);
                    notifyListenersOfAddition(id, currentInventory.get(id));
                }
            }

            //Now we should check to see if we have dropped anything.
            for(Integer id : last_inventory.keySet()) {
                if(last_inventory.get(id) > Inventory.getCount(id)) {
                    //Item was removed
                    int removeCount = (last_inventory.get(id) - Inventory.getCount(id));
                    General.println("Item removed, ID: " + id + " COUNT - " + removeCount);
                    notifyListenersOfRemoval(id, removeCount);
                }
            }
            last_inventory = currentInventory;
            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
                //Maybe kill the thread?
                interrupt();
                General.println("Inventory Observer Error. Listeners will no longer be notified.");
            }
        }
    }

    /**
     * Passes along the "event" to all of our sublisteners
     * @param id
     *              The ID of the item removed
     * @param removeCount
     *              The count of the item removed
     */
    private void notifyListenersOfRemoval(int id, int removeCount) {
        // TODO Auto-generated method stub
    }

    /**
     * Passes along the "event" to all of our sublisteners
     * @param id
     *              The ID of the item added
     * @param count
     *              The count of the item added
     */
    private void notifyListenersOfAddition(int id, int count) {
        //Shit was added.
        //Loop through listeners and notify them.
        for(InventoryListener l : listeners) {
            l.inventoryItemAdded(id, count);
        }
    }

    /**
     * Resets the inventory hashmap
     */
    private HashMap<Integer, Integer> populateInventoryMap() {
        HashMap<Integer, Integer> returnMap = new HashMap<Integer, Integer>();
        for(RSItem item : Inventory.getAll()) {
            if(!returnMap.containsKey(item.getID())) {
                returnMap.put(item.getID(), Inventory.getCount(item.getID()));
            }
        }
        return returnMap;
    }
}

