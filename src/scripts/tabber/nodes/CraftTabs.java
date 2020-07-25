package scripts.tabber.nodes;

import scripts.tabber.helpers.HouseHelper;
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

        if (!lectern.isOnScreen()) {
            sleep(200, 1000);
            Camera.turnToTile(lectern.getPosition());
        }

        if (lectern.getPosition().distanceTo(Player.getPosition()) > 15) {
            sleep(500, 1500);
            Walking.walkTo(deriveTile(lectern.getPosition(), 2));

            while (lectern.getPosition().distanceTo(Player.getPosition()) > 7) {
                sleep(100, 200);
            }

            while (!lectern.click()) {
                sleep(300, 1300);
            }
        } else {
            sleep(300, 1300);
            while (!lectern.click()) {
                sleep(300, 1300);
            }
        }


        while (Interfaces.get(79, 23) == null) {
            sleep(10);
        }


        RSInterface varrockComponent =  Interfaces.get(79, 23);

        sleep(200, 500);
        varrockComponent.click();
        Mouse.leaveGame();
        while (true) {
            sleep(1000,10000);

            if (new Random().nextInt(15) == 0) {
                Skills.hover(Skills.SKILLS.MAGIC);
                sleep(300, 2000);
                Inventory.open();

                if (!isFinished()) {
                    Mouse.leaveGame();
                }
            }

            if (isFinished()) {
                return;
            }
        }
    }

    @Override
    public boolean validate() {
        if (getLectern().getPosition().distanceTo(Player.getPosition()) > blackListAt) {
            HouseHelper.blacklistOwner();
        }

        return !inRimmington();
    }

    private RSObject getLectern() {
        RSObject[] rsObjects = Objects.find(30, "Lectern");

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

}
