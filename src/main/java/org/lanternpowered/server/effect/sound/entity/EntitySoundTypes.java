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

import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.entity.living.animal.Horse;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public final class EntitySoundTypes {

    // SORTFIELDS:ON

    /**
     * The {@link EntitySoundType} that is played when a entity gets angry. Is
     * in vanilla minecraft only used {@link Horse}s.
     */
    public static final EntitySoundType ANGRY = DummyObjectProvider.createFor(EntitySoundType.class, "ANGRY");

    /**
     * The {@link EntitySoundType} that is played when a entity dies.
     */
    public static final EntitySoundType DEATH = DummyObjectProvider.createFor(EntitySoundType.class, "DEATH");

    /**
     * The {@link EntitySoundType} that is played when a entity falls and hits the ground.
     * <p>
     * This is a special {@link EntitySoundType}, the fall height
     * will be available in the current {@link CauseStack} when the
     * {@link EntitySoundEffect} is being played.
     */
    public static final EntitySoundType FALL = DummyObjectProvider.createFor(EntitySoundType.class, "FALL");

    /**
     * The {@link EntitySoundType} that is played when a entity takes damage.
     */
    public static final EntitySoundType HURT = DummyObjectProvider.createFor(EntitySoundType.class, "HURT");

    /**
     * The {@link EntitySoundType} that is played when a entity is looking/wandering around.
     */
    public static final EntitySoundType IDLE = DummyObjectProvider.createFor(EntitySoundType.class, "IDLE");

    /**
     * The {@link EntitySoundType} that is played when a entity falls into the water.
     */
    public static final EntitySoundType SPLASH = DummyObjectProvider.createFor(EntitySoundType.class, "SPLASH");

    /**
     * The {@link EntitySoundType} that is played when a entity is swimming.
     */
    public static final EntitySoundType SWIM = DummyObjectProvider.createFor(EntitySoundType.class, "SWIM");

    /**
     * The {@link EntitySoundType} that is played when a villagers says no.
     */
    public static final EntitySoundType VILLAGER_NO = DummyObjectProvider.createFor(EntitySoundType.class, "VILLAGER_NO");

    /**
     * The {@link EntitySoundType} that is played when a villagers says yes.
     */
    public static final EntitySoundType VILLAGER_YES = DummyObjectProvider.createFor(EntitySoundType.class, "VILLAGER_YES");

    /**
     * The {@link EntitySoundType} that is played when a entity is walking.
     */
    public static final EntitySoundType WALK = DummyObjectProvider.createFor(EntitySoundType.class, "WALK");

    // SORTFIELDS:OFF

    private EntitySoundTypes() {
    }
}
