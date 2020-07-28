package scripts.nodes;

import org.tribot.api.DynamicClicking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import scripts.Main;

import static org.tribot.api.General.sleep;


public class LeaveHouse extends Node {

    @Override
    public void execute() throws NodeExecutionException {
        RSObject portal = getSingleObject("Portal");

        if (portal == null) {
            throw new NodeExecutionException("Could not find portal");
        }

        approachAndInteract(portal, "Enter", 0);

        if (Inventory.find("Soft clay").length < 5) {
            RSItem notedSoftClay = getNotedSoftclay();

            if (Main.getPhialsVisible()) {
                notedSoftClay.hover();
            }
        }

        while(!inRimmington()) {
            sleep(10);
        }

    }

    @Override
    public boolean validate() {
        return !inRimmington() && !Inventory.isFull();
    }
}
