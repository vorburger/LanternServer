package org.lanternpowered.server.inventory.neww.archetype;

import org.spongepowered.api.item.inventory.Carrier;

import java.util.Optional;

public class ConstructionContext {

    /**
     * Gets the {@link Carrier} that is bound to the inventory.
     *
     * @return The carrier
     */
    public Optional<Carrier> getCarrier() {
        return Optional.empty();
    }
}
