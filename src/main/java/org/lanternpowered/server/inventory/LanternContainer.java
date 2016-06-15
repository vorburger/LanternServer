/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.entity.HumanInventoryView;
import org.lanternpowered.server.inventory.entity.HumanMainInventory;
import org.lanternpowered.server.inventory.entity.LanternHumanInventory;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetWindowSlot;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWindowItems;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.HumanInventory;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

public abstract class LanternContainer extends LanternOrderedInventory implements Container {

    private static int windowIdCounter = 1;

    final Set<Player> viewers = new HashSet<>();
    final Map<LanternSlot, Boolean> dirtySlots = new HashMap<>();

    protected final int windowId;

    protected final LanternOrderedInventory openInventory;
    final LanternHumanInventory humanInventory;

    /**
     * Creates a new {@link LanternContainer}, the specified {@link HumanInventory} is
     * used as the bottom inventory and also as top inventory if {@code null} is provided
     * for the inventory that should be opened.
     *
     * @param name The name of the container
     * @param humanInventory The human inventory
     * @param openInventory The inventory to open
     */
    public LanternContainer(@Nullable Translation name,
            LanternHumanInventory humanInventory, @Nullable OrderedInventory openInventory) {
        super(null, name);
        this.humanInventory = humanInventory;
        HumanMainInventory mainInventory = humanInventory.query(HumanMainInventory.class).first();
        if (openInventory != null) {
            this.registerChild(openInventory);
            this.registerChild(mainInventory);
            this.windowId = windowIdCounter++;
            if (windowIdCounter >= 127) {
                windowIdCounter = 1;
            }
            this.openInventory = (LanternOrderedInventory) openInventory;
        } else {
            this.registerChild(humanInventory);
            this.openInventory = humanInventory;
            this.windowId = 0;
        }
        for (LanternSlot slot : this.slots) {
            slot.addContainer(this);
        }
    }

    /**
     * Gets the {@link Inventory} that is being opened.
     *
     * @return The inventory
     */
    public LanternOrderedInventory getOpenInventory() {
        return this.openInventory;
    }

    @Override
    public Set<Player> getViewers() {
        return ImmutableSet.copyOf(this.viewers);
    }

    @Override
    public boolean hasViewers() {
        return !this.viewers.isEmpty();
    }

    @Override
    public void open(Player viewer) {
        checkNotNull(viewer, "viewer");
        ((LanternPlayer) viewer).getContainerSession().setOpenContainer(this);
    }

    @Override
    public void close(Player viewer) {
        checkNotNull(viewer, "viewer");
        PlayerContainerSession session = ((LanternPlayer) viewer).getContainerSession();
        if (session.getOpenContainer() == this) {
            session.setOpenContainer(null);
        }
    }

    protected abstract void openInventoryFor(LanternPlayer viewer);

    public void openInventoryForAndInitialize(Player viewer) {
        this.openInventoryFor((LanternPlayer) viewer);

        final List<ItemStack> items = this.slots.stream()
                .map(slot -> slot.peek().orElse(null)).collect(Collectors.toList());
        // Send the inventory content
        ((LanternPlayer) viewer).getConnection().send(
                new MessagePlayOutWindowItems(this.windowId, items.toArray(new ItemStack[items.size()])));
    }

    /**
     * Queues a {@link ItemStack} change of a {@link Slot}.
     *
     * @param slot The slot
     */
    public void queueSlotChange(Slot slot) {
        this.queueSlotChange(slot, false);
    }

    /**
     * Queues a {@link ItemStack} change of a {@link Slot}. This
     * is done "silently" and this means that there won't be any
     * animation played (in the hotbar).
     *
     * @param slot The slot
     */
    public void queueSilentSlotChange(Slot slot) {
        this.queueSlotChange(slot, true);
    }

    void queueSlotChange(Slot slot, boolean silent) {
        if (!this.viewers.isEmpty()) {
            int index = this.openInventory.getSlotIndex(slot);
            if (index != -1) {
                this.dirtySlots.put((LanternSlot) slot, silent);
            } else {
                this.queueHumanSlotChange(slot, silent);
            }
        }
    }

    void queueHumanSlotChange(Slot slot, boolean silent) {
        int index = this.humanInventory.getSlotIndex(slot);
        if (index != -1) {
            this.dirtySlots.put((LanternSlot) slot, silent);
        }
    }

    Set<Player> getRawViewers() {
        return this.viewers;
    }

    public void streamSlotChanges() {
        final List<Message> messages = new ArrayList<>();
        for (Map.Entry<LanternSlot, Boolean> entry : this.dirtySlots.entrySet()) {
            int windowId = this.windowId;
            int index = -1;
            if (windowId != 0) {
                index = this.openInventory.getSlotIndex(entry.getKey());
            } else {
                // The silent slot index, is different....
                // Try the raw inventory slots first
                if (entry.getValue()) {
                    index = ((LanternOrderedInventory) this.humanInventory.getInventoryView(HumanInventoryView.RAW_INVENTORY))
                            .getSlotIndex(entry.getKey());
                    // Silent updates use the window id -2, this is not
                    // supported for all slot though
                    if (index != -1) {
                        windowId = -2;
                    }
                }
                if (index == -1) {
                    index = this.getSlotIndex(entry.getKey());
                }
            }
            messages.add(new MessagePlayOutSetWindowSlot(windowId, index, entry.getKey().peek().orElse(null)));
        }
        this.dirtySlots.clear();
        if (!messages.isEmpty()) {
            this.getRawViewers().forEach(player -> ((LanternPlayer) player).getConnection().sendAll(messages));
        }
    }

    @Override
    public boolean canInteractWith(Player player) {
        return true;
    }

}