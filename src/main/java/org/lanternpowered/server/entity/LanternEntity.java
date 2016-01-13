/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.entity;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.lanternpowered.server.data.LanternDataHolder;
import org.lanternpowered.server.data.manipulator.mutable.entity.LanternHealthData;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.data.manipulator.mutable.entity.BreathingData;
import org.spongepowered.api.data.manipulator.mutable.entity.FallDistanceData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.IgniteableData;
import org.spongepowered.api.data.manipulator.mutable.entity.InvisibilityData;
import org.spongepowered.api.data.manipulator.mutable.entity.InvulnerabilityData;
import org.spongepowered.api.data.manipulator.mutable.entity.PassengerData;
import org.spongepowered.api.data.manipulator.mutable.entity.SizeData;
import org.spongepowered.api.data.manipulator.mutable.entity.VelocityData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.RelativePositions;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;

import javax.annotation.Nullable;

@NonnullByDefault
public abstract class LanternEntity extends LanternDataHolder implements Entity, AbstractPropertyHolder {

    static {
        defaultManipulators.add(IgniteableData.class);
        defaultManipulators.add(VelocityData.class);
        defaultManipulators.add(DisplayNameData.class);
        defaultManipulators.add(InvisibilityData.class);
        defaultManipulators.add(FallDistanceData.class);
        defaultManipulators.add(SizeData.class);
        defaultManipulators.add(InvulnerabilityData.class);
        defaultManipulators.add(BreathingData.class);
        defaultManipulators.add(PassengerData.class);
    }

    protected final static float EPSILON = 1.0e-004f;

    private final World world;

    protected Transform<World> position;

    protected Vector3d motion;

    public LanternEntity(DataView data, Transform<World> position) {
        super(data);
        this.world = position.getExtent();
        this.position = position;

        //TODO: Load from data
    }

    @Override
    public UUID getUniqueId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean validateRawData(DataContainer container) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setRawData(DataContainer container) throws InvalidDataException {
        // TODO Auto-generated method stub

    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EntityType getType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public World getWorld() {
        return this.world;
    }

    @Override
    public Location<World> getLocation() {
        return this.position.getLocation();
    }

    @Override
    public void setLocation(Location<World> location) {
        this.position = this.position.setLocation(location);
        
    }

    @Override
    public void setLocationAndRotation(Location<World> location, Vector3d rotation) {
        this.position = this.position.setLocation(location).setRotation(rotation);
    }

    @Override
    public boolean setLocationSafely(Location<World> location) {
        Optional<Location<World>> safe = LanternGame.get().getTeleportHelper().getSafeLocation(location);
        if(safe.isPresent()) {
            setLocation(safe.get());
            return true;
        }

        return false;
    }

    @Override
    public boolean setLocationAndRotationSafely(Location<World> location, Vector3d rotation) {
        Optional<Location<World>> safe = LanternGame.get().getTeleportHelper().getSafeLocation(location);
        if(safe.isPresent()) {
            setLocationAndRotation(safe.get(), rotation);
            return true;
        }
        setRotation(rotation);
        return false;
    }

    @Override
    public boolean transferToWorld(String worldName, Vector3d position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean transferToWorld(UUID uuid, Vector3d position) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Optional<Entity> getPassenger() {
        return null;
    }

    @Override
    public DataTransactionResult setPassenger(@Nullable Entity entity) {
        return null;
    }

    @Override
    public Optional<Entity> getVehicle() {
        return null;
    }

    @Override
    public DataTransactionResult setVehicle(@Nullable Entity entity) {
        return null;
    }

    @Override
    public Entity getBaseVehicle() {
        return null;
    }

    @Override
    public Vector3d getRotation() {
        return this.position.getRotation();
    }

    @Override
    public void setRotation(Vector3d rotation) {
        this.position = this.position.setRotation(rotation);
    }

    @Override
    public boolean isOnGround() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isRemoved() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isLoaded() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void remove() {
        // TODO Auto-generated method stub
        
    }

    /**
     * Ticks the entity.
     */
    protected void tick() {

    }

    @Override
    public <T extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(Class<? extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> holderClass) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <E> DataTransactionResult transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> DataTransactionResult offer(BaseValue<E> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(org.spongepowered.api.data.manipulator.DataManipulator<?, ?> valueContainer) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(org.spongepowered.api.data.manipulator.DataManipulator<?, ?> valueContainer, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult offer(Iterable<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> valueContainers) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult
            offer(Iterable<org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> valueContainers, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(Class<? extends org.spongepowered.api.data.manipulator.DataManipulator<?, ?>> containerClass) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(BaseValue<?> value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult remove(Key<?> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult undo(DataTransactionResult result) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(DataHolder that) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrNull(Key<? extends BaseValue<E>> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E> E getOrElse(Key<? extends BaseValue<E>> key, E defaultValue) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean supports(Key<?> key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean supports(BaseValue<?> baseValue) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public DataHolder copy() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableSet<Key<?>> getKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableSet<ImmutableValue<?>> getValues() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Vector3d getScale() {
        return Vector3d.ONE;
    }

    @Override
    public void setScale(Vector3d scale) {
        // Not used currently
    }

    @Override
    public Transform<World> getTransform() {
        return this.position;
    }

    @Override
    public void setTransform(Transform<World> transform) {
        this.position = Preconditions.checkNotNull(transform);
    }

    @Override
    public void setLocationAndRotation(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean setLocationAndRotationSafely(Location<World> location, Vector3d rotation, EnumSet<RelativePositions> relativePositions) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public EntitySnapshot createSnapshot() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Random getRandom() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean damage(double damage, DamageSource damageSource, Cause cause) {
        // TODO: Damage modifiers, etc.
        DamageEntityEvent event = SpongeEventFactory.createDamageEntityEvent(
                cause, Lists.newArrayList(), this, damage);
        // TODO: Not cancellable?
        damage = event.getFinalDamage();
        if (damage > 0) {
            LanternHealthData data = (LanternHealthData) getOrCreate(HealthData.class).get();
            double health = get(Keys.HEALTH).get() - damage;

            this.offer(Keys.HEALTH, health);
            if (health <= 0.0) {
                // TODO: Notify stuff
            }
            return true;
        }
        return false;
    }

    @Override public Translation getTranslation() {
        return null;
    }
}
