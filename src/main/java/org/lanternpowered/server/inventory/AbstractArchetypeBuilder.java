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

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractArchetypeBuilder<R extends T, T extends AbstractInventory, B extends AbstractArchetypeBuilder<R, T, B>>
        extends AbstractBuilder<R, T, B> {

    @Nullable private LanternInventoryArchetype<R> cachedArchetype;

    protected void invalidateCachedArchetype() {
        this.cachedArchetype = null;
    }

    @Override
    public <N extends T> AbstractArchetypeBuilder<N, T, ?> typeSupplier(Supplier<N> supplier) {
        super.typeSupplier(supplier);
        invalidateCachedArchetype();
        return (AbstractArchetypeBuilder<N, T, ?>) this;
    }

    @Override
    public B property(InventoryProperty<String, ?> property) {
        super.property(property);
        invalidateCachedArchetype();
        return (B) this;
    }

    /**
     * Constructs a {@link LanternInventoryArchetype} from this builder.
     *
     * @return The inventory archetype
     */
    public LanternInventoryArchetype<R> buildArchetype() {
        final String pluginId = (this.pluginContainer == null ? Lantern.getImplementationPlugin() : this.pluginContainer).getId();
        return buildArchetype(pluginId, UUID.randomUUID().toString());
    }

    /**
     * Constructs a {@link LanternInventoryArchetype} from this builder.
     *
     * @param pluginId The plugin id
     * @param id The id
     * @return The inventory archetype
     */
    public LanternInventoryArchetype<R> buildArchetype(String pluginId, String id) {
        checkState(this.supplier != null);
        if (this.cachedArchetype != null && this.cachedArchetype.getId()
                .equals(pluginId + ':' + id.toLowerCase(Locale.ENGLISH))) {
            return this.cachedArchetype;
        }
        return this.cachedArchetype = new BuilderInventoryArchetype<>(pluginId, id, copy());
    }

    protected void copyTo(B builder) {
        builder.supplier = this.supplier;
        builder.pluginContainer = this.pluginContainer;
        builder.properties.clear();
        for (Map.Entry<Class<?>, Map<String, InventoryProperty<String, ?>>> entry : this.properties.entrySet()) {
            builder.properties.put(entry.getKey(), new HashMap<>(entry.getValue()));
        }
        builder.cachedProperties = this.cachedProperties;
    }

    /**
     * Constructs a copy of this builder.
     *
     * @return The copy
     */
    protected final B copy() {
        final B copy = newBuilder();
        copyTo(copy);
        return copy;
    }

    protected abstract B newBuilder();

    /**
     * Gets a {@link List} with all the children {@link InventoryArchetype}s.
     *
     * @return The inventory archetypes
     */
    protected abstract List<InventoryArchetype> getArchetypes();
}
