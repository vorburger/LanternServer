package org.lanternpowered.server.inventory.vanilla.block;

import org.lanternpowered.server.block.tile.ITileEntityInventory;
import org.lanternpowered.server.inventory.type.slot.LanternFilteringSlot;
import org.spongepowered.api.block.tileentity.Jukebox;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.item.inventory.Carrier;

import java.util.Optional;

import javax.annotation.Nullable;

public class JukeboxInventory extends LanternFilteringSlot implements ITileEntityInventory {

    @Nullable private TileEntityCarrier carrier;

    @Override
    protected void queueUpdate() {
        super.queueUpdate();
        // Stop the record if it's already playing,
        // don't eject the current one, who's interacting
        // with the inventory should handle that
        if (this.carrier instanceof Jukebox) {
            ((Jukebox) this.carrier).stopRecord();
        }
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        if (carrier instanceof TileEntityCarrier) {
            this.carrier = (TileEntityCarrier) carrier;
        }
    }

    @Override
    public Optional<TileEntityCarrier> getTileEntity() {
        return Optional.ofNullable(this.carrier);
    }
}
