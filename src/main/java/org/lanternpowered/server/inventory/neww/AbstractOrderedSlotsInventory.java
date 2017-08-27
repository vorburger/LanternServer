package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * A ordered children inventory that only supports slots.
 */
public abstract class AbstractOrderedSlotsInventory extends AbstractOrderedInventory<AbstractSlot> {

    public static Builder builder() {
        return new Builder();
    }

    @Nullable private List<AbstractSlot> slots;
    @Nullable private Object2IntMap<AbstractSlot> slotsToIndex;

    void init(List<AbstractSlot> slots) {
        final Object2IntMap<AbstractSlot> slotsToIndex = new Object2IntOpenHashMap<>();
        slotsToIndex.defaultReturnValue(INVALID_INDEX);
        for (int i = 0; i < slots.size(); i++) {
            slotsToIndex.put(slots.get(i), i);
        }
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex);
        this.slots = slots;
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

    public static final class Builder {

        private final List<AbstractSlot> slots = new ArrayList<>();
        private int freeSlotStart;

        private Builder() {
        }

        /**
         * Adds the {@link AbstractSlot} to the first free slot.
         *
         * @param slot The slot
         * @return This builder, for chaining
         */
        public Builder slot(Supplier<AbstractSlot> slot) {
            return slot(slot.get());
        }

        /**
         * Adds the {@link AbstractSlot} to the first free slot.
         *
         * @param slot The slot
         * @return This builder, for chaining
         */
        public Builder slot(AbstractSlot slot) {
            int index = -1;
            for (int i = this.freeSlotStart; i < this.slots.size(); i++) {
                if (this.slots.get(i) == null) {
                    index = i;
                }
            }
            if (index == -1) {
                this.slots.add(slot);
                this.freeSlotStart = this.slots.size();
            } else {
                this.slots.set(index, slot);
                this.freeSlotStart = index + 1;
            }
            return this;
        }

        /**
         * Adds the {@link AbstractSlot} to the specific slot index.
         *
         * @param slot The slot
         * @return This builder, for chaining
         */
        public Builder slot(int index, AbstractSlot slot) {
            checkArgument(index >= 0, "Index %s cannot be negative ", index);
            while (this.slots.size() <= index) {
                this.slots.add(null);
            }
            checkState(this.slots.get(index) == null, "There is already a slot bound at index %s", index);
            this.slots.set(index, slot);
            return this;
        }

        /**
         * Adds the {@link AbstractSlot}s to
         * the first free slots.
         *
         * @param slots The slots
         * @return This builder, for chaining
         */
        public Builder slots(Iterable<AbstractSlot> slots) {
            slots.forEach(this::slot);
            return this;
        }

        /**
         * Constructs the {@link AbstractOrderedSlotsInventory}.
         *
         * @return The constructed inventory
         */
        public AbstractOrderedSlotsInventory build() {
            return build(DefaultOrderedSlotsInventory::new);
        }

        /**
         * Constructs the {@link AbstractOrderedSlotsInventory}.
         *
         * @param inventorySupplier The inventory typeSupplier
         * @param <T> The inventory type
         * @return The constructed inventory
         */
        public <T extends AbstractOrderedSlotsInventory> T build(Supplier<T> inventorySupplier) {
            for (int i = 0; i < this.slots.size(); i++) {
                checkState(this.slots.get(i) != null,
                        "Slot isn't set at index %s, the size of the inventory is expanded up to %s", i, this.slots.size());
            }
            final T inventory = inventorySupplier.get();
            inventory.init(ImmutableList.copyOf(this.slots));
            return inventory;
        }
    }
}
