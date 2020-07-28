package scripts.nodes;

import org.tribot.api2007.Inventory;
import scripts.Main;
import scripts.helpers.HouseHelper;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSObject;
import scripts.Areas;

import static org.tribot.api.General.sleep;

public class EnterHouse extends Node {
    @Override
    public void execute() throws Exception {
        RSObject portal = getSingleObject("Portal");
        sleep(300, 1500);

        if (!portal.isOnScreen()) {
            Camera.turnToTile(portal.getPosition());
        }

        approachAndInteract(portal, "Friend's house", 0);

        RSInterface enterHouseDialogue = Interfaces.get(162, 44);
        enterHouseDialogue.hover();

        while (enterHouseDialogue.isHidden()) {
            sleep(10);
        }

        RSInterface lastNameComponent = Interfaces.get(162, 40, 0);

        sleep(200, 500);
        if (lastNameComponent != null && lastNameComponent.getText().contains(HouseHelper.getOwnerName())) {
            lastNameComponent.click();
        } else {
            Keyboard.typeString(HouseHelper.getOwnerName());
            sleep(100,200);
            Keyboard.pressEnter();
        }

        while (inRimmington()) {
            sleep(10);

            if (Main.cannotAccessHouse()) {
                waitReaction();
                execute();
                return;
            }

            while (isLoading()) {
                sleep(10);
            }
        }
    }

    private void waitWhileEnteringPortal(boolean enteringHouse) {
        int failCount = 0;

        if (enteringHouse) {
            while (!inRimmington() && !isLoading()) {
                if (failCount > 30) {
                    break;
                }

                sleep(100);
                failCount++;
            }
        } else {
            while (inRimmington() || isLoading()) {
                if (failCount > 30) {
                    break;
                }

                sleep(100);
                failCount++;
            }
        }
    }

    @Override
    public boolean validate() {
        return Inventory.isFull() && inRimmington();
    }
}
