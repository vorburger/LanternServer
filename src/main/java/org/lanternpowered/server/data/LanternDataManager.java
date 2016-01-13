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
package org.lanternpowered.server.data;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.lanternpowered.server.config.serializer.DataSerializableTypeSerializer;
import org.spongepowered.api.data.DataManager;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.ImmutableDataBuilder;
import org.spongepowered.api.data.ImmutableDataHolder;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.persistence.DataBuilder;
import org.spongepowered.api.data.persistence.DataContentUpdater;

public final class LanternDataManager implements DataManager {

    static {
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(DataSerializable.class), new DataSerializableTypeSerializer());
    }

    private static final LanternDataManager instance = new LanternDataManager();

    private final Map<Class<? extends DataManipulator<?, ?>>, DataManipulatorBuilder<?, ?>> builderMap = new MapMaker()
            .concurrencyLevel(4)
            .makeMap();
    private final Map<Class<? extends ImmutableDataManipulator<?, ?>>, DataManipulatorBuilder<?, ?>> immutableBuilderMap = new MapMaker()
            .concurrencyLevel(4)
            .makeMap();

    private final Map<Class<?>, DataBuilder<?>> builders = Maps.newHashMap();
    private final Map<Class<? extends ImmutableDataHolder<?>>, ImmutableDataBuilder<?, ?>> immutableDataBuilderMap = new MapMaker().concurrencyLevel(4).makeMap();
    private boolean registrationComplete = false;
    private static boolean allowRegistrations = true;

    public void completeRegistration() {
        checkState(allowRegistrations);
        allowRegistrations = false;
    }

    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> void register(Class<? extends T> manipulatorClass, Class<? extends I> immutableManipulatorClass, DataManipulatorBuilder<T, I> builder) {
        checkState(allowRegistrations, "Registrations are no longer allowed!");
        if (!this.builderMap.containsKey(checkNotNull(manipulatorClass))) {
            this.builderMap.put(manipulatorClass, checkNotNull(builder));
            this.immutableBuilderMap.put(checkNotNull(immutableManipulatorClass), builder);
            registerBuilder((Class<T>) manipulatorClass, builder);
        } else {
            throw new IllegalStateException("Already registered the DataUtil for " + manipulatorClass.getCanonicalName());
        }
    }

    @Override
    public <T extends DataSerializable> void registerBuilder(Class<T> clazz, DataBuilder<T> builder) {
        checkNotNull(clazz);
        checkNotNull(builder);
        checkState(!this.registrationComplete);
        if (!this.builders.containsKey(clazz)) {
            this.builders.put(clazz, builder);
        }
    }

    @Override
    public <T extends DataSerializable> void registerContentUpdater(Class<T> clazz, DataContentUpdater updater) {

    }

    @Override
    public <T extends DataSerializable> Optional<DataContentUpdater> getWrappedContentUpdater(Class<T> clazz, int fromVersion, int toVersion) {
        return null;
    }

    @Override
    public <T extends DataSerializable> Optional<DataBuilder<T>> getBuilder(Class<T> clazz) {
        checkNotNull(clazz);
        if (this.builders.containsKey(clazz)) {
            return Optional.of((DataBuilder<T>) this.builders.get(clazz));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T extends DataSerializable> Optional<T> deserialize(Class<T> clazz, DataView dataView) {
        final Optional<DataBuilder<T>> optional = getBuilder(clazz);
        if (optional.isPresent()) {
            return optional.get().build(dataView);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public <T extends ImmutableDataHolder<T>, B extends ImmutableDataBuilder<T, B>> void register(Class<T> holderClass, B builder) {
        if (!this.immutableDataBuilderMap.containsKey(checkNotNull(holderClass))) {
            this.immutableDataBuilderMap.put(holderClass, checkNotNull(builder));
        } else {
            throw new IllegalStateException("Already registered the DataUtil for " + holderClass.getCanonicalName());
        }
    }

    @Override
    public <T extends ImmutableDataHolder<T>, B extends ImmutableDataBuilder<T, B>> Optional<B> getImmutableBuilder(Class<T> holderClass) {
        return Optional.ofNullable((B) this.immutableDataBuilderMap.get(checkNotNull(holderClass)));
    }

    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> Optional<DataManipulatorBuilder<T, I>> getManipulatorBuilder(Class<T> manipulatorClass) {
        return Optional.ofNullable((DataManipulatorBuilder<T, I>) this.builderMap.get(checkNotNull(manipulatorClass)));
    }

    @Override
    public <T extends DataManipulator<T, I>, I extends ImmutableDataManipulator<I, T>> Optional<DataManipulatorBuilder<T, I>> getImmutableManipulatorBuilder(Class<I> immutableManipulatorClass) {
        return Optional.ofNullable((DataManipulatorBuilder<T, I>) this.immutableBuilderMap.get(checkNotNull(immutableManipulatorClass)));
    }

    public static void finalizeRegistration() {
        //TODO: Implement
    }
}
