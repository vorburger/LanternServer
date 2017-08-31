/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.LanternContainer;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private final Set<InventoryViewerListener> viewerListeners = new HashSet<>();
    private final Set<InventoryCloseListener> closeListeners = new HashSet<>();

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
    void close() {
        super.close();
        for (InventoryCloseListener listener : this.closeListeners) {
            listener.onClose(this);
        }
    }

    @Override
    public void addCloseListener(InventoryCloseListener listener) {
        checkNotNull(listener, "listener");
        this.closeListeners.add(listener);
    }

    @Override
    public void addViewListener(InventoryViewerListener listener) {
        checkNotNull(listener, "listener");
        this.viewerListeners.add(listener);
    }

    private static final class ViewerListenerCallback implements InventoryViewerListener.Callback {

        private boolean remove;

        @Override
        public void remove() {
            this.remove = true;
        }
    }

    @Override
    void addViewer(Viewer viewer, LanternContainer container) {
        if (this.viewerListeners.isEmpty()) {
            return;
        }
        final ViewerListenerCallback callback = new ViewerListenerCallback();
        this.viewerListeners.removeIf(listener -> {
            callback.remove = false;
            listener.onViewerAdded(viewer, container, callback);
            return callback.remove;
        });
    }

    @Override
    void removeViewer(Viewer viewer, LanternContainer container) {
        if (this.viewerListeners.isEmpty()) {
            return;
        }
        final ViewerListenerCallback callback = new ViewerListenerCallback();
        this.viewerListeners.removeIf(listener -> {
            callback.remove = false;
            listener.onViewerRemoved(viewer, container, callback);
            return callback.remove;
        });
    }

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
            final UnorderedSlotsInventoryQuery result = new UnorderedSlotsInventoryQuery();
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
            final UnorderedSlotsInventoryQuery result = new UnorderedSlotsInventoryQuery();
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
