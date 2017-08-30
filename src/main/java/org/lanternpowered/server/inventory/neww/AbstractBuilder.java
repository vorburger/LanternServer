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
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractBuilder<R extends T, T extends AbstractInventory, B extends AbstractBuilder<R, T, B>> {

    @Nullable protected Supplier<R> supplier;
    protected final Map<Class<?>, InventoryProperty<String, ?>> properties = new HashMap<>();
    protected final Map<String, InventoryProperty<String, ?>> propertiesByName = new HashMap<>();
    @Nullable private LanternInventoryArchetype<R> cachedArchetype;

    // Catalog properties
    @Nullable protected PluginContainer pluginContainer;

    protected void invalidateCachedArchetype() {
        this.cachedArchetype = null;
    }

    /**
     * Sets the {@link Supplier} for the {@link AbstractInventory}, the
     * supplied slot may not be initialized yet.
     *
     * @param supplier The type supplier
     * @param <N> The supplied slot type
     * @return This builder, for chaining
     */
    public <N extends T> AbstractBuilder<N, T, ?> typeSupplier(Supplier<N> supplier) {
        checkNotNull(supplier, "supplier");
        this.supplier = (Supplier<R>) supplier;
        invalidateCachedArchetype();
        return (AbstractBuilder<N, T, ?>) this;
    }

    /**
     * Adds the provided {@link InventoryProperty}.
     *
     * @param property The property
     * @return This builder, for chaining
     */
    public B property(InventoryProperty<String, ?> property) {
        checkNotNull(property, "property");
        this.properties.put(property.getClass(), property);
        this.propertiesByName.put(property.getKey(), property);
        invalidateCachedArchetype();
        return (B) this;
    }

    /**
     * Sets the plugin that provides the {@link LanternInventoryArchetype}.
     *
     * @param plugin The plugin instance
     * @return This builder, for chaining
     */
    public B plugin(Object plugin) {
        this.pluginContainer = checkPlugin(plugin, "plugin");
        return (B) this;
    }

    /**
     * Constructs a {@link AbstractInventory}.
     *
     * @return The inventory
     */
    public R build() {
        return build0(this.pluginContainer == null ? Lantern.getImplementationPlugin() : this.pluginContainer);
    }

    /**
     * Constructs a {@link AbstractInventory} and sets the plugin
     * instance that constructed the inventory.
     *
     * @param plugin The plugin
     * @return The inventory
     */
    public R build(Object plugin) {
        return build0(checkPlugin(plugin, "plugin"));
    }

    private R build0(PluginContainer plugin) {
        checkState(this.supplier != null);
        final R inventory = this.supplier.get();
        if (inventory instanceof AbstractMutableInventory) {
            final AbstractMutableInventory mutableInventory = (AbstractMutableInventory) inventory;
            final String pluginId = (this.pluginContainer == null ? Lantern.getImplementationPlugin() : this.pluginContainer).getId();
            mutableInventory.setArchetype(buildArchetype(pluginId, UUID.randomUUID().toString()));
            mutableInventory.setPlugin(plugin);
        }
        build(inventory);
        return inventory;
    }

    /**
     * Initializes the build {@link AbstractInventory}.
     *
     * @param inventory The inventory
     */
    protected abstract void build(R inventory);

    /**
     * Constructs a {@link LanternInventoryArchetype} from this builder.
     *
     * @return The inventory archetype
     */
    public LanternInventoryArchetype<R> buildArchetype() {
        checkState(this.supplier != null);
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
        if (this.cachedArchetype != null &&this.cachedArchetype.getId()
                .equals(pluginId + ':' + id.toLowerCase(Locale.ENGLISH))) {
            return this.cachedArchetype;
        }
        return this.cachedArchetype = new LanternInventoryArchetype<>(pluginId, id, copy());
    }

    /**
     * Constructs a copy of this builder.
     *
     * @return The copy
     */
    protected abstract B copy();

    /**
     * Gets a {@link List} with all the children {@link InventoryArchetype}s.
     *
     * @return The inventory archetypes
     */
    protected abstract List<InventoryArchetype> getArchetypes();
}
