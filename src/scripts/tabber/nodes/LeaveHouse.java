package scripts.tabber.nodes;

import org.tribot.api2007.Camera;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;

import static org.tribot.api.General.sleep;


public class LeaveHouse extends Node {

    @Override
    public void execute() throws Exception {
        RSObject portal = getSingleObject("Portal");

        if (portal == null) {
            return;
        }

        if (!portal.isOnScreen()) {
            sleep(200,1000);
            Camera.turnToTile(portal.getPosition());

            if (!portal.isOnScreen()) {
                sleep(300,1000);
                Walking.walkTo(deriveTile(portal.getPosition(), 4));
            }

        }

        sleep(300, 1500);
        // portal.click();

        while (!portal.click()) {
            sleep(100);
        }

        if (Inventory.find("Soft clay").length < 5) {
            RSItem notedSoftClay = getNotedSoftclay();
            notedSoftClay.hover();
        }

        while(!inRimmington()) {
            sleep(10);
        }


        /*
        waitWhileWalking();
        waitWhileEnteringPortal(false);*/
    }

    @Override
    public boolean validate() {
        return !inRimmington();
    }
}
