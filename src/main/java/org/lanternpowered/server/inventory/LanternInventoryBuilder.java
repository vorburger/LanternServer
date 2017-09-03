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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.property.PropertyKeySetter;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.function.Consumer;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class LanternInventoryBuilder<T extends AbstractInventory> implements Inventory.Builder {

    public static LanternInventoryBuilder<AbstractInventory> create() {
        return new LanternInventoryBuilder<>();
    }

    @Nullable private LanternInventoryArchetype<T> inventoryArchetype;
    @Nullable private AbstractBuilder builder;
    @Nullable private Carrier carrier;

    private LanternInventoryBuilder() {
    }

    @Override
    public LanternInventoryBuilder<T> from(Inventory value) {
        checkNotNull(value, "value");
        this.inventoryArchetype = (LanternInventoryArchetype<T>) value.getArchetype();
        this.builder = null; // Regenerate the builder if needed
        return this;
    }

    @Override
    public LanternInventoryBuilder<T> reset() {
        this.inventoryArchetype = null;
        this.builder = null;
        this.carrier = null;
        return this;
    }

    @Override
    public LanternInventoryBuilder<T> of(InventoryArchetype archetype) {
        checkNotNull(archetype, "archetype");
        this.inventoryArchetype = (LanternInventoryArchetype<T>) archetype;
        this.builder = null; // Regenerate the builder if needed
        return this;
    }

    public <N extends AbstractInventory> LanternInventoryBuilder<N> of(LanternInventoryArchetype<N> archetype) {
        checkNotNull(archetype, "archetype");
        this.inventoryArchetype = (LanternInventoryArchetype<T>) archetype;
        archetype.getBuilder();
        this.builder = null; // Regenerate the builder if needed
        return (LanternInventoryBuilder<N>) this;
    }

    @Override
    public LanternInventoryBuilder<T> property(String name, InventoryProperty<?,?> property) {
        checkNotNull(name, "name");
        checkNotNull(property, "property");
        if (this.inventoryArchetype == null) {
            return this;
        }
        final InventoryProperty<String, ?> property1 = (InventoryProperty<String, ?>) property;
        PropertyKeySetter.setKey(property1, name); // Modify the name of the property
        if (this.builder == null) {
            this.builder = this.inventoryArchetype.getBuilder().copy();
        }
        this.builder.property(property1);
        return this;
    }

    @Override
    public LanternInventoryBuilder<T> withCarrier(Carrier carrier) {
        checkNotNull(carrier, "carrier");
        this.carrier = carrier;
        return this;
    }

    @Override
    public T build(Object plugin) {
        checkState(this.inventoryArchetype != null, "The inventory archetype must be set");
        final AbstractInventory inventory;
        if (this.builder != null) {
            inventory = this.builder.build(plugin, this.inventoryArchetype);
        } else {
            inventory = this.inventoryArchetype.getBuilder().build(plugin, this.inventoryArchetype);
        }
        if (inventory instanceof AbstractMutableInventory && this.carrier != null) {
            final AbstractMutableInventory mutableInventory = (AbstractMutableInventory) inventory;
            mutableInventory.setCarrier0(this.carrier);
        }
        if (this.carrier instanceof AbstractCarrier) {
            ((AbstractCarrier) this.carrier).setInventory(inventory);
        }
        return (T) inventory;
    }

    @Override
    public <E extends InteractInventoryEvent> LanternInventoryBuilder<T> listener(Class<E> type, Consumer<E> listener) {
        return this; // TODO
    }

    @Override
    public LanternInventoryBuilder<T> forCarrier(Carrier carrier) {
        return this; // TODO
    }

    @Override
    public LanternInventoryBuilder<T> forCarrier(Class<? extends Carrier> carrier) {
        return this; // TODO
    }
}
