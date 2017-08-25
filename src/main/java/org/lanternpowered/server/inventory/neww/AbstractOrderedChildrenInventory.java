package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public abstract class AbstractOrderedChildrenInventory extends LanternOrderedInventory<AbstractMutableInventory> {

    @Nullable private List<AbstractMutableInventory> children;
    @Nullable private List<AbstractSlot> slots;
    @Nullable private Object2IntMap<AbstractSlot> slotsToIndex;

    /**
     * Initializes this ordered children inventory.
     *
     * @param children The children
     */
    void init(List<AbstractMutableInventory> children) {
        this.children = children;
        final ImmutableList.Builder<AbstractSlot> slotsBuilder = ImmutableList.builder();
        final Object2IntMap<AbstractSlot> slotsToIndex = new Object2IntOpenHashMap<>();
        slotsToIndex.defaultReturnValue(INVALID_INDEX);
        int index = 0;
        for (AbstractMutableInventory inventory : children) {
            if (inventory instanceof AbstractSlot) {
                final AbstractSlot slot = (AbstractSlot) inventory;
                slotsBuilder.add(slot);
                slotsToIndex.put(slot, index++);
            } else if (inventory instanceof AbstractOrderedChildrenInventory) {
                final AbstractOrderedChildrenInventory childrenInventory = (AbstractOrderedChildrenInventory) inventory;
                checkState(childrenInventory.slots != null, "Attempted to add a children inventory that is not initialized yet.");
                childrenInventory.slots.forEach(slot -> {

                });
            }
        }
        this.slots = slotsBuilder.build();
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex);
    }

    @Override
    protected List<AbstractMutableInventory> getChildren() {
        return this.children == null ? Collections.emptyList() : this.children;
    }

    @Override
    Object2IntMap<AbstractSlot> getSlotsToIndexMap() {
        return this.slotsToIndex == null ? Object2IntMaps.emptyMap() : this.slotsToIndex;
    }
}
