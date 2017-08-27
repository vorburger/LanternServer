package org.lanternpowered.server.inventory.neww;

import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;

import java.lang.ref.WeakReference;
import java.util.Optional;

import javax.annotation.Nullable;

public abstract class AbstractCarriedEquipmentInventory extends AbstractEquipmentInventory implements EquipmentInventory {

    @Nullable private WeakReference<ArmorEquipable> carrier;

    @Override
    public Optional<ArmorEquipable> getCarrier() {
        return this.carrier == null ? Optional.empty() : Optional.ofNullable(this.carrier.get());
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        // Only ArmorEquipable carriers are supported by this inventory
        this.carrier = carrier instanceof ArmorEquipable ? new WeakReference<>((ArmorEquipable) carrier) : null;
    }
}
