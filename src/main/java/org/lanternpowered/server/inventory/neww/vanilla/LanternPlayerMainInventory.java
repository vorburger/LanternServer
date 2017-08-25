package org.lanternpowered.server.inventory.neww.vanilla;

import org.lanternpowered.server.inventory.ContainerViewListener;
import org.lanternpowered.server.inventory.InventoryCloseListener;
import org.lanternpowered.server.inventory.neww.AbstractGridInventory;
import org.lanternpowered.server.inventory.slot.SlotChangeListener;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.GridInventory;

public class LanternPlayerMainInventory extends AbstractGridInventory implements MainPlayerInventory {

    private LanternHotbarInventory hotbar;
    private AbstractGridInventory grid;

    protected void init() {
        this.hotbar = query(LanternHotbarInventory.class).first();
        this.grid = query(AbstractGridInventory.class).first();
    }

    @Override
    public Hotbar getHotbar() {
        return this.hotbar;
    }

    @Override
    public GridInventory getGrid() {
        return this.grid;
    }

    // TODO

    @Override public void addChangeListener(SlotChangeListener listener) {

    }

    @Override public void addViewListener(ContainerViewListener listener) {

    }

    @Override public void addCloseListener(InventoryCloseListener listener) {

    }

    @Override public <T extends Inventory> T first() {
        return null;
    }

    @Override public <T extends Inventory> T next() {
        return null;
    }

    @Override public InventoryTransactionResult offer(ItemStack stack) {
        return null;
    }

    @Override public InventoryTransactionResult set(ItemStack stack) {
        return null;
    }
}
