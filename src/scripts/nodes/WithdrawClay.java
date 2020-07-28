package scripts.nodes;

import org.tribot.api.DynamicClicking;
import org.tribot.api.input.Mouse;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSNPC;
import scripts.Areas;
import scripts.Main;

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

        waitReaction();

        if (GameTab.getOpen() != GameTab.TABS.INVENTORY) {
            Inventory.open();
        }

        notedClay.click();

        approachAndInteract(phials, "Use Soft clay -> Phials", notedClay.getID());

        RSInterface dialogue = Interfaces.get(219,1,3);
        while (Interfaces.get(219,1,3) == null || dialogue.isHidden()) {
            waitReaction();
            dialogue = Interfaces.get(219,1,3);

            if (Main.misClickCheck()) {
                execute();
                return;
            }
            /*msWaitedForPhialsSequence += 200;

            if (msWaitedForPhialsSequence >= 3000) {
                return;
            }*/
        }

        sleep(100,300);

        while(!dialogue.click()) {
            sleep(100);
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
