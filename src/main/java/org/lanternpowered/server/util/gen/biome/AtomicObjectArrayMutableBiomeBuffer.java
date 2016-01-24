/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.util.gen.biome;

import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2i;
import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeAreaWorker;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.worker.MutableBiomeAreaWorker;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Mutable view of a {@link BiomeType} array.
 *
 * <p>Normally, the {@link AtomicShortArrayMutableBiomeBuffer} class uses memory more
 * efficiently, but when the {@link BiomeType} array is already created (for
 * example for a contract specified by Minecraft) this implementation becomes
 * more efficient.</p>
 */
@NonnullByDefault
public final class AtomicObjectArrayMutableBiomeBuffer extends AbstractMutableBiomeBuffer implements MutableBiomeArea {

    private final AtomicReferenceArray<BiomeType> biomes;

    /**
     * Creates a new instance.
     *
     * @param biomes The biome array
     * @param start The start position
     * @param size The size
     */
    public AtomicObjectArrayMutableBiomeBuffer(BiomeType[] biomes, Vector2i start, Vector2i size) {
        super(start, size);
        this.biomes = new AtomicReferenceArray<>(biomes);
    }

    /**
     * Creates a new instance.
     *
     * @param biomes The biome array. The array is not copied, so changes made
     *        by this object will write through.
     * @param start The start position
     * @param size The size
     */
    private AtomicObjectArrayMutableBiomeBuffer(AtomicReferenceArray<BiomeType> biomes, Vector2i start, Vector2i size) {
        super(start, size);
        this.biomes = biomes;
    }

    @Override
    public BiomeType getBiome(int x, int z) {
        this.checkRange(x, z);
        return this.biomes.get(this.index(x, z));
    }

    @Override
    public MutableBiomeArea getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                final BiomeType[] array = new BiomeType[this.biomes.length()];
                for (int i = 0; i < array.length; i++) {
                    array[i] = this.biomes.get(i);
                }
                return new ObjectArrayMutableBiomeBuffer(array, this.start, this.size);
            case THREAD_SAFE:
                final AtomicReferenceArray<BiomeType> copy = new AtomicReferenceArray<>(this.biomes.length());
                for (int i = 0; i < copy.length(); i++) {
                    copy.set(i, this.biomes.get(i));
                }
                return new AtomicObjectArrayMutableBiomeBuffer(copy, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBiomeArea getImmutableBiomeCopy() {
        final BiomeType[] array = new BiomeType[this.biomes.length()];
        for (int i = 0; i < array.length; i++) {
            array[i] = this.biomes.get(i);
        }
        return new ObjectArrayImmutableBiomeBuffer(array, this.start, this.size);
    }

    @Override
    public void setBiome(int x, int z, BiomeType biome) {
        checkNotNull(biome, "biome");
        this.checkRange(x, z);
        this.biomes.set(this.index(x, z), biome);
    }

    @Override
    public MutableBiomeAreaWorker<? extends MutableBiomeArea> getBiomeWorker() {
        return new LanternMutableBiomeAreaWorker<>(this);
    }
}
