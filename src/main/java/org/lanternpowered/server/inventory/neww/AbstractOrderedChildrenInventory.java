package org.lanternpowered.server.inventory.neww;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public abstract class AbstractOrderedChildrenInventory extends AbstractOrderedInventory<AbstractMutableInventory> {

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
            } else if (inventory instanceof AbstractOrderedInventory) {
                final AbstractOrderedInventory childrenInventory = (AbstractOrderedInventory) inventory;
                for (AbstractSlot slot : childrenInventory.getSlotInventories()) {
                    slotsBuilder.add(slot);
                    slotsToIndex.put(slot, index++);
                }
            } else {
                throw new IllegalArgumentException("All the children inventories must be ordered.");
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
    protected List<AbstractSlot> getSlotInventories() {
        return this.slots == null ? Collections.emptyList() : this.slots;
    }

    @Override
    Object2IntMap<AbstractSlot> getSlotsToIndexMap() {
        return this.slotsToIndex == null ? Object2IntMaps.emptyMap() : this.slotsToIndex;
    }


    public static final class Builder<T extends AbstractOrderedChildrenInventory>
            extends AbstractBuilder<T, AbstractOrderedChildrenInventory, Builder<T>>  {

        private final List<LanternInventoryArchetype<? extends AbstractMutableInventory>> inventories = new ArrayList<>();

        private Builder() {
        }

        /**
         * Adds the {@link InventoryArchetype}.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder inventory(LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype) {
            this.inventories.add(inventoryArchetype);
            return this;
        }

        @Override
        protected void build(AbstractOrderedChildrenInventory inventory) {
            final List<AbstractMutableInventory> inventories = this.inventories.stream()
                    .map(archetype -> archetype.builder.build())
                    .collect(ImmutableList.toImmutableList());
            inventory.init(inventories);
        }

        @Override
        protected Builder<T> copy() {
            final Builder<T> copy = new Builder<>();
            copy.inventories.addAll(this.inventories);
            return copy;
        }
    }
}
