package org.lanternpowered.server.data.manipulator.mutable.common;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;

import java.util.Optional;

public abstract class LanternAbstractData<M extends DataManipulator<M, I>, I extends ImmutableDataManipulator<I, M>> extends AbstractData<M, I> {

    @Override
    public Optional<M> fill(DataHolder dataHolder, MergeFunction overlap) {

        return null;
    }

    @Override
    public Optional<M> from(DataContainer container) {
        return null;
    }

    @Override
    public M copy() {
        return null;
    }

    @Override
    public I asImmutable() {
        return null;
    }

    @Override
    public int compareTo(M o) {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return null;
    }
}
