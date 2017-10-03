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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.ResettableBuilder;

import java.util.Optional;

public interface EntitySoundCollection {

    /**
     * Constructs a new {@link Builder}.
     *
     * @return The builder
     */
    static Builder builder() {
        return Sponge.getRegistry().createBuilder(Builder.class);
    }

    /**
     * Attempts to get the {@link EntitySoundEffect} for the given
     * {@link EntitySoundType}.
     *
     * @param soundType The entity sound type
     * @return The entity sound effect
     */
    Optional<EntitySoundEffect> get(EntitySoundType soundType);

    /**
     * Attempts to get the {@link EntitySoundEffect} for the given
     * {@link EntitySoundType}. {@link EntitySoundEffect#NONE} will
     * be returned if no entry could be found.
     *
     * @param soundType The entity sound type
     * @return The entity sound effect
     */
    default EntitySoundEffect getOrEmpty(EntitySoundType soundType) {
        return get(soundType).orElse(EntitySoundEffect.NONE);
    }

    /**
     * A builder to construct {@link EntitySoundCollection}s.
     */
    interface Builder extends ResettableBuilder<EntitySoundCollection, Builder> {

        /**
         * Adds a {@link EntitySoundEffect} for the
         * given {@link EntitySoundType}.
         *
         * @param soundType The entity sound type
         * @param soundEffect The entity sound effect
         * @return This builder, for chaining
         */
        Builder add(EntitySoundType soundType, EntitySoundEffect soundEffect);

        /**
         * Builds a {@link EntitySoundCollection}.
         *
         * @return The entity sound collection
         */
        EntitySoundCollection build();
    }
}
