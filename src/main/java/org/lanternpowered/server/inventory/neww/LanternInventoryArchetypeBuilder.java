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

import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryDimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LanternInventoryArchetypeBuilder implements InventoryArchetype.Builder {

    private final List<LanternInventoryArchetype<?>> archetypes = new ArrayList<>();
    private final Map<Class<?>, InventoryProperty<?,?>> properties = new HashMap<>();

    @Override
    public InventoryArchetype.Builder from(InventoryArchetype value) {
        return null;
    }

    @Override
    public InventoryArchetype.Builder reset() {
        return null;
    }

    @Override
    public InventoryArchetype.Builder property(InventoryProperty<String, ?> property) {
        checkNotNull(property, "property");
        this.properties.put(property.getClass(), property);
        return this;
    }

    @Override
    public InventoryArchetype.Builder with(InventoryArchetype archetype) {
        checkNotNull(archetype, "archetype");
        this.archetypes.add((LanternInventoryArchetype<?>) archetype);
        return this;
    }

    @Override
    public InventoryArchetype.Builder with(InventoryArchetype... archetypes) {
        Arrays.stream(archetypes).forEach(this::with);
        return this;
    }

    @Override
    public InventoryArchetype build(String id, String name) {
        final InventoryDimension inventoryDimension = (InventoryDimension) this.properties.remove(InventoryDimension.class);
        final InventoryCapacity inventoryCapacity = (InventoryCapacity) this.properties.remove(InventoryCapacity.class);
        // Dimension doesn't matter, this means that we can just create a ordered children archetype
        if (inventoryDimension == null) {
            for (LanternInventoryArchetype<?> archetype : this.archetypes) {
                if (archetype.builder instanceof AbstractSlot.Builder) {

                }
            }
        }
        return null;
    }
}
