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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.inventory.type.LanternOrderedSlotsInventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.ItemStack;

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

    public static Builder<LanternOrderedSlotsInventory> builder() {
        return new Builder<>().typeSupplier(LanternOrderedSlotsInventory::new);
    }

    @Nullable private List<AbstractSlot> slots;
    @Nullable private Object2IntMap<AbstractSlot> slotsToIndex;
    @Nullable private List<AbstractSlot> slotsWithPriority;

    void init(List<? extends AbstractSlot> slots, @Nullable List<? extends AbstractSlot> prioritizedSlots) {
        final Object2IntMap<AbstractSlot> slotsToIndex = new Object2IntOpenHashMap<>();
        slotsToIndex.defaultReturnValue(INVALID_INDEX);
        for (int i = 0; i < slots.size(); i++) {
            slotsToIndex.put(slots.get(i), i);
        }
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex);
        this.inventoryToIndex = this.slotsToIndex; // Reuse the slot to index map
        this.slots = (List<AbstractSlot>) slots;
        this.slotsWithPriority = (List<AbstractSlot>) prioritizedSlots;
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
    protected Object2IntMap<AbstractSlot> getSlotsToIndexMap() {
        return this.slotsToIndex == null ? Object2IntMaps.emptyMap() : this.slotsToIndex;
    }

    public static final class Builder<T extends AbstractOrderedSlotsInventory>
            extends AbstractArchetypeBuilder<T, AbstractOrderedSlotsInventory, Builder<T>> {

        private final List<PrioritizedObject<LanternInventoryArchetype<? extends AbstractSlot>>> slots = new ArrayList<>();
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
            return slot(slotArchetype, DEFAULT_PRIORITY);
        }

        /**
         * Adds the {@link LanternInventoryArchetype} to the first free slot.
         *
         * @param slotArchetype The slot archetype
         * @return This builder, for chaining
         */
        public Builder<T> slot(LanternInventoryArchetype<? extends AbstractSlot> slotArchetype, int priority) {
            int index = -1;
            for (int i = this.freeSlotStart; i < this.slots.size(); i++) {
                if (this.slots.get(i) == null) {
                    index = i;
                }
            }
            final PrioritizedObject<LanternInventoryArchetype<? extends AbstractSlot>> prioritizedObject =
                    new PrioritizedObject<>(slotArchetype, priority);
            if (index == -1) {
                this.slots.add(prioritizedObject);
                this.freeSlotStart = this.slots.size();
            } else {
                this.slots.set(index, prioritizedObject);
                this.freeSlotStart = index + 1;
            }
            return this;
        }

        /**
         * Adds the {@link LanternInventoryArchetype} to the specific slot index
         * and the specified priority. All the {@link AbstractSlot} indexes will be generated
         * on the insertion order. The priority will only affect the iteration order, this
         * will affect {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param slotArchetype The slot archetype
         * @param priority The priority
         * @return This builder, for chaining
         */
        public Builder<T> slot(int index, LanternInventoryArchetype<? extends AbstractSlot> slotArchetype, int priority) {
            checkArgument(index >= 0, "Index %s cannot be negative ", index);
            while (this.slots.size() <= index) {
                this.slots.add(null);
            }
            checkState(this.slots.get(index) == null, "There is already a slot bound at index %s", index);
            this.slots.set(index, new PrioritizedObject<>(slotArchetype, priority));
            return this;
        }

        /**
         * Adds the {@link LanternInventoryArchetype} to the specific slot index.
         *
         * @param slotArchetype The slot archetype
         * @return This builder, for chaining
         */
        public Builder<T> slot(int index, LanternInventoryArchetype<? extends AbstractSlot> slotArchetype) {
            return slot(index, slotArchetype, DEFAULT_PRIORITY);
        }

        /**
         * Adds the {@link LanternInventoryArchetype}s to the first free slots and the
         * specified priority. All the {@link AbstractSlot} indexes will be generated
         * on the insertion order. The priority will only affect the iteration order, this
         * will affect {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param slotArchetypes The slot archetypes
         * @param priority The priority
         * @return This builder, for chaining
         */
        public Builder<T> slots(Iterable<LanternInventoryArchetype<? extends AbstractSlot>> slotArchetypes, int priority) {
            slotArchetypes.forEach(slotArchetype -> slot(slotArchetype, priority));
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
            final ImmutableList<PrioritizedObject<? extends AbstractSlot>> prioritizedChildrenObjects = this.slots.stream()
                    .map(e -> {
                        final AbstractSlot inventory1 = e.object.build();
                        inventory1.setParentSafely(inventory);
                        return new PrioritizedObject<>(inventory1, e.priority);
                    })
                    .collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractSlot> slots = prioritizedChildrenObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractSlot> prioritizedSlots = prioritizedChildrenObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init(slots, prioritizedSlots);
        }

        @Override
        protected void copyTo(Builder<T> copy) {
            super.copyTo(copy);
            copy.slots.addAll(this.slots);
            copy.freeSlotStart = this.freeSlotStart;
        }

        @Override
        protected Builder<T> newBuilder() {
            return new Builder<>();
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            return (List) this.slots;
        }
    }
}
