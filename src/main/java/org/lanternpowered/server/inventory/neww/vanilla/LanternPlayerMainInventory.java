package org.lanternpowered.server.inventory.neww.vanilla;

import org.lanternpowered.server.inventory.ContainerViewListener;
import org.lanternpowered.server.inventory.InventoryCloseListener;
import org.lanternpowered.server.inventory.neww.AbstractGridInventory;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
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

    @Override public void addViewListener(ContainerViewListener listener) {

    }

    @Override public void addCloseListener(InventoryCloseListener listener) {

    }
}
