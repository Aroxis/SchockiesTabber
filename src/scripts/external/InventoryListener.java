package scripts.external;

/**
 * Receives notifications from the inventory observer
 * @author Sea Shepard
 *
 */
public interface InventoryListener {

    /**
     * Called when an item is added to the inventory
     * @param id
     */
    public void inventoryItemAdded(int id, int count);

    /**
     * Called when an item is removed from the inventory
     * @param id
     */
    public void inventoryItemRemoved(int id, int count);
}