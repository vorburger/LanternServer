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
package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.block.tile.ITileEntityInventory;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractOrderedSlotsInventory;
import org.lanternpowered.server.inventory.CarrierReference;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.LanternItemStackSnapshot;
import org.lanternpowered.server.inventory.VanillaOpenableInventory;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ContainerProperties;
import org.lanternpowered.server.inventory.client.FurnaceClientContainer;
import org.lanternpowered.server.inventory.type.slot.LanternFuelSlot;
import org.lanternpowered.server.inventory.type.slot.LanternInputSlot;
import org.lanternpowered.server.inventory.type.slot.LanternOutputSlot;
import org.lanternpowered.server.inventory.vanilla.LanternPlayerInventory;
import org.lanternpowered.server.item.recipe.fuel.IFuel;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.slot.InputSlot;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FurnaceInventory extends AbstractOrderedSlotsInventory implements ITileEntityInventory, VanillaOpenableInventory {

    private final CarrierReference<Carrier> carrierReference = CarrierReference.of(Carrier.class);

    private LanternInputSlot inputSlot;
    private LanternFuelSlot fuelSlot;
    private LanternOutputSlot outputSlot;

    private boolean useCachedProgress;

    private double smeltProgress = 0;
    private double fuelProgress = 1;

    public LanternInputSlot getInputSlot() {
        return this.inputSlot;
    }

    public LanternFuelSlot getFuelSlot() {
        return this.fuelSlot;
    }

    public LanternOutputSlot getOutputSlot() {
        return this.outputSlot;
    }

    public void enableCachedProgress() {
        this.useCachedProgress = true;
    }

    public void resetCachedProgress() {
        this.smeltProgress = -1;
        this.fuelProgress = -1;
    }

    @Override
    protected void init() {
        super.init();

        this.inputSlot = query(LanternInputSlot.class).first();
        this.fuelSlot = query(LanternFuelSlot.class).first();
        this.outputSlot = query(LanternOutputSlot.class).first();
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        this.carrierReference.set(carrier);
    }

    @Override
    public Optional<TileEntityCarrier> getCarrier() {
        return this.carrierReference.as(TileEntityCarrier.class);
    }

    @Override
    public IInventory getShiftClickTarget(LanternContainer container, Slot slot) {
        // Use the default behavior in case the slot is located in this inventory
        if (containsInventory(slot)) {
            if (slot instanceof InputSlot) {
                // The input slots uses a different insertion order to the default
                return container.getPlayerInventory().getView(LanternPlayerInventory.View.PRIORITY_MAIN_AND_HOTBAR);
            }
            return VanillaOpenableInventory.super.getShiftClickTarget(container, slot);
        }
        // The item stack should be present
        final ItemStackSnapshot snapshot = LanternItemStackSnapshot.wrap(slot.peek().get()); // Wrap, peek creates a copy
        // Check if the item can be used as a ingredient
        final Optional<SmeltingRecipe> optSmeltingRecipe = Lantern.getRegistry()
                .getSmeltingRecipeRegistry().findMatchingRecipe(snapshot);
        final List<IInventory> inventories = new ArrayList<>();
        if (optSmeltingRecipe.isPresent()) {
            inventories.add(this.inputSlot);
        }
        // Check if the item can be used as a fuel
        final Optional<IFuel> optFuel = Lantern.getRegistry()
                .getFuelRegistry().findMatching(snapshot);
        if (optFuel.isPresent()) {
            inventories.add(this.fuelSlot);
        }
        return inventories.isEmpty() ? VanillaOpenableInventory.super.getShiftClickTarget(container, slot) :
                inventories.size() == 1 ? inventories.get(0) : inventories.get(0).union(inventories.get(1));
    }

    @Override
    public boolean disableShiftClickWhenFull() {
        return false;
    }

    @Override
    public ClientContainer constructClientContainer0(LanternContainer container) {
        final FurnaceClientContainer clientContainer = new FurnaceClientContainer(TextTranslation.toText(getName()));
        // Provide the smelting progress
        clientContainer.bindPropertySupplier(ContainerProperties.SMELT_PROGRESS, () -> {
            final Optional<DataHolder> dataHolder = this.carrierReference.as(DataHolder.class);
            if (!dataHolder.isPresent()) {
                return 0.0;
            }
            double smeltProgress = this.smeltProgress;
            if (!this.useCachedProgress || smeltProgress < 0) {
                smeltProgress = this.smeltProgress =
                        dataHolder.get().get(Keys.PASSED_COOK_TIME).get().doubleValue() /
                                dataHolder.get().get(Keys.MAX_COOK_TIME).get().doubleValue();
            }
            return smeltProgress;
        });
        // Provide the fuel progress
        clientContainer.bindPropertySupplier(ContainerProperties.FUEL_PROGRESS, () -> {
            final Optional<DataHolder> dataHolder = this.carrierReference.as(DataHolder.class);
            if (!dataHolder.isPresent()) {
                return 1.0;
            }
            double fuelProgress = this.fuelProgress;
            if (!this.useCachedProgress || fuelProgress < 0) {
                fuelProgress = this.fuelProgress = 1.0 -
                        dataHolder.get().get(Keys.PASSED_BURN_TIME).get().doubleValue() /
                                dataHolder.get().get(Keys.MAX_BURN_TIME).get().doubleValue();
            }
            return fuelProgress;
        });
        return clientContainer;
    }
}
