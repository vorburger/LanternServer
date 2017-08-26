package org.lanternpowered.server.inventory.neww;

import org.lanternpowered.server.inventory.InventoryPropertyHolder;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class LanternInventoryBuilder implements Inventory.Builder {

    public static final class Context implements InventoryPropertyHolder {

        /**
         * Gets the {@link Carrier} that is bound to the inventory.
         *
         * @return The carrier
         */
        public Optional<Carrier> getCarrier() {
            return Optional.empty();
        }

        @Override
        public Map<String, InventoryProperty<String, ?>> getProperties() {
            return null;
        }

        @Override
        public Optional<InventoryProperty<String, ?>> getProperty(String key) {
            return null;
        }

        @Override
        public <T extends InventoryProperty<String, ?>> Optional<T> getProperty(Class<T> type, String key) {
            return null;
        }

        @Override
        public <T extends InventoryProperty<String, ?>> Optional<T> getProperty(Class<T> property) {
            return Optional.empty();
        }
    }

    @Override
    public Inventory.Builder from(Inventory value) {
        return null;
    }

    @Override
    public Inventory.Builder reset() {
        return null;
    }

    @Override
    public Inventory.Builder of(InventoryArchetype archetype) {
        return null;
    }

    @Override
    public Inventory.Builder property(String name, InventoryProperty<?, ?> property) {
        return null;
    }

    @Override
    public Inventory.Builder withCarrier(Carrier carrier) {
        return null;
    }

    @Override
    public <E extends InteractInventoryEvent> Inventory.Builder listener(Class<E> type, Consumer<E> listener) {
        return null;
    }

    @Override
    public Inventory.Builder forCarrier(Carrier carrier) {
        return null;
    }

    @Override
    public Inventory.Builder forCarrier(Class<? extends Carrier> carrier) {
        return null;
    }

    @Override
    public Inventory build(Object plugin) {
        return null;
    }
}
