package org.lanternpowered.server.inventory.neww;

import org.spongepowered.api.item.inventory.type.InventoryColumn;

import java.util.List;

public abstract class AbstractInventoryColumn extends AbstractInventory2D implements InventoryColumn {

    @Override
    void init(List<AbstractSlot> slots) {
        super.init(slots, slots.size(), 1);
    }
}
