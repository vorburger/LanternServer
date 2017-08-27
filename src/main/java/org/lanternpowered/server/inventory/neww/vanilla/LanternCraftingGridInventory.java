package org.lanternpowered.server.inventory.neww.vanilla;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.ContainerViewListener;
import org.lanternpowered.server.inventory.InventoryCloseListener;
import org.lanternpowered.server.inventory.neww.AbstractGridInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
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
}
