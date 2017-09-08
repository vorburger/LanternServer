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

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.inventory.type.LanternOrderedChildrenInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractOrderedChildrenInventory extends AbstractOrderedInventory<AbstractMutableInventory> {

    public static Builder<LanternOrderedChildrenInventory> builder() {
        return new Builder<>().typeSupplier(LanternOrderedChildrenInventory::new);
    }

    public static ViewBuilder<LanternOrderedChildrenInventory> viewBuilder() {
        return new ViewBuilder<>().typeSupplier(LanternOrderedChildrenInventory::new);
    }

    @Nullable private List<AbstractMutableInventory> children;
    @Nullable private List<AbstractSlot> slots;
    @Nullable private Object2IntMap<AbstractSlot> slotsToIndex;

    /**
     * Initializes this ordered children inventory.
     *
     * @param children The children
     */
    void init(List<AbstractMutableInventory> children, @Nullable List<AbstractMutableInventory> prioritizedChildren) {
        this.children = prioritizedChildren == null ? children : prioritizedChildren;
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
        init();
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
    protected Object2IntMap<AbstractSlot> getSlotsToIndexMap() {
        return this.slotsToIndex == null ? Object2IntMaps.emptyMap() : this.slotsToIndex;
    }

    public static final class Builder<T extends AbstractOrderedChildrenInventory>
            extends AbstractArchetypeBuilder<T, AbstractOrderedChildrenInventory, Builder<T>>  {

        private final List<PrioritizedObject<LanternInventoryArchetype<? extends AbstractMutableInventory>>> inventories = new ArrayList<>();

        private Builder() {
        }

        @Override
        public <N extends AbstractOrderedChildrenInventory> Builder<N> typeSupplier(Supplier<N> supplier) {
            return (Builder<N>) super.typeSupplier(supplier);
        }

        /**
         * Adds the {@link InventoryArchetype} with the specified priority. All the
         * {@link AbstractSlot} indexes will be generated on the insertion order. The
         * priority will only affect the iteration order, this will affect
         * {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> inventory(LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype, int priority) {
            this.inventories.add(new PrioritizedObject<>(inventoryArchetype, priority));
            return this;
        }

        /**
         * Adds the {@link InventoryArchetype} with the default priority.
         *
         * @param inventoryArchetype The inventory archetype
         * @return This builder, for chaining
         */
        public Builder<T> inventory(LanternInventoryArchetype<? extends AbstractMutableInventory> inventoryArchetype) {
            return inventory(inventoryArchetype, DEFAULT_PRIORITY);
        }

        @Override
        protected void build(AbstractOrderedChildrenInventory inventory) {
            final ImmutableList<PrioritizedObject<? extends AbstractMutableInventory>> prioritizedChildrenObjects = this.inventories.stream()
                    .map(e -> {
                        final AbstractMutableInventory inventory1 = e.object.build();
                        inventory1.setParentSafely(inventory);
                        return new PrioritizedObject<>(inventory1, e.priority);
                    })
                    .collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractMutableInventory> children = prioritizedChildrenObjects.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractMutableInventory> prioritizedChildren = prioritizedChildrenObjects.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init(children, prioritizedChildren);
        }

        @Override
        protected void copyTo(Builder<T> copy) {
            super.copyTo(copy);
            copy.inventories.addAll(this.inventories);
        }

        @Override
        protected Builder<T> newBuilder() {
            return new Builder<>();
        }

        @Override
        protected List<InventoryArchetype> getArchetypes() {
            return (List) this.inventories;
        }
    }

    public static final class ViewBuilder<T extends AbstractOrderedChildrenInventory>
            extends AbstractViewBuilder<T, AbstractOrderedChildrenInventory, ViewBuilder<T>>  {

        private final List<PrioritizedObject<AbstractMutableInventory>> inventories = new ArrayList<>();

        private ViewBuilder() {
        }

        @Override
        public <N extends AbstractOrderedChildrenInventory> ViewBuilder<N> typeSupplier(Supplier<N> supplier) {
            return (ViewBuilder<N>) super.typeSupplier(supplier);
        }

        /**
         * Adds the {@link AbstractMutableInventory} with the specified priority. All the
         * {@link AbstractSlot} indexes will be generated on the insertion order. The
         * priority will only affect the iteration order, this will affect
         * {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param inventory The inventory
         * @param priority The priority
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventory(Inventory inventory, int priority) {
            this.inventories.add(new PrioritizedObject<>((AbstractMutableInventory) inventory, priority));
            return this;
        }

        /**
         * Adds the {@link AbstractMutableInventory} with the default priority.
         *
         * @param inventory The inventory
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventory(Inventory inventory) {
            return inventory(inventory, DEFAULT_PRIORITY);
        }

        /**
         * Adds the {@link AbstractMutableInventory}s with the specified priority. All the
         * {@link AbstractSlot} indexes will be generated on the insertion order. The
         * priority will only affect the iteration order, this will affect
         * {@link IInventory#offer(ItemStack)}, ... operations.
         *
         * @param inventories The inventories
         * @param priority The priority
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventories(Iterable<? extends Inventory> inventories, int priority) {
            inventories.forEach(inventory -> inventory(inventory, priority));
            return this;
        }

        /**
         * Adds the {@link AbstractMutableInventory} with the default priority.
         *
         * @param inventories The inventories
         * @return This builder, for chaining
         */
        public ViewBuilder<T> inventories(Iterable<? extends Inventory> inventories) {
            inventories.forEach(this::inventory);
            return this;
        }

        @Override
        protected void build(AbstractOrderedChildrenInventory inventory) {
            final ImmutableList<AbstractMutableInventory> children = this.inventories.stream()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            final ImmutableList<AbstractMutableInventory> prioritizedChildren = this.inventories.stream().sorted()
                    .map(e -> e.object).collect(ImmutableList.toImmutableList());
            inventory.init(children, prioritizedChildren);
        }
    }
}
