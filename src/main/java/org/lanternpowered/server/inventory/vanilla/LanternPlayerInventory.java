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
package org.lanternpowered.server.inventory.vanilla;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import org.lanternpowered.server.inventory.AbstractGridInventory;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.AbstractOrderedChildrenInventory;
import org.lanternpowered.server.inventory.AbstractSlot;
import org.lanternpowered.server.inventory.CarrierReference;
import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.OpenableInventory;
import org.lanternpowered.server.inventory.behavior.VanillaContainerInteractionBehavior;
import org.lanternpowered.server.inventory.client.PlayerClientContainer;
import org.lanternpowered.server.inventory.type.LanternCraftingInventory;
import org.lanternpowered.server.inventory.type.LanternOrderedChildrenInventory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Optional;

public class LanternPlayerInventory extends AbstractOrderedChildrenInventory implements PlayerInventory, OpenableInventory {

    private final CarrierReference<Player> carrierReference = CarrierReference.of(Player.class);

    private LanternCraftingInventory craftingInventory;
    private LanternMainPlayerInventory mainInventory;
    private LanternPlayerEquipmentInventory equipmentInventory;
    private AbstractSlot offhandSlot;

    private final EnumMap<View, AbstractInventory> views = new EnumMap<>(View.class);

    /**
     * Gets the specified inventory view.
     *
     * @param view The view type
     * @return The inventory view
     */
    public AbstractInventory getView(View view) {
        checkNotNull(view, "view");
        if (view == View.HOTBAR) {
            return this.mainInventory.getHotbar();
        } else if (view == View.MAIN) {
            return this.mainInventory.getGrid();
        } else if (view == View.MAIN_AND_PRIORITY_HOTBAR) {
            return this.mainInventory;
        }
        return this.views.get(view);
    }

    @Override
    public Optional<Player> getCarrier() {
        return this.carrierReference.get();
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        this.carrierReference.set(carrier);
    }

    @Override
    protected void init() {
        super.init();

        // Search the the inventories for the helper methods
        this.craftingInventory = query(LanternCraftingInventory.class).first();
        this.mainInventory = query(LanternMainPlayerInventory.class).first();
        this.equipmentInventory = query(LanternPlayerEquipmentInventory.class).first();
        this.offhandSlot = query(new EquipmentSlotType(EquipmentTypes.OFF_HAND)).first();

        // Construct the inventory views

        final LanternOrderedChildrenInventory priorityMainAndHotbar = AbstractOrderedChildrenInventory.viewBuilder()
                .inventory(this.mainInventory.getGrid())
                .inventory(this.mainInventory.getHotbar())
                .build();
        this.views.put(View.PRIORITY_MAIN_AND_HOTBAR, priorityMainAndHotbar);

        final List<AbstractInventory> inventories = new ArrayList<>();
        inventories.addAll(getChildren());
        inventories.set(inventories.indexOf(this.mainInventory), priorityMainAndHotbar);
        this.views.put(View.ALL_PRIORITY_MAIN, AbstractOrderedChildrenInventory.viewBuilder()
                .inventories(inventories)
                .build());

        this.views.put(View.REVERSE_MAIN_AND_HOTBAR, AbstractOrderedChildrenInventory.viewBuilder()
                .inventories(Lists.reverse(Lists.newArrayList(this.mainInventory.iterator())))
                .build());
    }

    @Override
    public LanternMainPlayerInventory getMain() {
        return this.mainInventory;
    }

    @Override
    public LanternPlayerEquipmentInventory getEquipment() {
        return this.equipmentInventory;
    }

    @Override
    public AbstractSlot getOffhand() {
        return this.offhandSlot;
    }

    @Override
    public LanternHotbarInventory getHotbar() {
        return getMain().getHotbar();
    }

    @Override
    public AbstractGridInventory getMainGrid() {
        return getMain().getGrid();
    }

    @Override
    public PlayerClientContainer constructClientContainer(LanternContainer container) {
        final PlayerClientContainer clientContainer = new PlayerClientContainer(Text.of(getName()));
        clientContainer.bindCursor(container.getCursorSlot());
        clientContainer.bindHotbarBehavior(getHotbar().getHotbarBehavior());
        clientContainer.bindInteractionBehavior(new VanillaContainerInteractionBehavior(container));
        clientContainer.bindBottom();
        getSlotsToIndexMap().object2IntEntrySet().forEach(entry ->
                clientContainer.bindSlot(entry.getIntValue(), entry.getKey()));
        return clientContainer;
    }

    /**
     * The different kind of {@link Inventory} views that can be
     * used for the {@link LanternPlayerInventory}. This mainly
     * modifies the insertion/poll order of item stacks. And the
     * which sub {@link Inventory}s are available.
     */
    public enum View {
        /**
         * The hotbar inventory view. Contains only the hotbar.
         */
        HOTBAR,
        /**
         * The main inventory view. Contains only the main inventory,
         * excludes the hotbar.
         */
        MAIN,
        /**
         * The main and hotbar inventory.
         */
        MAIN_AND_PRIORITY_HOTBAR,
        /**
         * The main and hotbar inventory, but the main inventory
         * has priority for offer/poll functions.
         */
        PRIORITY_MAIN_AND_HOTBAR,
        /**
         * The reverse order for the main and hotbar inventory. Starting
         * from the bottom right corner, then going left until the row
         * is finished and doing this for every row until the most
         * upper one is reached.
         */
        REVERSE_MAIN_AND_HOTBAR,
        /**
         * All the inventories but the main inventory has priority over
         * the hotbar.
         */
        ALL_PRIORITY_MAIN,
    }
}
