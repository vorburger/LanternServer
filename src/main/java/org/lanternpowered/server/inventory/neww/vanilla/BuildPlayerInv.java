package org.lanternpowered.server.inventory.neww.vanilla;

import org.lanternpowered.server.inventory.neww.AbstractGridInventory;
import org.lanternpowered.server.inventory.neww.AbstractOrderedSlotsInventory;
import org.lanternpowered.server.inventory.neww.DefaultSlot;

public class BuildPlayerInv {

    public static void test() {
        // Step 1: Construct the hotbar
        final AbstractOrderedSlotsInventory.Builder hotbarBuilder = AbstractOrderedSlotsInventory.builder();
        // In case of a row or column, the index also represents the x or y position
        for (int i = 0; i < 9; i++) {
            hotbarBuilder.slot(DefaultSlot::new);
        }
        final LanternHotbarInventory hotbarInventory = hotbarBuilder.build(LanternHotbarInventory::new);
        // Step 2: Construct the main inventory without the hotbar
        final AbstractGridInventory.Builder mainWithoutHotbarBuilder = AbstractGridInventory.builder();
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                mainWithoutHotbarBuilder.slot(x, y, DefaultSlot::new);
            }
        }
        final AbstractGridInventory mainWithoutHotbarInventory = mainWithoutHotbarBuilder.build();
        // Step 3: Construct a grid of the main with hotbar
        final AbstractGridInventory.Builder mainBuilder = AbstractGridInventory.builder();
        for (int y = 0; y < 3; y++) {
            mainBuilder.row(y, mainWithoutHotbarInventory.getRow(y).get());
        }
        mainBuilder.row(3, hotbarInventory); // Set the hotbar inventory at the third row
        final LanternPlayerMainInventory mainInventory = mainBuilder.build(LanternPlayerMainInventory::new);
    }
}
