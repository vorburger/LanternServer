package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryDimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanternInventoryArchetypeBuilder implements InventoryArchetype.Builder {

    private final List<LanternInventoryArchetype<?>> archetypes = new ArrayList<>();
    private final Map<Class<?>, InventoryProperty<?,?>> properties = new HashMap<>();

    @Override
    public InventoryArchetype.Builder from(InventoryArchetype value) {
        return null;
    }

    @Override
    public InventoryArchetype.Builder reset() {
        return null;
    }

    @Override
    public InventoryArchetype.Builder property(InventoryProperty<String, ?> property) {
        checkNotNull(property, "property");
        this.properties.put(property.getClass(), property);
        return this;
    }

    @Override
    public InventoryArchetype.Builder with(InventoryArchetype archetype) {
        checkNotNull(archetype, "archetype");
        this.archetypes.add((LanternInventoryArchetype<?>) archetype);
        return this;
    }

    @Override
    public InventoryArchetype.Builder with(InventoryArchetype... archetypes) {
        Arrays.stream(archetypes).forEach(this::with);
        return this;
    }

    @Override
    public InventoryArchetype build(String id, String name) {
        final InventoryDimension inventoryDimension = (InventoryDimension) this.properties.remove(InventoryDimension.class);
        final InventoryCapacity inventoryCapacity = (InventoryCapacity) this.properties.remove(InventoryCapacity.class);
        // Dimension doesn't matter, this means that we can just create a ordered children archetype
        if (inventoryDimension == null) {
            for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                if (archetype.builder instanceof AbstractSlot.Builder) {

                }
            }
        }
        return null;
    }
}
