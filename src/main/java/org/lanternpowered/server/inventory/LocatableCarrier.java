package org.lanternpowered.server.inventory;

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

@SuppressWarnings("unchecked")
public final class LocatableCarrier<T extends AbstractInventory & CarriedInventory<LocatableCarrier<T>>> implements Carrier {

    /*
    public static <R extends AbstractInventory & CarriedInventory<LocatableCarrier<R>>> LocatableCarrier<R> of(
            LanternInventoryArchetype<R> archetype, Location<World> location) {
        return new LocatableCarrier(archetype, Lantern.getImplementationPlugin());
    }

    public static <R extends AbstractInventory & CarriedInventory<LocatableCarrier<R>>> LocatableCarrier<R> of(
            LanternInventoryArchetype<R> archetype, Location<World> location, Object plugin) {
        return new LocatableCarrier(archetype, plugin);
    }

    public static <R extends AbstractInventory & CarriedInventory<LocatableCarrier<R>>> LocatableCarrier<R> of(
            LanternInventoryBuilder<R> builder, Location<World> location) {
        return new LocatableCarrier(builder, plugin);
    }

    public static <R extends AbstractInventory & CarriedInventory<LocatableCarrier<R>>> LocatableCarrier<R> of(
            LanternInventoryArchetype<R> archetype, Location<World> location, Object plugin) {
        return new LocatableCarrier(archetype, plugin);
    }
*/
    private final T inventory;

    private LocatableCarrier(LanternInventoryArchetype<T> archetype, Object plugin) {
        this.inventory = archetype.builder().withCarrier(this).build(plugin);
    }

    private LocatableCarrier(LanternInventoryBuilder<T> builder, Object plugin) {
        this.inventory = builder.withCarrier(this).build(plugin);
    }

    private LocatableCarrier(T inventory) {
        this.inventory = inventory;
    }

    @Override
    public T getInventory() {
        return this.inventory;
    }
}
