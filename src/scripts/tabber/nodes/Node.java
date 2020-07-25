package scripts.tabber.nodes;

import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.tabber.Areas;

import java.util.Random;

import static org.tribot.api.General.sleep;

public abstract class Node {
    public static final ABCUtil abc = new ABCUtil();

    public abstract void execute() throws Exception;

    public abstract boolean validate();

    protected RSObject getSingleObject(String name) {
        RSObject[] objects = Objects.findNearest(30, name);

        if (objects.length != 0) {
            return objects[0];
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
        return Interfaces.get(71) != null;
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

}