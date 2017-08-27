package org.lanternpowered.server.inventory.neww.vanilla;

import org.lanternpowered.server.inventory.ContainerViewListener;
import org.lanternpowered.server.inventory.InventoryCloseListener;
import org.lanternpowered.server.inventory.behavior.HotbarBehavior;
import org.lanternpowered.server.inventory.behavior.VanillaHotbarBehavior;
import org.lanternpowered.server.inventory.neww.AbstractInventoryRow;
import org.lanternpowered.server.inventory.neww.AbstractSlot;
import org.lanternpowered.server.inventory.neww.ISlot;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.inventory.slot.SlotChangeListener;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class LanternHotbarInventory extends AbstractInventoryRow implements Hotbar {

    private final HotbarBehavior hotbarBehavior = new VanillaHotbarBehavior();

    /**
     * Gets the {@link ISlot} that is currently selected.
     *
     * @return The selected slot
     */
    public ISlot getSelectedSlot() {
        final int slotIndex = this.hotbarBehavior.getSelectedSlotIndex();
        return getSlot(slotIndex).orElseThrow(() -> new IllegalStateException("No slot at index: " + slotIndex));
    }

    /**
     * Gets the {@link HotbarBehavior}.
     *
     * @return The hotbar behavior
     */
    public HotbarBehavior getHotbarBehavior() {
        return this.hotbarBehavior;
    }

    @Override
    public int getSelectedSlotIndex() {
        return this.hotbarBehavior.getSelectedSlotIndex();
    }

    @Override
    public void setSelectedSlotIndex(int index) {
        this.hotbarBehavior.setSelectedSlotIndex(index);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, @Nullable Object key) {
        if (EquipmentSlotType.class.isAssignableFrom(property) && child == getSelectedSlot()) {
            return Optional.of((T) new EquipmentSlotType(EquipmentTypes.MAIN_HAND));
        }
        return Optional.empty();
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        final List<T> properties = super.tryGetProperties(child, property);
        if (EquipmentSlotType.class.isAssignableFrom(property) && child == getSelectedSlot()) {
            properties.add((T) new EquipmentSlotType(EquipmentTypes.MAIN_HAND));
        }
        return properties;
    }

    // TODO
    @Override public void addViewListener(ContainerViewListener listener) {

    }

    @Override public void addCloseListener(InventoryCloseListener listener) {

    }
}
