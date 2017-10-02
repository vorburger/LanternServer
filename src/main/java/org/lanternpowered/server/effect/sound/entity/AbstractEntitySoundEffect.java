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
package org.lanternpowered.server.effect.sound.entity;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.property.entity.EyeHeightProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@SuppressWarnings("ConstantConditions")
public abstract class AbstractEntitySoundEffect implements EntitySoundEffect {

    private final EntitySoundPosition position;

    protected AbstractEntitySoundEffect(EntitySoundPosition position) {
        this.position = position;
    }

    protected AbstractEntitySoundEffect() {
        this(EntitySoundPosition.FEET);
    }

    @Override
    public void play(Entity entity) {
        final Random random = ThreadLocalRandom.current();
        final Location<World> location = entity.getLocation();
        Vector3d position = location.getPosition();
        if (this.position == EntitySoundPosition.HEAD) {
            final EyeHeightProperty eyeHeightProperty = entity.getProperty(EyeHeightProperty.class).orElse(null);
            if (eyeHeightProperty != null) {
                position = position.add(0, eyeHeightProperty.getValue(), 0);
            }
        }
        play(entity, location.getExtent(), position, random);
    }

    protected abstract void play(Entity entity, World world, Vector3d position, Random random);
}
