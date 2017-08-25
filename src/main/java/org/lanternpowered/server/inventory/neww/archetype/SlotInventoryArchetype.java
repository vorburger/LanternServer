package org.lanternpowered.server.inventory.neww.archetype;

import org.lanternpowered.server.inventory.neww.AbstractInventory;
import org.lanternpowered.server.inventory.neww.LanternInventoryBuilder;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.property.AcceptsItems;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SlotInventoryArchetype extends AbstractInventoryArchetype {

    public SlotInventoryArchetype(String pluginId, String name) {
        super(pluginId, name);
    }

    public SlotInventoryArchetype(String pluginId, String id, String name) {
        super(pluginId, id, name);
    }

    @Override
    public List<InventoryArchetype> getChildArchetypes() {
        return Collections.emptyList();
    }

    @Override
    protected AbstractInventory construct(LanternInventoryBuilder.Context context) {
        final Optional<AcceptsItems> optAcceptsItems = context.getProperty(AcceptsItems.class);
        if (optAcceptsItems.isPresent()) {

        }
        return null;
    }
}
