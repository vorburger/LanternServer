package org.lanternpowered.server.world.schematic;

import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.world.schematic.BlockPalette;
import org.spongepowered.api.world.schematic.BlockPaletteType;

import java.util.function.Supplier;

public class LanternBlockPaletteType extends SimpleCatalogType.Base implements BlockPaletteType {

    private final Supplier<BlockPalette> supplier;

    public LanternBlockPaletteType(String identifier, Supplier<BlockPalette> supplier) {
        super(identifier);
        this.supplier = supplier;
    }

    @Override
    public BlockPalette create() {
        return this.supplier.get();
    }
}
