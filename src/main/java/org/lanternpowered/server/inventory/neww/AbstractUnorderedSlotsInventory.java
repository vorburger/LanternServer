package org.lanternpowered.server.inventory.neww;

import org.spongepowered.api.item.inventory.Inventory;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple children {@link Inventory} which is mainly used for query results. All normal
 * {@link Inventory}s will very likely be ordered. The {@link AbstractSlot} will {@link List}
 * will be lazily loaded in this inventory.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractUnorderedSlotsInventory extends AbstractChildrenInventory<AbstractSlot> {

    @Nullable private List<AbstractSlot> slots;

    void init(List<? extends AbstractSlot> slots) {
        this.slots = (List<AbstractSlot>) slots;
    }

    @Override
    protected List<AbstractSlot> getChildren() {
        return (List) getSlotInventories();
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return this.slots == null ? Collections.emptyList() : this.slots;
    }
}
