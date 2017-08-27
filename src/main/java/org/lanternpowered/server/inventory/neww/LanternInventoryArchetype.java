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

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.inventory.InventoryPropertyHolder;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unchecked")
public class LanternInventoryArchetype<T extends AbstractInventory> extends PluginCatalogType.Base
        implements InventoryArchetype, InventoryPropertyHolder {

    protected final AbstractBuilder<T, ? super T, ?> builder;
    private final Map<String, InventoryProperty<String, ?>> propertiesByName;
    private final List<InventoryArchetype> childArchetypes;

    LanternInventoryArchetype(String pluginId, String name,
            AbstractBuilder<T, ? super T, ?> builder) {
        super(pluginId, name);
        this.propertiesByName = Collections.unmodifiableMap(builder.propertiesByName);
        this.childArchetypes = Collections.unmodifiableList(builder.getArchetypes());
        this.builder = builder;
    }

    @Override
    public List<InventoryArchetype> getChildArchetypes() {
        return this.childArchetypes;
    }

    @Override
    public Map<String, InventoryProperty<String, ?>> getProperties() {
        return this.propertiesByName;
    }

    @Override
    public Optional<InventoryProperty<String, ?>> getProperty(String key) {
        checkNotNull(key, "key");
        return Optional.ofNullable(this.propertiesByName.get(key));
    }

    @Override
    public <P extends InventoryProperty<String, ?>> Optional<P> getProperty(Class<P> property) {
        checkNotNull(property, "property");
        return Optional.ofNullable((P) this.builder.properties.get(property));
    }

    @Override
    public <P extends InventoryProperty<String, ?>> Optional<P> getProperty(Class<P> type, String key) {
        // return this.builder.asPropertyHolder().getProperty(type, key);
        return Optional.empty();
    }

    /**
     * Constructs a {@link AbstractInventory}.
     *
     * @return The inventory
     */
    public T build() {
        return this.builder.build();
    }
}
