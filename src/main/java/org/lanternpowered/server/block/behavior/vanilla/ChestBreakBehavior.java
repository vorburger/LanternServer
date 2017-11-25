package org.lanternpowered.server.block.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.behavior.types.BreakBlockBehavior;
import org.lanternpowered.server.block.tile.vanilla.LanternChest;
import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternChestConnection;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ChestBreakBehavior implements BreakBlockBehavior {

    @Override
    public BehaviorResult tryBreak(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        Location<World> location = context.requireContext(ContextKeys.BLOCK_LOCATION);
        final BlockState state = location.getBlock();
        final Direction connectionDir = LanternChest.getConnectedDirection(state);
        if (connectionDir != Direction.NONE) {
            location = location.getBlockRelative(connectionDir);
            final BlockState relState = location.getBlock();
            if (relState.getType() == state.getType() &&
                    relState.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get() ==
                    state.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get()) {
                final LanternChestConnection relConnection = relState.getTraitValue(LanternEnumTraits.CHEST_CONNECTION).get();
                final LanternChestConnection connection = state.getTraitValue(LanternEnumTraits.CHEST_CONNECTION).get();
                if ((relConnection == LanternChestConnection.LEFT && connection == LanternChestConnection.RIGHT) ||
                        (relConnection == LanternChestConnection.RIGHT && connection == LanternChestConnection.LEFT)) {
                    context.addBlockChange(BlockSnapshot.builder()
                            .from(location)
                            .add(LanternKeys.CHEST_CONNECTION, LanternChestConnection.SINGLE)
                            .build());
                }
            }
        }
        return BehaviorResult.CONTINUE;
    }
}
