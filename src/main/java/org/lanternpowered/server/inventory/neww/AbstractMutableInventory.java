package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A base class for every {@link Inventory} that
 * isn't a {@link EmptyInventory}.
 */
@SuppressWarnings("unchecked")
public abstract class AbstractMutableInventory extends AbstractInventory {

    @Nullable private PluginContainer plugin;
    @Nullable private LanternEmptyInventory emptyInventory;
    @Nullable private InventoryArchetype archetype;

    /**
     * Sets the {@link PluginContainer} of this inventory.
     *
     * @param plugin The plugin container
     */
    void setPlugin(PluginContainer plugin) {
        this.plugin = plugin;
    }

    /**
     * Sets the {@link InventoryArchetype} of this inventory.
     *
     * @param archetype The archetype
     */
    void setArchetype(InventoryArchetype archetype) {
        this.archetype = archetype;
    }

    /**
     * Sets the {@link Carrier} of this {@link Inventory}.
     *
     * @param carrier The carrier
     */
    protected abstract void setCarrier(Carrier carrier);

    /**
     * Gets all the {@link AbstractSlot}s that could be found in the
     * children inventories. This method may return a empty {@link List}
     * if the subclass doesn't support children, for example slots and
     * empty inventories.
     *
     * @return The slots
     */
    protected abstract List<AbstractSlot> getSlotInventories();

    @Override
    public <T extends Inventory> Iterable<T> slots() {
        return (Iterable<T>) getSlotInventories();
    }

    @Override
    public IInventory intersect(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        if (inventory == this) {
            return this;
        }
        final AbstractInventory abstractInventory = (AbstractInventory) inventory;
        List<AbstractSlot> slots = abstractInventory.getSlotInventories();
        if (slots.isEmpty()) {
            return genericEmpty();
        }
        slots = new ArrayList<>(slots);
        slots.retainAll(getSlotInventories());
        if (slots.isEmpty()) { // No slots were intersected, just return a empty inventory
            return genericEmpty();
        } else {
            // Construct the result inventory
            final DefaultUnorderedChildrenInventory result = new DefaultUnorderedChildrenInventory();
            result.init(Collections.unmodifiableList(slots));
            return result;
        }
    }

    @Override
    public IInventory union(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        if (inventory == this) {
            return this;
        }
        final AbstractInventory abstractInventory = (AbstractInventory) inventory;
        final List<AbstractSlot> slotsA = abstractInventory.getSlotInventories();
        if (slotsA.isEmpty()) {
            return this;
        }
        final List<AbstractSlot> slotsB = new ArrayList<>(getSlotInventories());
        slotsB.removeAll(slotsA);
        slotsB.addAll(0, slotsA);
        if (slotsB.isEmpty()) { // No slots were intersected, just return a empty inventory
            return genericEmpty();
        } else {
            // Construct the result inventory
            final DefaultUnorderedChildrenInventory result = new DefaultUnorderedChildrenInventory();
            result.init(Collections.unmodifiableList(slotsB));
            return result;
        }
    }

    @Override
    public InventoryArchetype getArchetype() {
        return this.archetype == null ? InventoryArchetypes.UNKNOWN : this.archetype;
    }

    @Override
    public PluginContainer getPlugin() {
        return this.plugin == null ? super.getPlugin() : this.plugin;
    }

    @Override
    protected EmptyInventory empty() {
        // Lazily construct the empty inventory
        LanternEmptyInventory emptyInventory = this.emptyInventory;
        if (emptyInventory == null) {
            emptyInventory = this.emptyInventory = new LanternEmptyInventory();
            emptyInventory.setParent(this);
        }
        return emptyInventory;
    }
}
