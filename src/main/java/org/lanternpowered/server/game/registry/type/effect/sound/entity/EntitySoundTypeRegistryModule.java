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
package org.lanternpowered.server.game.registry.type.effect.sound.entity;

import org.lanternpowered.server.effect.sound.entity.EntitySoundType;
import org.lanternpowered.server.effect.sound.entity.EntitySoundTypes;
import org.lanternpowered.server.effect.sound.entity.LanternEntitySoundType;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;

public final class EntitySoundTypeRegistryModule extends PluginCatalogRegistryModule<EntitySoundType> {

    public EntitySoundTypeRegistryModule() {
        super(EntitySoundTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternEntitySoundType("minecraft", "angry"));
        register(new LanternEntitySoundType("minecraft", "death"));
        register(new LanternEntitySoundType("minecraft", "fall"));
        register(new LanternEntitySoundType("minecraft", "hurt"));
        register(new LanternEntitySoundType("minecraft", "idle"));
        register(new LanternEntitySoundType("minecraft", "lightning"));
        register(new LanternEntitySoundType("minecraft", "splash"));
        register(new LanternEntitySoundType("minecraft", "swim"));
        register(new LanternEntitySoundType("minecraft", "villager_no"));
        register(new LanternEntitySoundType("minecraft", "villager_yes"));
        register(new LanternEntitySoundType("minecraft", "walk"));
    }
}
