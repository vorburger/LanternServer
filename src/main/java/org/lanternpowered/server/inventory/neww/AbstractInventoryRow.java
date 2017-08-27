package org.lanternpowered.server.inventory.neww;

import org.spongepowered.api.item.inventory.type.InventoryRow;

import java.util.List;

public abstract class AbstractInventoryRow extends AbstractInventory2D implements InventoryRow {

    @Override
    void init(List<? extends AbstractSlot> slots) {
        super.init(slots, 1, slots.size());
    }
}
