package scripts.tabber;

import org.tribot.api2007.Inventory;
import org.tribot.api2007.WorldHopper;
import scripts.tabber.external.InventoryListener;
import scripts.tabber.external.InventoryObserver;
import scripts.tabber.helpers.HouseHelper;
import scripts.tabber.nodes.*;
import org.tribot.api.General;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

@ScriptManifest(authors = { "Schockie" }, category = "Money making", name = "Schockie's tabber")
public final class Main extends Script implements Painting, MessageListening07, InventoryListener {

    private int tabsCreated = 0;
    private static Mode scriptMode;
    private static Node currentNode = null;
    private LocalDateTime startTime;
    private static ArrayList<Node> nodes = new ArrayList<>();

    public Main() {
        Collections.addAll(nodes, new SeekHouse(), new WithdrawClay(), new EnterHouse(), new CraftTabs(), new LeaveHouse());
        scriptMode = null;
    }

    @Override
    public void run() {
        startTime = LocalDateTime.now();
        loop(20, 40);
    }

    @Override
    public void onPaint(Graphics gfx) {
        /*
        gfx.setColor(Color.cyan);

        LocalDateTime now = LocalDateTime.now();

        Long seconds = startTime.until(now, ChronoUnit.SECONDS);
        int[] timeArray = splitToComponentTimes(seconds);

        double tabsPerHour =  Math.round(tabsCreated / seconds.doubleValue() * 3600);

        gfx.drawString("Schockie's TeleTabber", 30, 30);
        gfx.drawString("Runtime: " + timeArray[0] + ":" + timeArray[1] + ":" + timeArray[2], 30 ,40);
        gfx.drawString("State: " + currentNode.getClass().getName(), 30, 50);
        gfx.drawString("Tabs created: " + tabsCreated + " (ph" + tabsPerHour + ")", 30, 60);

        /*int logsY = 30;
        for (String string : InteractionLogManager.getLogs()) {
            gfx.drawString(string, 300, logsY);
            logsY += 10;
        }*/
    }

    @Override
    public void inventoryItemAdded(int id, int count) {
        if (id == 8007) {
            tabsCreated += count;
        }
    }

    @Override
    public void inventoryItemRemoved(int id, int count) {

    }

    @Override
    public void serverMessageReceived(String serverMessage) {
        String[] blackListMessagesHouseOwners = new String[] { "That player is offline", "They do not seem to be at home", "Their house is in", "They have locked their house"};

        for (String message : blackListMessagesHouseOwners) {
            if (serverMessage.contains(message)) {
                HouseHelper.blacklistOwner(HouseHelper.getOwnerName());
                return;
            }
        }

        if (serverMessage.contains("You do not have enough") && serverMessage.contains("to cast this spell.")) {
            System.out.println(serverMessage + " Stopping script");
        }
    }

    private void loop(int min, int max) {
        if (WorldHopper.getWorld() != 330) {
            WorldHopper.changeWorld(330);
            sleep(500,1000);
            Inventory.open();
        }

        new InventoryObserver().addListener(this);

        try {
            while (true) {
                for (final Node node: nodes) {
                    if (node.validate()) {
                        currentNode = node;
                        node.execute();
                        sleep(General.random(10, 20));
                    }
                }
            }
        } catch (Exception ex) {

        }
    }

    private static int[] splitToComponentTimes(long longVal)
    {
        int hours = (int) longVal / 3600;
        int remainder = (int) longVal - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        int[] ints = {hours , mins , secs};
        return ints;
    }

    public enum Mode {
        Varrock,
        House
    }
}
