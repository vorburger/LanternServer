package org.lanternpowered.server.inventory.neww;

import com.google.common.collect.ImmutableList;
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
public abstract class AbstractUnorderedChildrenInventory extends AbstractChildrenInventory<AbstractMutableInventory> {

    @Nullable private List<AbstractMutableInventory> children;
    @Nullable private List<AbstractSlot> slots;

    void init(List<? extends AbstractMutableInventory> children) {
        this.children = (List<AbstractMutableInventory>) children;
    }

    @Override
    protected List<AbstractMutableInventory> getChildren() {
        return this.children == null ? Collections.emptyList() : this.children;
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        if (this.slots != null) {
            return this.slots;
        } else if (this.children == null) {
            return Collections.emptyList();
        }
        // Collect all the slots and cache the result
        final ImmutableList.Builder<AbstractSlot> slots = ImmutableList.builder();
        for (AbstractMutableInventory child : this.children) {
            if (child instanceof AbstractSlot) {
                slots.add((AbstractSlot) child);
            } else {
                slots.addAll(child.getSlotInventories());
            }
        }
        this.slots = slots.build();
        return this.slots;
    }
}
