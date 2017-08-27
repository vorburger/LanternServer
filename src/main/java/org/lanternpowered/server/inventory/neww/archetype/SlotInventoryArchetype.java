package org.lanternpowered.server.inventory.neww.archetype;

import org.lanternpowered.server.inventory.neww.AbstractBuilder;
import org.lanternpowered.server.inventory.neww.LanternInventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetype;

import java.util.Collections;
import java.util.List;

public class SlotInventoryArchetype extends LanternInventoryArchetype {

    public SlotInventoryArchetype(String pluginId, String name, AbstractBuilder builder) {
        super(pluginId, name, builder);
    }

    public SlotInventoryArchetype(String pluginId, String id, String name, AbstractBuilder builder) {
        super(pluginId, id, name, builder);
    }

    @Override
    public List<InventoryArchetype> getChildArchetypes() {
        return Collections.emptyList();
    }

    /*
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
        Supplier<AbstractSlot> typeSupplier;
        final AbstractSlot.Builder builder = AbstractSlot.builder();
        if (itemFilter != null) {
            if (itemFilter instanceof EquipmentItemFilter) {
                typeSupplier = LanternEquipmentSlot::new;
            } else {
                typeSupplier = LanternFilteringSlot::new;
            }
            builder.filter(itemFilter);
        } else {
            typeSupplier = DefaultSlot::new;
        }
        return builder.build(typeSupplier);
    }*/
}
