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
package org.lanternpowered.server.cause.entity.damage.source;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.data.manipulator.immutable.entity.ImmutableFallingBlockData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.FallingBlock;
import org.spongepowered.api.event.cause.entity.damage.source.FallingBlockDamageSource;

import javax.annotation.Nullable;

public class LanternFallingBlockDamageSourceBuilder extends AbstractEntityDamageSourceBuilder<FallingBlockDamageSource,
        FallingBlockDamageSource.Builder, LanternFallingBlockDamageSourceBuilder> implements FallingBlockDamageSource.Builder {

    @Nullable protected ImmutableFallingBlockData fallingBlockData;

    @Override
    public LanternFallingBlockDamageSourceBuilder entity(Entity entity) {
        checkNotNull(entity, "Entity source cannot be null!");
        checkArgument(entity instanceof FallingBlock, "Entity source must be a falling block!");
        return super.entity(entity);
    }

    @Override
    public LanternFallingBlockDamageSourceBuilder fallingBlock(ImmutableFallingBlockData fallingBlockData) {
        this.fallingBlockData = checkNotNull(fallingBlockData, "fallingBlockData");
        return this;
    }

    @Override
    public FallingBlockDamageSource build() throws IllegalStateException {
        checkState(this.source != null, "The falling block entity must be set");
        return new LanternFallingBlockDamageSource(this);
    }
}
