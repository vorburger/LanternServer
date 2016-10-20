package org.lanternpowered.server.world.schematic;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
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

public class LanternLocalBlockPalette implements BlockPalette {

    private static final int DEFAULT_ALLOCATION_SIZE = 64;

    private final Object2IntMap<BlockState> idByState = new Object2IntOpenHashMap<>();
    private final Int2ObjectMap<BlockState> stateById = new Int2ObjectOpenHashMap<>();

    {
        this.idByState.defaultReturnValue(-1);
    }

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
        checkNotNull(state, "state");
        final int id = this.idByState.getInt(state);
        return id == -1 ? Optional.empty() : Optional.of(id);
    }

    @Override
    public int getOrAssign(BlockState state) {
        checkNotNull(state, "state");
        final int id = this.idByState.getInt(state);
        if (id != -1) {
            return id;
        }
        final int next = this.allocation.nextClearBit(0);
        assign(state, next);
        return next;
    }

    @Override
    public Optional<BlockState> get(int id) {
        return Optional.ofNullable(this.stateById.get(id));
    }

    @Override
    public boolean remove(BlockState state) {
        final int id = this.idByState.getInt(state);
        if (id == -1) {
            return false;
        }
        this.allocation.clear(id);
        if (id == this.maxId) {
            this.maxId = this.allocation.previousSetBit(this.maxId);
        }
        this.stateById.remove(id);
        this.idByState.remove(state);
        return true;
    }

    @Override
    public Collection<BlockState> getEntries() {
        return ImmutableSet.copyOf(this.idByState.keySet());
    }

    public void assign(BlockState state, int id) {
        if (this.maxId < id) {
            this.maxId = id;
        }
        this.allocation.set(id);
        this.stateById.put(id, state);
        this.idByState.put(state, id);
    }
}
