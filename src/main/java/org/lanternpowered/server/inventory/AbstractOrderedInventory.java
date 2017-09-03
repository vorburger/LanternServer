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

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractOrderedInventory<C extends AbstractMutableInventory>
        extends AbstractChildrenInventory<C> implements IOrderedInventory {

    /**
     * Represents a invalid slot index.
     */
    static final int INVALID_INDEX = -1;

    protected abstract Object2IntMap<AbstractSlot> getSlotsToIndexMap();

    // Supply the slot indexes for the children

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, @Nullable Object key) {
        if (property == SlotIndex.class && child instanceof Slot) {
            final int index = getSlotsToIndexMap().getInt(child);
            return index == INVALID_INDEX ? Optional.empty() : Optional.of(property.cast(SlotIndex.of(index)));
        }
        return super.tryGetProperty(child, property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        final List<T> properties = super.tryGetProperties(child, property);
        if (property == SlotIndex.class && child instanceof Slot) {
            final int index = getSlotsToIndexMap().getInt(child);
            if (index != INVALID_INDEX) {
                properties.add(property.cast(SlotIndex.of(index)));
            }
        }
        return properties;
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index) {
        return getSlot(index).flatMap(Inventory::poll);
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index, int limit) {
        return getSlot(index).flatMap(slot -> slot.poll(limit));
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index) {
        return getSlot(index).flatMap(Inventory::peek);
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index, int limit) {
        return getSlot(index).flatMap(slot -> slot.peek(limit));
    }

    @Override
    public InventoryTransactionResult set(SlotIndex index, ItemStack stack) {
        return getSlot(index).map(slot -> slot.set(stack)).orElse(CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS);
    }

    @Override
    public Optional<Slot> getSlot(SlotIndex slotIndex) {
        checkNotNull(slotIndex, "slotIndex");
        if (slotIndex.getOperator() != Property.Operator.EQUAL || slotIndex.getValue() == null) {
            return Optional.empty();
        }
        return (Optional) getSlot(slotIndex.getValue());
    }

    @Override
    public Optional<ISlot> getSlot(int index) {
        final List<AbstractSlot> slots = getSlotInventories();
        return index < 0 || index >= slots.size() ? Optional.empty() : Optional.ofNullable(slots.get(index));
    }

    @Override
    public int getSlotIndex(Slot slot) {
        return getSlotsToIndexMap().getInt(slot);
    }
}
