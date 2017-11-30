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
package org.lanternpowered.server.item;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.entity.living.player.CooldownTracker;
import org.spongepowered.api.item.ItemType;

import java.util.OptionalDouble;
import java.util.OptionalInt;

public class LanternCooldownTracker implements CooldownTracker {

    private final Object2LongMap<ItemType> map = new Object2LongOpenHashMap<>();

    {
        this.map.defaultReturnValue(-1L);
    }

    @Override
    public void setCooldown(ItemType itemType, int ticks) {
        checkNotNull(itemType, "itemType");
        if (ticks <= 0) {
            resetCooldown(itemType);
        } else {
            synchronized (this.map) {
                this.map.put(itemType, LanternGame.currentTimeTicks() + ticks);
            }
            set0(itemType, ticks);
        }
    }

    protected void set0(ItemType itemType, int cooldown) {
    }

    @Override
    public void resetCooldown(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        final long time;
        synchronized (this.map) {
            time = this.map.remove(itemType);
        }
        if (time == -1L || time - LanternGame.currentTimeTicks() <= 0) {
            return;
        }
        remove0(itemType);
    }

    protected void remove0(ItemType itemType) {
    }

    @Override
    public OptionalInt getCooldown(ItemType itemType) {
        checkNotNull(itemType, "itemType");
       synchronized (this.map) {
            final long time = this.map.get(itemType);
            if (time != -1L) {
                final long current = LanternGame.currentTimeTicks();
                if (time <= current) {
                    this.map.remove(itemType);
                } else {
                    return OptionalInt.of((int) (time - current));
                }
            }
            return OptionalInt.empty();
        }
    }

    @Override
    public boolean hasCooldown(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        synchronized (this.map) {
            final long time = this.map.get(itemType);
            if (time != -1L) {
                final long current = LanternGame.currentTimeTicks();
                if (time <= current) {
                    this.map.remove(itemType);
                } else {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public OptionalDouble getFractionRemaining(ItemType type) {
        // TODO: Properly implement this
        return hasCooldown(type) ? OptionalDouble.of(1.0) : OptionalDouble.empty();
    }
}
