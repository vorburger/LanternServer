package org.lanternpowered.server.inventory.neww;

import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.item.inventory.equipment.EquipmentInventory;

import java.lang.ref.WeakReference;
import java.util.Optional;

import javax.annotation.Nullable;

public abstract class AbstractCarriedEquipmentInventory extends AbstractEquipmentInventory implements EquipmentInventory {

    @Nullable private WeakReference<ArmorEquipable> carrier;

    void setCarrier(@Nullable ArmorEquipable carrier) {
        this.carrier = carrier == null ? null : new WeakReference<>(carrier);
    }

    @Override
    public Optional<ArmorEquipable> getCarrier() {
        return this.carrier == null ? Optional.empty() : Optional.ofNullable(this.carrier.get());
    }
}
