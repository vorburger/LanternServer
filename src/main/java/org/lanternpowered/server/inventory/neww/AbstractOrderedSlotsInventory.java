package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A ordered children inventory that only supports slots.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractOrderedSlotsInventory extends AbstractOrderedInventory<AbstractSlot> {

    public static Builder builder() {
        return new Builder();
    }

    @Nullable private List<AbstractSlot> slots;
    @Nullable private Object2IntMap<AbstractSlot> slotsToIndex;

    void init(List<? extends AbstractSlot> slots) {
        final Object2IntMap<AbstractSlot> slotsToIndex = new Object2IntOpenHashMap<>();
        slotsToIndex.defaultReturnValue(INVALID_INDEX);
        for (int i = 0; i < slots.size(); i++) {
            slotsToIndex.put(slots.get(i), i);
        }
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex);
        this.inventoryToIndex = this.slotsToIndex; // Reuse the slot to index map
        this.slots = (List<AbstractSlot>) slots;
    }

    @Override
    protected List<AbstractSlot> getChildren() {
        return this.slots == null ? Collections.emptyList() : this.slots;
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return getChildren();
    }

    @Override
    Object2IntMap<AbstractSlot> getSlotsToIndexMap() {
        return this.slotsToIndex == null ? Object2IntMaps.emptyMap() : this.slotsToIndex;
    }

    public static final class Builder<T extends AbstractOrderedSlotsInventory> extends AbstractBuilder<T,AbstractOrderedSlotsInventory,Builder<T>> {

        private final List<LanternInventoryArchetype<? extends AbstractSlot>> slots = new ArrayList<>();
        private int freeSlotStart;

        private Builder() {
        }

        /**
         * Adds the {@link LanternInventoryArchetype} to the first free slot.
         *
         * @param slotArchetype The slot archetype
         * @return This builder, for chaining
         */
        public Builder slot(LanternInventoryArchetype<? extends AbstractSlot> slotArchetype) {
            int index = -1;
            for (int i = this.freeSlotStart; i < this.slots.size(); i++) {
                if (this.slots.get(i) == null) {
                    index = i;
                }
            }
            if (index == -1) {
                this.slots.add(slotArchetype);
                this.freeSlotStart = this.slots.size();
            } else {
                this.slots.set(index, slotArchetype);
                this.freeSlotStart = index + 1;
            }
            return this;
        }

        /**
         * Adds the {@link LanternInventoryArchetype} to the specific slot index.
         *
         * @param slotArchetype The slot archetype
         * @return This builder, for chaining
         */
        public Builder slot(int index, LanternInventoryArchetype<? extends AbstractSlot> slotArchetype) {
            checkArgument(index >= 0, "Index %s cannot be negative ", index);
            while (this.slots.size() <= index) {
                this.slots.add(null);
            }
            checkState(this.slots.get(index) == null, "There is already a slot bound at index %s", index);
            this.slots.set(index, slotArchetype);
            return this;
        }

        /**
         * Adds the {@link LanternInventoryArchetype}s to
         * the first free slots.
         *
         * @param slotArchetypes The slot archetypes
         * @return This builder, for chaining
         */
        public Builder slots(Iterable<LanternInventoryArchetype<? extends AbstractSlot>> slotArchetypes) {
            slotArchetypes.forEach(this::slot);
            return this;
        }

        @Override
        protected void build(T inventory) {
            for (int i = 0; i < this.slots.size(); i++) {
                checkState(this.slots.get(i) != null,
                        "Slot isn't set at index %s, the size of the inventory is expanded up to %s", i, this.slots.size());
            }
            final List<? extends AbstractSlot> slots = this.slots.stream()
                    .map(LanternInventoryArchetype::build)
                    .collect(ImmutableList.toImmutableList());
            inventory.init(slots);
        }

        @Override
        protected Builder<T> copy() {
            final Builder<T> copy = new Builder<>();
            copy.slots.addAll(this.slots);
            copy.freeSlotStart = this.freeSlotStart;
            return copy;
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            return (List) this.slots;
        }
    }
}
