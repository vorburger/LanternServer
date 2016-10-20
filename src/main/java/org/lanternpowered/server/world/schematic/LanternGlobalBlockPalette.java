package org.lanternpowered.server.world.schematic;

import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.schematic.BlockPalette;
import org.spongepowered.api.world.schematic.BlockPaletteType;
import org.spongepowered.api.world.schematic.BlockPaletteTypes;

import java.util.Collection;
import java.util.Optional;

public class LanternGlobalBlockPalette implements BlockPalette {

    public static LanternGlobalBlockPalette INSTANCE = new LanternGlobalBlockPalette();

    private final int length;

    private LanternGlobalBlockPalette() {
        int highest = 0;
        for (BlockState state : Sponge.getRegistry().getAllOf(BlockState.class)) {
            int id = BlockRegistryModule.get().getStateInternalIdAndData(state);
            if (id > highest) {
                highest = id;
            }
        }
        this.length = highest;
    }

    @Override
    public BlockPaletteType getType() {
        return BlockPaletteTypes.GLOBAL;
    }

    @Override
    public int getHighestId() {
        return this.length;
    }

    @Override
    public Optional<Integer> get(BlockState state) {
        return Optional.of((int) BlockRegistryModule.get().getStateInternalIdAndData(state));
    }

    @Override
    public int getOrAssign(BlockState state) {
        return BlockRegistryModule.get().getStateInternalIdAndData(state);
    }

    @Override
    public Optional<BlockState> get(int id) {
        return BlockRegistryModule.get().getStateByInternalIdAndData(id);
    }

    @Override
    public boolean remove(BlockState state) {
        throw new UnsupportedOperationException("Cannot remove blockstates from the global palette");
    }

    @Override
    public Collection<BlockState> getEntries() {
        return Sponge.getRegistry().getAllOf(BlockState.class);
    }
}
