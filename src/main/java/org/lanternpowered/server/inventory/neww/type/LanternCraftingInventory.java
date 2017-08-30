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
package org.lanternpowered.server.inventory.neww.type;

import com.flowpowered.math.vector.Vector2i;
import org.lanternpowered.server.inventory.neww.AbstractOrderedChildrenInventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;
import org.spongepowered.api.item.inventory.property.SlotPos;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.InventoryColumn;
import org.spongepowered.api.item.inventory.type.InventoryRow;

import java.util.Optional;

public class LanternCraftingInventory extends AbstractOrderedChildrenInventory implements CraftingInventory {

    private CraftingOutput output;
    private CraftingGridInventory grid;

    @Override
    protected void init() {
        super.init();

        // Search for the underlying inventories
        this.output = query(CraftingOutput.class).first();
        this.grid = query(CraftingGridInventory.class).first();
    }

    @Override
    public CraftingGridInventory getCraftingGrid() {
        return this.grid;
    }

    @Override
    public CraftingOutput getResult() {
        return this.output;
    }

    // The CraftingInventory shouldn't be a grid..., just forward
    // all the methods to the actual grid
    // Throw an exception instead?

    @Override
    public int getColumns() {
        return this.grid.getColumns();
    }

    @Override
    public int getRows() {
        return this.grid.getRows();
    }

    @Override
    public Vector2i getDimensions() {
        return this.grid.getDimensions();
    }

    @Override
    public Optional<ItemStack> poll(int x, int y) {
        return this.grid.poll(x, y);
    }

    @Override
    public Optional<ItemStack> poll(int x, int y, int limit) {
        return this.grid.poll(x, y, limit);
    }

    @Override
    public Optional<ItemStack> peek(int x, int y) {
        return this.grid.peek(x, y);
    }

    @Override
    public Optional<ItemStack> peek(int x, int y, int limit) {
        return this.grid.peek(x, y, limit);
    }

    @Override
    public InventoryTransactionResult set(int x, int y, ItemStack stack) {
        return this.grid.set(x, y, stack);
    }

    @Override
    public Optional<Slot> getSlot(int x, int y) {
        return this.grid.getSlot(x, y);
    }

    @Override
    public Optional<InventoryRow> getRow(int y) {
        return this.grid.getRow(y);
    }

    @Override
    public Optional<InventoryColumn> getColumn(int x) {
        return this.grid.getColumn(x);
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos) {
        return this.grid.poll(pos);
    }

    @Override
    public Optional<ItemStack> poll(SlotPos pos, int limit) {
        return this.grid.poll(pos, limit);
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos) {
        return this.grid.peek(pos);
    }

    @Override
    public Optional<ItemStack> peek(SlotPos pos, int limit) {
        return this.grid.peek(pos, limit);
    }

    @Override
    public InventoryTransactionResult set(SlotPos pos, ItemStack stack) {
        return this.grid.set(pos, stack);
    }

    @Override
    public Optional<Slot> getSlot(SlotPos pos) {
        return this.grid.getSlot(pos);
    }
}
