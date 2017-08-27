package org.lanternpowered.server.inventory.neww.vanilla;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.ContainerViewListener;
import org.lanternpowered.server.inventory.InventoryCloseListener;
import org.lanternpowered.server.inventory.neww.AbstractGridInventory;
import org.lanternpowered.server.inventory.neww.IInventory;
import org.lanternpowered.server.inventory.slot.SlotChangeListener;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class LanternCraftingGridInventory extends AbstractGridInventory implements CraftingGridInventory {

    @Override
    public Optional<CraftingRecipe> getRecipe(World world) {
        return Lantern.getRegistry().getCraftingRecipeRegistry().findMatchingRecipe(this, world);
    }

    // TODO: Move/remove

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
