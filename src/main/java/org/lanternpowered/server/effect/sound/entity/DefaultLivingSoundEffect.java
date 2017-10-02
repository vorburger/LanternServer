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
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;

import java.util.Random;

public class DefaultLivingSoundEffect extends AbstractEntitySoundEffect {

    private final SoundType soundType;
    private final SoundCategory soundCategory;

    public DefaultLivingSoundEffect(EntitySoundPosition position, SoundType soundType, SoundCategory soundCategory) {
        super(position);
        this.soundCategory = soundCategory;
        this.soundType = soundType;
    }

    public DefaultLivingSoundEffect(EntitySoundPosition position, SoundType soundType) {
        this(position, soundType, SoundCategories.NEUTRAL);
    }

    /**
     * Gets a randomized volume value for the sound effect.
     *
     * @param random The random
     * @return The volume value
     */
    protected double getVolume(Entity entity, Random random) {
        return 1.0;
    }

    /**
     * Gets a randomized pitch value for the sound effect.
     *
     * @param random The random
     * @return The pitch value
     */
    protected double getPitch(Entity entity, Random random) {
        double value = random.nextFloat() - random.nextFloat() * 0.2;
        // Adults and children use a different pitch value
        if (entity.get(Keys.IS_ADULT).orElse(true)) {
            value += 1.0;
        } else {
            value += 1.5;
        }
        return value;
    }

    @Override
    protected void play(Entity entity, World world, Vector3d position, Random random) {
        world.playSound(this.soundType, this.soundCategory, position, getVolume(entity, random), getPitch(entity, random));
    }
}
