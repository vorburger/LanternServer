package org.lanternpowered.server.inventory.neww;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.*;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractBuilder<R extends T, T extends AbstractInventory, B extends AbstractBuilder<R, T, B>> {

    @Nullable protected Supplier<R> supplier;
    protected final Map<Class<?>, InventoryProperty<String, ?>> properties = new HashMap<>();
    protected final Map<String, InventoryProperty<String, ?>> propertiesByName = new HashMap<>();
    @Nullable protected LanternInventoryArchetype<R> cachedArchetype;
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
    public <N extends T> B typeSupplier(Supplier<N> supplier) {
        checkNotNull(supplier, "supplier");
        this.supplier = (Supplier<R>) supplier;
        invalidateCachedArchetype();
        return (B) this;
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
        checkState(this.supplier != null);
        final R inventory = this.supplier.get();
        if (inventory instanceof AbstractMutableInventory) {
            final String pluginId = (this.pluginContainer == null ? Lantern.getImplementationPlugin() : this.pluginContainer).getId();
            ((AbstractMutableInventory) inventory).setArchetype(buildArchetype(pluginId, UUID.randomUUID().toString()));
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
     * @param pluginId The plugin id
     * @param id The id
     * @return The inventory archetype
     */
    public LanternInventoryArchetype<R> buildArchetype(String pluginId, String id) {
        if (this.cachedArchetype != null) {
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
     * Gets this builder as a {@link InventoryPropertyHolder}.
     *
     * @return The property holder
     */
    public InventoryPropertyHolder asPropertyHolder() {
        return null;
    }
}
