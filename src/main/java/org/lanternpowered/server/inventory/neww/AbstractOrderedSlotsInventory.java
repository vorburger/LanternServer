/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
import java.util.function.Supplier;

import javax.annotation.Nullable;

/**
 * A ordered children inventory that only supports slots.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractOrderedSlotsInventory extends AbstractOrderedInventory<AbstractSlot> {

    public static Builder<AbstractOrderedSlotsInventory> builder() {
        return new Builder();
    }

    @Nullable private List<AbstractSlot> slots;
    @Nullable private Object2IntMap<AbstractSlot> slotsToIndex;
    @Nullable private List<AbstractSlot> slotsWithPriority;

    void init(List<? extends AbstractSlot> slots) {
        final Object2IntMap<AbstractSlot> slotsToIndex = new Object2IntOpenHashMap<>();
        slotsToIndex.defaultReturnValue(INVALID_INDEX);
        for (int i = 0; i < slots.size(); i++) {
            slotsToIndex.put(slots.get(i), i);
        }
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex);
        this.inventoryToIndex = this.slotsToIndex; // Reuse the slot to index map
        this.slots = (List<AbstractSlot>) slots;
        init();
    }

    @Override
    protected List<AbstractSlot> getChildren() {
        return this.slotsWithPriority != null ? this.slotsWithPriority :
                this.slots != null ? this.slots : Collections.emptyList();
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return getChildren();
    }

    @Override
    Object2IntMap<AbstractSlot> getSlotsToIndexMap() {
        return this.slotsToIndex == null ? Object2IntMaps.emptyMap() : this.slotsToIndex;
    }

    public static final class Builder<T extends AbstractOrderedSlotsInventory>
            extends AbstractArchetypeBuilder<T, AbstractOrderedSlotsInventory, Builder<T>> {

        private final List<LanternInventoryArchetype<? extends AbstractSlot>> slots = new ArrayList<>();
        private int freeSlotStart;

        private Builder() {
        }

        @Override
        public <N extends AbstractOrderedSlotsInventory> Builder<N> typeSupplier(Supplier<N> supplier) {
            return (Builder) super.typeSupplier(supplier);
        }

        /**
         * Adds the {@link LanternInventoryArchetype} to the first free slot.
         *
         * @param slotArchetype The slot archetype
         * @return This builder, for chaining
         */
        public Builder<T> slot(LanternInventoryArchetype<? extends AbstractSlot> slotArchetype) {
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
        public Builder<T> slot(int index, LanternInventoryArchetype<? extends AbstractSlot> slotArchetype) {
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
        public Builder<T> slots(Iterable<LanternInventoryArchetype<? extends AbstractSlot>> slotArchetypes) {
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
