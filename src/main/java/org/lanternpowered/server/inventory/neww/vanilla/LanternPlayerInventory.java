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
package org.lanternpowered.server.inventory.neww.vanilla;

import org.lanternpowered.server.inventory.neww.AbstractOrderedChildrenInventory;
import org.lanternpowered.server.inventory.neww.AbstractSlot;
import org.lanternpowered.server.inventory.neww.InventoryCloseListener;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.item.inventory.property.EquipmentSlotType;

import java.lang.ref.WeakReference;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternPlayerInventory extends AbstractOrderedChildrenInventory implements PlayerInventory {

    @Nullable private WeakReference<Player> carrier;

    private LanternMainPlayerInventory mainInventory;
    private LanternEquipmentInventory equipmentInventory;
    private AbstractSlot offhandSlot;

    @Override
    public Optional<Player> getCarrier() {
        return this.carrier == null ? Optional.empty() : Optional.ofNullable(this.carrier.get());
    }

    @Override
    protected void setCarrier(Carrier carrier) {
        super.setCarrier(carrier);
        // Only Player carriers are supported by this inventory
        this.carrier = carrier instanceof Player ? new WeakReference<>((Player) carrier) : null;
    }

    @Override
    protected void init() {
        super.init();

        // Search the the inventories for the helper methods
        this.mainInventory = query(LanternMainPlayerInventory.class).first();
        this.equipmentInventory = query(LanternEquipmentInventory.class).first();
        this.equipmentInventory = query(new EquipmentSlotType(EquipmentTypes.OFF_HAND)).first();
    }

    @Override
    public LanternMainPlayerInventory getMain() {
        return this.mainInventory;
    }

    @Override
    public LanternEquipmentInventory getEquipment() {
        return this.equipmentInventory;
    }

    @Override
    public AbstractSlot getOffhand() {
        return this.offhandSlot;
    }
}
