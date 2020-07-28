package scripts;

import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.*;
import scripts.external.InventoryListener;
import scripts.external.InventoryObserver;
import scripts.helpers.HouseHelper;
import scripts.nodes.*;
import org.tribot.api.General;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.Painting;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;

@ScriptManifest(authors = { "Schockie" }, category = "Money making", name = "Schockie's tabber", version = 0.1)
public final class Main extends Script implements Painting, MessageListening07, InventoryListener {

    private int tabsCreated = 0;
    private static Mode scriptMode = Mode.Varrock;
    private static Node currentNode = null;
    private static LocalDateTime startTime;
    private static ArrayList<Node> nodes = new ArrayList<>();
    private static boolean phialsVisible = true;
    private static int magicXpStartedAt;
    private static boolean nothingInterestingHappens = false;
    private static boolean cannotAccessHouse = false;

    public Main() {
        Collections.addAll(nodes, new SeekHouse(), new WithdrawClay(), new EnterHouse(), new CraftTabs(), new LeaveHouse());
        scriptMode = null;
    }

    public static Mode getScriptMode() {
        return Mode.Varrock;
    }

    public static void setScriptMode(Mode mode) {
        scriptMode = mode;
    }

    public static void setPhialsVisisble(boolean visible) {
        phialsVisible = visible;
    }

    public static boolean getPhialsVisible() {
        return phialsVisible;
    }

    @Override
    public void run() {
        startTime = LocalDateTime.now();
        loop(20, 40);
    }

    @Override
    public void onPaint(Graphics gfx) {
        gfx.setColor(Color.cyan);

        LocalDateTime now = LocalDateTime.now();

        Long seconds = startTime.until(now, ChronoUnit.SECONDS);
        int[] timeRunningArray = splitToComponentTimes(seconds);

        double tabsPerHour =  Math.round(tabsCreated / seconds.doubleValue() * 3600);
        double magicXpPerHour = Math.round(getMagicXpGained() / seconds.doubleValue() * 3600);

        int[] timeUntilNext = splitToComponentTimes(Double.doubleToLongBits((Skills.getXPToNextLevel(Skills.SKILLS.MAGIC) /  (magicXpPerHour / 3600))));

        gfx.drawString("Schockie's TeleTabber", 30, 30);
        gfx.drawString("Runtime: " + timeRunningArray[0] + ":" + timeRunningArray[1] + ":" + timeRunningArray[2], 30 ,40);
        gfx.drawString("State: " + currentNode.getClass().getTypeName(), 30, 50);
        gfx.drawString("Tabs created: " + tabsCreated + " (ph" + tabsPerHour + ")", 30, 60);
        gfx.drawString("Xp gained: " + getMagicXpGained() + " (ph" + magicXpPerHour + ")", 30, 70);
        gfx.drawString("Until lvl: " + timeUntilNext[0] + ":" + timeUntilNext[1] + ":" + timeUntilNext[2], 30 ,80);
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
                cannotAccessHouse = true;
                return;
            }
        }

        if (serverMessage.contains("You do not have enough") && serverMessage.contains("to cast this spell.")) {
            System.out.println(serverMessage + " Stopping script");
        }

        if (serverMessage.contains("Nothing interesting happens.")) {
            nothingInterestingHappens = true;
        }
    }

    private void loop(int min, int max) {
        if (WorldHopper.getWorld() != 330) {
            WorldHopper.changeWorld(330);
            sleep(500,1000);
            Inventory.open();
        }

        magicXpStartedAt = Skills.getXP(Skills.SKILLS.MAGIC);

        new InventoryObserver().addListener(this);
        checkSettings();
        General.useAntiBanCompliance(true);

        try {
            while (true) {
                for (final Node node: nodes) {
                    if (node.validate()) {
                        currentNode = node;
                        System.out.println("Executing: " + currentNode.getClass().getTypeName());
                        node.execute();
                        sleep(General.random(10, 20));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void checkSettings() {
        if (Options.isResizableModeEnabled()) {
            if (!Options.isChatboxTransparent()) {
                Options.setChatboxTransparent(true);
            }
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

    private int getMagicXpGained() {
        int currentXp = Skills.getXP(Skills.SKILLS.MAGIC);
        return currentXp - magicXpStartedAt;
    }

    public static boolean misClickCheck() {
        if (nothingInterestingHappens) {
            nothingInterestingHappens = false;
            return true;
        }

        return false;
    }

    public static boolean cannotAccessHouse() {
        if (cannotAccessHouse) {
            cannotAccessHouse = false;
            return true;
        }

        return false;
    }

    public enum Mode {
        Varrock,
        House
    }
}
