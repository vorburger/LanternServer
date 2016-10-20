package org.lanternpowered.server.world.schematic;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.schematic.BlockPalette;
import org.spongepowered.api.world.schematic.BlockPaletteType;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;

import java.util.BitSet;
import java.util.Collection;
import java.util.Optional;

public class LanternBlockPalette implements BlockPalette {

    private static final int DEFAULT_ALLOCATION_SIZE = 64;

    private final Object2IntMap<BlockState> idByState = new Object2IntOpenHashMap<>();
    private final Int2ObjectMap<BlockState> stateById = new Int2ObjectOpenHashMap<>();

    private final BitSet allocation = new BitSet(DEFAULT_ALLOCATION_SIZE);
    private int maxId = 0;

    @Override
    public BlockPaletteType getType() {
        return BlockPaletteTypes.LOCAL;
    }

    @Override
    public int getHighestId() {
        return this.maxId;
    }

    @Override
    public Optional<Integer> get(BlockState state) {
        return null;
    }

    @Override
    public int getOrAssign(BlockState state) {
        return 0;
    }

    @Override
    public Optional<BlockState> get(int id) {
        return null;
    }

    @Override
    public boolean remove(BlockState state) {
        return false;
    }

    @Override
    public Collection<BlockState> getEntries() {
        return null;
    }
}
