package org.lanternpowered.server.inventory.neww;

import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.type.OrderedInventory;

import java.util.Optional;

public interface IOrderedInventory extends IInventory, OrderedInventory {

    /**
     * Gets the {@link Slot} for the specified index.
     *
     * @param index The slot index
     * @return The slot if found
     */
    Optional<ISlot> getSlot(int index);

    /**
     * Gets the index of the {@link Slot} in this ordered inventory,
     * may return {@code -1} if the slot was not found.
     *
     * @param slot The slot
     * @return The slot index
     */
    int getSlotIndex(Slot slot);
}
