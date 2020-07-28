package scripts.nodes;

import scripts.Main;
import scripts.helpers.HouseHelper;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSPlayer;

import java.util.Random;

import static org.tribot.api.General.sleep;

public class CraftTabs extends Node {
    private int blackListAt = 15;

    @Override
    public void execute() throws Exception {
        RSObject lectern = getLectern();

        while (lectern == null || isLoading()) {
            lectern = getLectern();
            sleep(10);
        }

        waitReaction();

        if (lectern == null)  {
            HouseHelper.blacklistOwner();
            new LeaveHouse().execute();
            return;
        }

        approachAndInteract(lectern, "Study", 0);

        while (Interfaces.get(79, 23) == null) {
            sleep(10);
        }

        RSInterface createTabComponent =  getCreateTabComponent();

        sleep(100,300);
        createTabComponent.click();

        if (abc.shouldLeaveGame()) {
            Mouse.leaveGame();
        }

        while (true) {
            sleep(1000,10000);

            if (abc.shouldCheckXP()) {
                Skills.hover(Skills.SKILLS.MAGIC);
                sleep(300, 2000);
                Inventory.open();

                if (!isFinished()) {
                    Mouse.leaveGame();
                }

                if (abc.shouldHover()) {
                    RSObject portal = getSingleObject("Portal");
                    if (portal != null && portal.isOnScreen()) {
                        portal.hover();
                    }
                    while (!isFinished()) {
                        sleep(10);

                    }
                    waitReaction();
                }
            }

            if (isFinished()) {
                return;
            }
        }
    }

    @Override
    public boolean validate() {
/*        if (getLectern() != null && getLectern().getPosition().distanceTo(Player.getPosition()) > blackListAt) {
            HouseHelper.blacklistOwner();
            return false;
        }*/

        return !inRimmington() && Inventory.getCount("Soft clay") >= 2;
    }

    private RSObject getLectern() {
        RSObject[] rsObjects = Objects.findNearest(30, "Lectern");

        if (rsObjects.length == 0) {
            return null;
        } else {
            return rsObjects[0];
        }
    }

    private boolean isFinished() {
        RSPlayer player = Player.getRSPlayer();

        if (player.getAnimation() == -1) {
            sleep(50);

            if (player.getAnimation() == -1) {
                return true;
            }

            return false;
        }

        return false;
    }

    private RSInterface getCreateTabComponent() {
        switch (Main.getScriptMode()) {
            case Varrock:
                return Interfaces.get(79, 23);
            case House:
                return Interfaces.get(79, 15);
            default:
                return null;
        }
    }

}
