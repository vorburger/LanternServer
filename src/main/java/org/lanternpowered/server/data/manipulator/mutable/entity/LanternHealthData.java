package org.lanternpowered.server.data.manipulator.mutable.entity;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableHealthData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.merge.MergeFunction;

import java.util.Optional;

public class LanternHealthData extends AbstractData<HealthData, ImmutableHealthData> {

    @Override
    public Optional<HealthData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return null;
    }

    @Override
    public Optional<HealthData> from(DataContainer container) {
        return null;
    }

    @Override
    public HealthData copy() {
        return null;
    }

    @Override
    public ImmutableHealthData asImmutable() {
        return null;
    }

    @Override
    public int compareTo(HealthData o) {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        return null;
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    protected void registerGettersAndSetters() {

    }
}
