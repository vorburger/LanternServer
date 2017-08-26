package org.lanternpowered.server.inventory.neww.archetype;

import org.lanternpowered.server.inventory.InventoryPropertyHolder;
import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.Map;
import java.util.Optional;

public class PostConstructionContext extends ConstructionContext implements InventoryPropertyHolder {

    @Override
    public Map<String, InventoryProperty<String, ?>> getProperties() {
        return null;
    }

    @Override
    public Optional<InventoryProperty<String, ?>> getProperty(String key) {
        return Optional.empty();
    }

    @Override
    public <T extends InventoryProperty<String, ?>> Optional<T> getProperty(Class<T> property, String key) {
        return Optional.empty();
    }

    @Override
    public <T extends InventoryProperty<String, ?>> Optional<T> getProperty(Class<T> property) {
        return Optional.empty();
    }
}
