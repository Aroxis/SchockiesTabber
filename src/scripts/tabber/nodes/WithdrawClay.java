package scripts.tabber.nodes;

import org.tribot.api.DynamicClicking;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import scripts.tabber.Areas;

import static org.tribot.api.General.sleep;

public class WithdrawClay extends Node {

    @Override
    public void execute() throws Exception {
        RSItem notedClay = getNotedSoftclay();
        RSNPC phials = getSingleNPC("Phials");

        if (notedClay == null) {
            throw new NodeExecutionException("Inventory does not contain noted Soft clay.");
        }

        if (phials == null) {
            throw new NodeExecutionException("Could not find Phials.");
        }

        sleep(100,300);

        //if (!phials.isOnScreen()) {
        Camera.turnToTile(phials);
        //}

        if (GameTab.getOpen() != GameTab.TABS.INVENTORY) {
            Inventory.open();
        }

        if (!Game.getSelectedItemName().equals(notedClay.name)) {
            notedClay.click();
        }

        sleep(400, 3000);
        Mouse.click(phials.getPosition().getHumanHoverPoint(), 3);
        sleep(200, 300);

        while (!DynamicClicking.clickRSNPC(phials, "Use Soft clay -> Phials")) {
            sleep(200, 400);
        }

        while (!Player.isMoving()){
            sleep(5);
        }

        int msWaitedForPhialsSequence = 0;

        waitWhileWalking();
        while (Interfaces.get(219,1,3) == null) {
            sleep(200);
            msWaitedForPhialsSequence += 200;

            if (msWaitedForPhialsSequence >= 3000) {
                return;
            }
        }

        sleep(100,500);

        RSInterface dialogue = Interfaces.get(219,1,3);

        while(!dialogue.click()) {
            sleep(300, 1000);
        }

        while(!Inventory.isFull()) {
            sleep(10);
        }
    }

    @Override
    public boolean validate() {

        return Areas.rimmingtonArea.contains(Player.getPosition()) && !Inventory.isFull();
    }

    private RSNPC getSingleNPC(String name) {
        RSNPC[] npcs = NPCs.findNearest(name);

        if (npcs.length != 0) {
            return npcs[0];
        }

        return null;
    }
}
