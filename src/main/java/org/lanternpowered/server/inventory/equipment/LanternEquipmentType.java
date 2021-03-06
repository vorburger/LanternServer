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
package org.lanternpowered.server.inventory.equipment;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.item.inventory.equipment.EquipmentType;

import java.util.function.Predicate;

import javax.annotation.Nullable;

public class LanternEquipmentType extends PluginCatalogType.Base implements EquipmentType {

    @Nullable private final Predicate<EquipmentType> childChecker;

    public LanternEquipmentType(String pluginId, String name) {
        this(pluginId, name, null);
    }

    public LanternEquipmentType(String pluginId, String name, @Nullable Predicate<EquipmentType> childChecker) {
        super(pluginId, name);
        this.childChecker = childChecker;
    }

    /**
     * Gets whether the specified {@link EquipmentType} can be considered
     * a child of this equipment type, this will also return {@code true}
     * if the specified equipment type == this.
     *
     * @param equipmentType The equipment type
     * @return Is child
     */
    public boolean isChild(@Nullable EquipmentType equipmentType) {
        return equipmentType != null && (equipmentType.equals(this) || (this.childChecker != null && this.childChecker.test(equipmentType)));
    }
}
