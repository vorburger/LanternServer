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
public abstract class AbstractUnorderedSlotsInventory extends AbstractChildrenInventory<AbstractSlot> {

    @Nullable private List<AbstractSlot> slots;

    void init(List<? extends AbstractSlot> slots) {
        this.slots = (List<AbstractSlot>) slots;
    }

    @Override
    protected List<AbstractSlot> getChildren() {
        return (List) getSlotInventories();
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return this.slots == null ? Collections.emptyList() : this.slots;
    }
}
