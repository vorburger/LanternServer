package org.lanternpowered.server.inventory.neww.archetype;

import org.lanternpowered.server.inventory.InventoryPropertyHolder;
import org.lanternpowered.server.inventory.neww.AbstractInventory;
import org.lanternpowered.server.inventory.neww.AbstractSlot;
import org.lanternpowered.server.inventory.neww.DefaultSlot;
import org.lanternpowered.server.inventory.neww.LanternEquipmentSlot;
import org.lanternpowered.server.inventory.neww.LanternFilteringSlot;
import org.lanternpowered.server.inventory.neww.filter.EquipmentItemFilter;
import org.lanternpowered.server.inventory.neww.filter.ItemFilter;
import org.lanternpowered.server.inventory.neww.filter.PropertyItemFilters;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.property.AcceptsItems;
import org.spongepowered.api.item.inventory.property.ArmorSlotType;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class SlotInventoryArchetype extends AbstractInventoryArchetype {

    @Nullable private ItemFilter itemFilter;

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
    protected void preConstruct(ConstructionContext context) {
        this.itemFilter = constructFilter(this);
    }

    @Nullable
    private ItemFilter constructFilter(InventoryPropertyHolder holder) {
        ItemFilter itemFilter = null;
        // Attempt to generate the ItemFilter
        final Optional<AcceptsItems> optAcceptsItems = holder.getProperty(AcceptsItems.class);
        if (optAcceptsItems.isPresent()) {
            itemFilter = PropertyItemFilters.of(optAcceptsItems.get());
        }
        final Optional<EquipmentSlotType> optEquipmentSlotType = holder.getProperty(EquipmentSlotType.class);
        if (optEquipmentSlotType.isPresent()) {
            EquipmentItemFilter equipmentItemFilter = EquipmentItemFilter.of(optEquipmentSlotType.get());
            if (itemFilter != null) {
                equipmentItemFilter = equipmentItemFilter.andThen(itemFilter);
            }
            itemFilter = equipmentItemFilter;
        }
        final Optional<ArmorSlotType> optArmorSlotType = holder.getProperty(ArmorSlotType.class);
        if (optArmorSlotType.isPresent()) {
            EquipmentItemFilter equipmentItemFilter = EquipmentItemFilter.of(optArmorSlotType.get());
            if (itemFilter != null) {
                equipmentItemFilter = equipmentItemFilter.andThen(itemFilter);
            }
            itemFilter = equipmentItemFilter;
        }
        return itemFilter;
    }

    @Override
    protected AbstractInventory construct(ConstructionContext context) {
        ItemFilter itemFilter = this.itemFilter;
        if (context instanceof PostConstructionContext) {
            itemFilter = constructFilter((InventoryPropertyHolder) context);
        }
        Supplier<AbstractSlot> supplier;
        final AbstractSlot.Builder builder = AbstractSlot.builder();
        if (itemFilter != null) {
            if (itemFilter instanceof EquipmentItemFilter) {
                supplier = LanternEquipmentSlot::new;
            } else {
                supplier = LanternFilteringSlot::new;
            }
            builder.filter(itemFilter);
        } else {
            supplier = DefaultSlot::new;
        }
        return builder.build(supplier);
    }
}
