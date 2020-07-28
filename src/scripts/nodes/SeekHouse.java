package scripts.nodes;

import scripts.helpers.HouseHelper;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Player;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSInterfaceChild;
import org.tribot.api2007.types.RSInterfaceComponent;
import org.tribot.api2007.types.RSObject;
import scripts.Areas;

import java.util.ArrayList;
import java.util.Collections;

import static org.tribot.api.General.sleep;

public class SeekHouse extends Node {
    @Override
    public void execute() throws Exception {
        while (isLoading() || getSingleObject("House Advertisement") == null) {
            sleep(10);
        }

        RSObject advert = getSingleObject("House Advertisement");

        approachAndInteract(advert, "View", 0);

        while (Interfaces.get(52, 9 ) == null) {
            sleep(10);
        }

        sleep(200, 1500);

        RSInterface closeButton = Interfaces.get(52, 3, 13);
        // RSInterfaceChild filterCheckbox = Interfaces.get(52, 28);

        // filterCheckbox.click();
        sleep(3000, 7000);
        scrapeOwnerNames();
        closeButton.click();
    }

    @Override
    public boolean validate() {
        return HouseHelper.getOwnerName().isEmpty() && Areas.rimmingtonArea.contains(Player.getPosition());
    }

    private static void scrapeOwnerNames() throws Exception {
        ArrayList<String> ownerNames = new ArrayList<String>();

        RSInterfaceChild playerHousesInterface = Interfaces.get(52, 9 );

        if (playerHousesInterface.isHidden()) {
            throw new Exception("Advertisement interface not open.");
        }

        for (RSInterfaceComponent listEntry : playerHousesInterface.getChildren()) {
            String playerName = listEntry.getText();
            if (!playerName.isEmpty() && !playerName.contains(" ")) {
                ownerNames.add(playerName);
            }
        }

        Collections.reverse(ownerNames);
        HouseHelper.setOwnerNames(ownerNames);
    }
}
