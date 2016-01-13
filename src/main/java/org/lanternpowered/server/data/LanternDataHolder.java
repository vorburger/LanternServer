/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.lanternpowered.server.data.util.DataQueries;
import org.lanternpowered.server.data.util.DataUtil;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

public abstract class LanternDataHolder implements DataHolder {

    private Map<Class<? extends DataManipulator<?, ?>>, DataManipulator<?, ?>> containerStore = Maps.newIdentityHashMap();
    protected Map<Class<Property<?, ?>>, Property<?, ?>> properties = Maps.newIdentityHashMap();
    protected static final Set<Class<? extends DataManipulator<?, ?>>> defaultManipulators = Sets.newIdentityHashSet();

    public LanternDataHolder(Map<Class<? extends DataManipulator<?, ?>>, DataManipulator<?, ?>> containerStore) {
        this.containerStore = Preconditions.checkNotNull(containerStore);
    }

    public LanternDataHolder(DataView container) {
        if(container.contains(DataQueries.DATA_MANIPULATORS)) {
            List<DataManipulator<?, ?>> manipulators = DataUtil.deserializeManipulatorList(container.getViewList(DataQueries.DATA_MANIPULATORS).get());
            manipulators.forEach(this::offer);
        }
    }

    /**
     * Gets the default manipulators associated with this object. These
     * manipulators will be added to the base object, rather than isolated.
     */
    public Set<DataManipulator<?, ?>> getDefaultManipulators() {
        return defaultManipulators.stream()
                .filter(containerStore::containsKey)
                .map(containerStore::get)
                .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> get(Class<T> containerClass) {
        if(containerStore.containsKey(containerClass)) {
            return Optional.of((T) containerStore.get(containerClass));
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends DataManipulator<?, ?>> Optional<T> getOrCreate(Class<T> containerClass) {
        if(get(containerClass).isPresent()) return get(containerClass);

        // TODO: How to cast properly?
        /* Optional<DataManipulatorBuilder<?, ?>> optional = Sponge.getDataManager().getManipulatorBuilder((Class) containerClass);
        if (optional.isPresent()) {
            return Optional.of((T) optional.get().create());
        }
        */

        return Optional.empty();
    }

    @Override
    public <E> DataTransactionResult offer(Key<? extends BaseValue<E>> key, E value) {
        return DataTransactionResult.failNoData(); //TODO: Implement
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataTransactionResult offer(DataManipulator<?, ?> valueContainer, MergeFunction function) {
        DataTransactionResult.Builder builder = DataTransactionResult.builder();
        DataManipulator<?, ?> existing = get(valueContainer.getClass()).orElse(null);
        DataManipulator<?, ?> merged = function.merge(valueContainer, existing);
        containerStore.put((Class<? extends DataManipulator<?, ?>>) valueContainer.getClass(), merged);
        if(existing != null) builder.replace(existing.getValues());

        return builder.success(merged.getValues())
                .result(DataTransactionResult.Type.SUCCESS)
                .build();
    }

    @Override
    public DataTransactionResult remove(Class<? extends DataManipulator<?, ?>> containerClass) {
        DataManipulator<?, ?> manipulator = containerStore.get(containerClass);
        if(manipulator != null) {
            this.containerStore.remove(containerClass);
            return DataTransactionResult.builder().replace(manipulator.getValues())
                    .result(DataTransactionResult.Type.SUCCESS).build();
        } else {
            return DataTransactionResult.failNoData();
        }
    }

    @Override
    public DataTransactionResult remove(Key<?> key) {
        final Iterator<DataManipulator<?, ?>> iterator = this.containerStore.values().iterator();
        while (iterator.hasNext()) {
            final DataManipulator<?, ?> manipulator = iterator.next();
            if (manipulator.getKeys().size() == 1 && manipulator.supports(key)) {
                iterator.remove();
                return DataTransactionResult.builder()
                        .replace(manipulator.getValues())
                        .result(DataTransactionResult.Type.SUCCESS)
                        .build();
            }
        }
        return DataTransactionResult.failNoData();
    }

    @Override
    public DataTransactionResult undo(DataTransactionResult result) {
        if (result.getReplacedData().isEmpty() && result.getSuccessfulData().isEmpty()) {
            return DataTransactionResult.successNoData();
        }
        final DataTransactionResult.Builder builder = DataTransactionResult.builder();
        for (ImmutableValue<?> replaced : result.getReplacedData()) {
            builder.absorbResult(offer(replaced));
        }
        for (ImmutableValue<?> successful : result.getSuccessfulData()) {
            builder.absorbResult(remove(successful));
        }
        return builder.build();
    }

    @Override
    public DataTransactionResult copyFrom(DataHolder that, MergeFunction function) {
        return offer(that.getContainers(), function);
    }

    @Override
    public Set<DataManipulator<?, ?>> getContainers() {
        return Sets.newHashSet(containerStore.values());
    }

    @Override
    public DataContainer toContainer() {
        DataContainer ret = new MemoryDataContainer();
        Set<DataManipulator<?, ?>> defaults = getDefaultManipulators();
        Set<DataManipulator<?, ?>> customs = Sets.difference(getDefaultManipulators(), getContainers());

        for(DataManipulator<?, ?> manipulator : defaults) {
            DataUtil.merge(ret, manipulator.toContainer());
        }
        ret.set(DataQueries.DATA_MANIPULATORS, DataUtil.getSerializedManipulatorList(customs));

        return ret;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Property<?, ?>> Optional<T> getProperty(Class<T> propertyClass) {
        return Optional.ofNullable((T) properties.get(propertyClass));
    }

    @Override
    public Collection<Property<?, ?>> getApplicableProperties() {
        return Collections.unmodifiableCollection(properties.values());
    }

    @Override
    public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
        for(DataManipulator<?, ?> manipulator : this.containerStore.values()) {
            if(manipulator.supports(key)) {
                return manipulator.get(key);
            }
        }
        return Optional.empty();
    }

    @Override
    public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
        for(DataManipulator<?, ?> manipulator : this.containerStore.values()) {
            if(manipulator.supports(key)) {
                return manipulator.getValue(key);
            }
        }
        return Optional.empty();
    }

    @Override
    public Set<Key<?>> getKeys() {
        ImmutableSet.Builder<Key<?>> builder = ImmutableSet.builder();
        containerStore.values().forEach(data -> builder.addAll(data.getKeys()));
        return builder.build();
    }

    @Override
    public Set<ImmutableValue<?>> getValues() {
        ImmutableSet.Builder<ImmutableValue<?>> builder = ImmutableSet.builder();
        containerStore.values().forEach(data -> builder.addAll(data.getValues()));
        return builder.build();
    }

    @Override
    public boolean supports(Class<? extends DataManipulator<?, ?>> holderClass) {
        return false; //TODO: Implement
    }

    @Override
    public boolean supports(Key<?> key) {
        return false; //TODO: Implement
    }

    @Override
    public boolean validateRawData(DataContainer container) {
        return false; //TODO: Implement
    }

    @Override
    public void setRawData(DataContainer container) throws InvalidDataException {
        //TODO: Implement
    }

    @Override
    public <E> DataTransactionResult transform(Key<? extends BaseValue<E>> key, Function<E, E> function) {
        return DataTransactionResult.failNoData(); //TODO: Implement
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

}