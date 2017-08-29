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
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * A base class for all the {@link Inventory}s that have multiple children,
 * this should be every {@link Inventory} except {@link EmptyInventory}
 * or {@link Slot}.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractChildrenInventory<C extends AbstractMutableInventory> extends AbstractMutableInventory {

    @Nullable Object2IntMap<C> inventoryToIndex;

    /**
     * Gets a {@link List} with all the children
     * in this inventory.
     *
     * @return The children list
     */
    protected abstract List<C> getChildren();

    @Nullable
    @Override
    AbstractInventory getChild(int index) {
        final List<C> children = getChildren();
        return index < 0 || index >= children.size() ? null : children.get(index);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    int getChildIndex(AbstractInventory inventory) {
        if (this.inventoryToIndex == null) {
            this.inventoryToIndex = new Object2IntOpenHashMap<>();
            this.inventoryToIndex.defaultReturnValue(AbstractOrderedInventory.INVALID_INDEX);
            final List<C> children = getChildren();
            for (int i = 0; i < children.size(); i++) {
                this.inventoryToIndex.put(children.get(i), i);
            }
        }
        return this.inventoryToIndex.get(inventory);
    }

    @Override
    void close() {
        super.close();
        getChildren().forEach(AbstractMutableInventory::close);
    }

    @Override
    void addViewer(Viewer viewer, LanternContainer container) {
        super.addViewer(viewer, container);
        getChildren().forEach(child -> child.addViewer(viewer, container));
    }

    @Override
    void removeViewer(Viewer viewer, LanternContainer container) {
        super.removeViewer(viewer, container);
        getChildren().forEach(child -> child.removeViewer(viewer, container));
    }

    @Override
    protected <T extends Inventory> T queryInventories(Predicate<AbstractMutableInventory> predicate) {
        final Set<AbstractMutableInventory> inventories = new LinkedHashSet<>();
        queryInventories(inventories, predicate);
        if (inventories.isEmpty()) {
            return genericEmpty();
        }
        // Construct the result inventory
        final DefaultUnorderedChildrenInventory result = new DefaultUnorderedChildrenInventory();
        result.init(ImmutableList.copyOf(inventories));
        return (T) result;
    }

    void queryInventories(Set<AbstractMutableInventory> inventories, Predicate<AbstractMutableInventory> predicate) {
        for (AbstractMutableInventory child : getChildren()) {
            if (predicate.test(child)) {
                inventories.add(child);
            }
            if (child instanceof AbstractChildrenInventory) {
                ((AbstractChildrenInventory) child).queryInventories(inventories, predicate);
            } else {
                inventories.addAll(child.queryInventories(predicate));
            }
        }
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        getChildren().forEach(child -> child.setCarrier(carrier));
    }

    @Override
    public void addChangeListener(SlotChangeListener listener) {
        getChildren().forEach(child -> child.addChangeListener(listener));
    }

    @Override
    public void clear() {
        getChildren().forEach(AbstractMutableInventory::clear);
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack) {
        return InventoryTransactionResult.builder().type(InventoryTransactionResult.Type.FAILURE).reject(stack).build();
    }

    @Override
    public int size() {
        int size = 0;
        for (AbstractMutableInventory child : getChildren()) {
            size += child.size();
        }
        return size;
    }

    @Override
    public int totalItems() {
        int totalItems = 0;
        for (AbstractMutableInventory child : getChildren()) {
            totalItems += child.totalItems();
        }
        return totalItems;
    }

    @Override
    public int capacity() {
        return getSlotInventories().size();
    }

    @Override
    public boolean hasChildren() {
        return !getChildren().isEmpty();
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        if (inventory == this) {
            return true;
        }
        for (AbstractMutableInventory child : getChildren()) {
            if (child == inventory || child.containsInventory(inventory)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<Inventory> iterator() {
        return (Iterator) getChildren().iterator();
    }

    @Override
    public boolean contains(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (AbstractMutableInventory child : getChildren()) {
            if (child.contains(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (AbstractMutableInventory inventory : getChildren()) {
            if (inventory.containsAny(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(ItemType type) {
        checkNotNull(type, "type");
        for (AbstractMutableInventory inventory : getChildren()) {
            if (inventory.contains(type)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (AbstractMutableInventory child : getChildren()) {
            if (child.isValidItem(stack)) {
                return true;
            }
        }
        return false;
    }

    // The max stack size can only be modified specifically for slots,
    // so use the default value and disable modifying the max stack size

    @Override
    public int getMaxStackSize() {
        return AbstractSlot.DEFAULT_MAX_STACK_SIZE;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        for (AbstractMutableInventory inventory : getChildren()) {
            final Optional<ItemStack> itemStack = inventory.poll(matcher);
            if (itemStack.isPresent()) {
                return itemStack;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return Optional.empty();
        }
        ItemStack stack = null;
        for (AbstractInventory inventory : getChildren()) {
            // Check whether the slot a item contains
            if (stack == null) {
                stack = inventory.poll(limit, matcher).orElse(null);
                if (stack != null) {
                    if (stack.getQuantity() >= limit) {
                        return Optional.of(stack);
                    } else {
                        limit -= stack.getQuantity();
                        if (!(matcher instanceof SimilarItemMatcher)) {
                            matcher = new SimilarItemMatcher(stack);
                        }
                    }
                }
            } else {
                final Optional<ItemStack> optItemStack = inventory.poll(limit, matcher);
                if (optItemStack.isPresent()) {
                    final int stackSize = optItemStack.get().getQuantity();
                    limit -= stackSize;
                    stack.setQuantity(stack.getQuantity() + stackSize);
                    if (limit <= 0) {
                        return Optional.of(stack);
                    }
                }
            }
        }
        return Optional.ofNullable(stack);
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        for (AbstractMutableInventory inventory : getChildren()) {
            final Optional<ItemStack> itemStack = inventory.peek(matcher);
            if (itemStack.isPresent()) {
                return itemStack;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return Optional.empty();
        }
        ItemStack stack = null;
        for (AbstractMutableInventory inventory : getChildren()) {
            // Check whether the slot a item contains
            if (stack == null) {
                stack = inventory.peek(limit, matcher).orElse(null);
                if (stack != null) {
                    if (stack.getQuantity() >= limit) {
                        return Optional.of(stack);
                    } else {
                        limit -= stack.getQuantity();
                        if (!(matcher instanceof SimilarItemMatcher)) {
                            matcher = new SimilarItemMatcher(stack);
                        }
                    }
                }
            } else {
                int peekedStackSize = 0;
                // Check whether the inventory a slot is to avoid
                // boxing/unboxing and cloning the item stack
                if (inventory instanceof Slot) {
                    final ItemStack stack1 = ((AbstractSlot) inventory).getRawItemStack();
                    if (stack1 != null && matcher.test(stack1)) {
                        peekedStackSize = Math.min(((Slot) inventory).getStackSize(), limit);
                    }
                } else {
                    final Optional<ItemStack> optItemStack = inventory.peek(limit, matcher);
                    if (optItemStack.isPresent()) {
                        peekedStackSize = optItemStack.get().getQuantity();
                    }
                }
                if (peekedStackSize > 0) {
                    limit -= peekedStackSize;
                    stack.setQuantity(stack.getQuantity() + peekedStackSize);
                    if (limit <= 0) {
                        return Optional.of(stack);
                    }
                }
            }
        }
        return Optional.ofNullable(stack);
    }

    @Override
    public FastOfferResult offerFast(ItemStack stack) {
        checkNotNull(stack, "stack");
        final Set<Inventory> processed = new HashSet<>();
        final Inventory inventory = queryAny(stack);
        if (inventory instanceof AbstractChildrenInventory) {
            final FastOfferResult offerResult = ((AbstractChildrenInventory) inventory).offerFast(stack, processed, true);
            final Optional<ItemStack> rejectedItem = offerResult.getRejectedItem();
            if (!rejectedItem.isPresent()) {
                return offerResult;
            }
            stack = rejectedItem.get();
        }
        return offerFast(stack, processed, false);
    }

    private FastOfferResult offerFast(ItemStack stack, Set<Inventory> processed, boolean add) {
        FastOfferResult offerResult = null;
        for (AbstractMutableInventory inventory : getChildren()) {
            if (!add && processed.contains(inventory)) {
                continue;
            }
            offerResult = inventory.offerFast(stack);
            final Optional<ItemStack> rejectedItem = offerResult.getRejectedItem();
            if (!rejectedItem.isPresent()) {
                return offerResult;
            }
            stack = rejectedItem.get();
            if (add) {
                processed.add(inventory);
            }
        }
        if (offerResult == null) {
            return new FastOfferResult(stack, false);
        }
        return offerResult;
    }

    /**
     * A {@link ItemStack} matcher that matches stacks that similar, internal use only.
     */
    private static final class SimilarItemMatcher implements Predicate<ItemStack> {

        private final ItemStack itemStack;

        SimilarItemMatcher(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public boolean test(ItemStack itemStack) {
            return ((LanternItemStack) this.itemStack).similarTo(itemStack);
        }
    }
}
