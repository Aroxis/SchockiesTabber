package scripts.nodes;

import org.tribot.api.DynamicClicking;
import org.tribot.api.interfaces.Positionable;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;
import scripts.Areas;

import java.util.Random;

import static org.tribot.api.General.sleep;


public abstract class Node {
    public static final ABCUtil abc = new ABCUtil();

    public abstract void execute() throws Exception;
    public abstract boolean validate();

    private int nextRunAt = -1;


    protected RSObject getSingleObject(String name) {
        RSObject[] objects = Objects.findNearest(30, name);

        if (objects.length != 0) {
            return objects[0];
        }

        return null;
    }

    protected RSItem getSingleItem(int id) {
        RSItem[] items = Inventory.getAll();

        for (RSItem item : items) {
            if (item.getDefinition().getID() == id) {
                return item;
            }
        }

        return null;
    }

    protected void waitWhileWalking() {
        while (Player.getRSPlayer().isMoving()) {
            sleep(200, 1000);

            /*
            if (!Game.isRunOn() && nextRunAt <= Game.getRunEnergy()) {
                WebWalking.setUseRun(true);
                nextRunAt = abcUtil.generateRunActivation();
            }*/
        }
    }

    protected boolean inRimmington() {
        return Areas.rimmingtonArea.contains(Player.getPosition());
    }

    protected boolean isLoading() {
        RSInterface homeInterface = Interfaces.get(71);
        boolean interfaceNotVisible = homeInterface == null || homeInterface.isHidden();


        return !interfaceNotVisible || Game.getGameState() == 25;
    }

    protected RSTile deriveTile(RSTile center, int offset) {
        Random random = new Random();
        return new RSTile(center.getX() + random.nextInt(offset), center.getY() + random.nextInt(offset), center.getPlane());
    }

    protected RSItem getNotedSoftclay() {
        RSItem[] items = Inventory.find("Soft clay");

        for (RSItem item: items) {
            if (item.getDefinition().isNoted()){
                return item;
            }
        }

        return null;
    }

    protected void approachAndInteract(RSObject rsObject, String option, int selectedItemId) throws NodeExecutionException {
        if (rsObject == null) {
            throw new NodeExecutionException("RSNPC was null.");
        }

        runCheck();

        RSTile objectTile = rsObject.getPosition();

        if (!rsObject.isOnScreen()) {
            if (rsObject.getPosition().distanceTo(Player.getPosition()) > 15) {
                RSItem selectedItem = getSingleItem(selectedItemId);

                while(Walking.walkTo(deriveTile(objectTile, 3))) {
                    waitReaction();
                }

                if (selectedItemId != 0 && Game.getItemSelectionState() == 0) {
                    selectedItem.click();
                }

                while (!rsObject.isOnScreen()) {
                    waitReaction();
                }


                if (Player.isMoving()) {
                    while (!DynamicClicking.clickRSObject(rsObject,option)) {
                        waitReaction();
                    }
                } else {
                    while (!DynamicClicking.clickRSObject(rsObject, option)) {
                        waitReaction();
                    }
                }


            } else {
                Camera.turnToTile(rsObject.getPosition());

                if (!rsObject.isOnScreen()) {
                    Walking.walkTo(deriveTile(rsObject.getPosition(), 3));

                    if (selectedItemId != 0 && Game.getItemSelectionState() == 0) {
                        getSingleItem(selectedItemId).click();
                    }

                    while (!DynamicClicking.clickRSObject(rsObject, option)) {
                        waitReaction();
                    }
                } else {
                    while (!DynamicClicking.clickRSObject(rsObject, option)) {
                        waitReaction();
                    }
                }
            }
        } else {
            while (!DynamicClicking.clickRSObject(rsObject, option)) {
                waitReaction();
            }
        }
    }

    protected void approachAndInteract(RSNPC rsNpc, String option, int selectedItemId) throws NodeExecutionException {
        if (rsNpc == null) {
            throw new NodeExecutionException("RSNPC was null.");
        }

        runCheck();

        RSTile objectTile = rsNpc.getPosition();

        if (!rsNpc.isOnScreen()) {
            if (rsNpc.getPosition().distanceTo(Player.getPosition()) > 15) {
                RSItem selectedItem = getSingleItem(selectedItemId);

                while(Walking.walkTo(deriveTile(objectTile, 3))) {
                    waitReaction();
                }

                if (selectedItemId != 0 && Game.getItemSelectionState() == 0) {
                    selectedItem.click();
                }

                while (!rsNpc.isOnScreen()) {
                    waitReaction();
                }


                if (Player.isMoving()) {
                    while (!DynamicClicking.clickRSNPC(rsNpc,option)) {
                        waitReaction();
                        selectedItem.click();
                    }
                } else {
                    while (!DynamicClicking.clickRSNPC(rsNpc, option)) {
                        waitReaction();
                        selectedItem.click();
                    }
                }


            } else {
                Camera.turnToTile(rsNpc.getPosition());

                if (!rsNpc.isOnScreen()) {
                    Walking.walkTo(deriveTile(rsNpc.getPosition(), 3));
                }

                if (selectedItemId != 0 && Game.getItemSelectionState() == 0) {
                    getSingleItem(selectedItemId).click();
                }

                while (!DynamicClicking.clickRSNPC(rsNpc, option)) {
                    waitReaction();
                }
            }
        } else {
            while (!DynamicClicking.clickRSNPC(rsNpc, option)) {
                waitReaction();
            }
        }
    }

    private int getDistanceFromPlayer(Positionable positionable) {
        return Player.getPosition().distanceTo(positionable);
    }

    private void runCheck() {
        if (nextRunAt == -1) {
            nextRunAt = abc.generateRunActivation();
        }

        if (!Game.isRunOn() && nextRunAt <= Game.getRunEnergy()) {
            Options.setRunEnabled(true);
            nextRunAt = abc.generateRunActivation();
        }
    }

    protected void waitReaction() {
        long time = abc.generateReactionTime();
        sleep(time);
    }

}