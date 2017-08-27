package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import org.lanternpowered.server.data.property.PropertyKeySetter;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.function.Consumer;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class LanternInventoryBuilder implements Inventory.Builder {

    @Nullable private LanternInventoryArchetype<?> inventoryArchetype;
    @Nullable private AbstractBuilder<?,?,?> builder;
    @Nullable private Carrier carrier;

    @Override
    public Inventory.Builder from(Inventory value) {
        checkNotNull(value, "value");
        this.inventoryArchetype = (LanternInventoryArchetype<?>) value.getArchetype();
        this.builder = null; // Regenerate the builder if needed
        return this;
    }

    @Override
    public Inventory.Builder reset() {
        this.inventoryArchetype = null;
        this.builder = null;
        this.carrier = null;
        return this;
    }

    @Override
    public Inventory.Builder of(InventoryArchetype archetype) {
        checkNotNull(archetype, "archetype");
        this.inventoryArchetype = (LanternInventoryArchetype<?>) archetype;
        this.builder = null; // Regenerate the builder if needed
        return this;
    }

    @Override
    public Inventory.Builder property(String name, InventoryProperty<?,?> property) {
        checkNotNull(name, "name");
        checkNotNull(property, "property");
        if (this.inventoryArchetype == null) {
            return this;
        }
        final InventoryProperty<String, ?> property1 = (InventoryProperty<String, ?>) property;
        PropertyKeySetter.setKey(property1, name); // Modify the name of the property
        if (this.builder == null) {
            this.builder = this.inventoryArchetype.builder.copy();
        }
        this.builder.property(property1);
        return this;
    }

    @Override
    public Inventory.Builder withCarrier(Carrier carrier) {
        checkNotNull(carrier, "carrier");
        this.carrier = carrier;
        return this;
    }

    @Override
    public Inventory build(Object plugin) {
        final PluginContainer pluginContainer = checkPlugin(plugin, "plugin");
        checkState(this.inventoryArchetype != null, "The inventory archetype must be set");
        final AbstractInventory inventory;
        if (this.builder != null) {
            inventory = this.builder.build();
        } else {
            inventory = this.inventoryArchetype.builder.build();
        }
        if (inventory instanceof AbstractMutableInventory && this.carrier != null) {
            final AbstractMutableInventory mutableInventory = (AbstractMutableInventory) inventory;
            mutableInventory.setCarrier(this.carrier);
            mutableInventory.setPlugin(pluginContainer);
        }
        return inventory;
    }

    @Override
    public <E extends InteractInventoryEvent> Inventory.Builder listener(Class<E> type, Consumer<E> listener) {
        return this; // TODO
    }

    @Override
    public Inventory.Builder forCarrier(Carrier carrier) {
        return this; // TODO
    }

    @Override
    public Inventory.Builder forCarrier(Class<? extends Carrier> carrier) {
        return this; // TODO
    }
}
