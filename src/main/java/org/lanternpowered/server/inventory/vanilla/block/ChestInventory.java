package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.block.tile.ITileEntityInventory;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.CarrierReference;
import org.lanternpowered.server.inventory.IInventory;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.VanillaOpenableInventory;
import org.lanternpowered.server.inventory.client.ChestClientContainer;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Slot;

import java.util.Optional;

public class ChestInventory extends AbstractGridInventory implements VanillaOpenableInventory, ITileEntityInventory {

    private final CarrierReference<TileEntityCarrier> carrierReference = CarrierReference.of(TileEntityCarrier.class);

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        this.carrierReference.set(carrier);
    }

    @Override
    public Optional<TileEntityCarrier> getCarrier() {
        return this.carrierReference.get();
    }

    @Override
    public ClientContainer constructClientContainer0(LanternContainer container) {
        return new ChestClientContainer(TextTranslation.toText(getName()), getRows());
    }

    @Override
    public IInventory getShiftClickTarget(LanternContainer container, Slot slot) {
        return containsInventory(slot) ? VanillaOpenableInventory.super.getShiftClickTarget(container, slot) : this;
    }
}
