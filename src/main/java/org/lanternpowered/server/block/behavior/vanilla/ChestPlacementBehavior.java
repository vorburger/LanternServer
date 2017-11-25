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
package org.lanternpowered.server.block.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternChestConnection;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Arrays;
import java.util.List;

public class ChestPlacementBehavior implements PlaceBlockBehavior {

    private static final List<Direction> EW = Arrays.asList(Direction.EAST, Direction.WEST);
    private static final List<Direction> WE = Arrays.asList(Direction.EAST, Direction.WEST);
    private static final List<Direction> NS = Arrays.asList(Direction.NORTH, Direction.SOUTH);
    private static final List<Direction> SN = Arrays.asList(Direction.NORTH, Direction.SOUTH);

    private static List<Direction> getConnectionDirections(Direction direction) {
        switch (direction) {
            case NORTH:
                return WE;
            case SOUTH:
                return EW;
            case EAST:
                return NS;
            case WEST:
                return SN;
            default:
                throw new IllegalStateException();
        }
    }

    @Override
    public BehaviorResult tryPlace(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final BlockSnapshot snapshot = context.getContext(ContextKeys.BLOCK_SNAPSHOT)
                .orElseThrow(() -> new IllegalStateException("The BlockSnapshotProviderPlaceBehavior's BlockSnapshot isn't present."));
        final Location<World> location = context.requireContext(ContextKeys.BLOCK_LOCATION);

        final BlockType blockType = context.requireContext(ContextKeys.BLOCK_TYPE);
        final Direction face = context.requireContext(ContextKeys.INTERACTION_FACE);

        final LanternPlayer player = (LanternPlayer) context.getContext(ContextKeys.PLAYER).orElse(null);
        // Get the direction the chest should face
        Direction facing;
        if (player != null) {
            facing = player.getHorizontalDirection(Direction.Division.CARDINAL).getOpposite();
        } else {
            facing = Direction.NORTH;
        }

        LanternChestConnection connection = null;
        Direction direction = null;

        boolean sneaking = false;
        if (player != null && player.get(Keys.IS_SNEAKING).get()) {
            sneaking = true;
        }

        System.out.println(face);
        System.out.println(location.getBlockPosition());
        Location<World> relLocation = location.getBlockRelative(face.getOpposite());
        System.out.println(relLocation.getBlockPosition());
        BlockState relState = relLocation.getBlock();
        System.out.println("DEBUG: " + relState.getId());
        if (relState.getType() == blockType) {
            System.out.println("DEBUG A");
            final LanternChestConnection relConnection = relState.getTraitValue(LanternEnumTraits.CHEST_CONNECTION).get();
            if (relConnection != LanternChestConnection.SINGLE) {
                System.out.println("DEBUG B");
                final Direction relFacing = relState.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get();
                final List<Direction> dirs = getConnectionDirections(relFacing);
                int index = dirs.indexOf(face);
                if (index != -1) {
                    connection = index == 0 ? LanternChestConnection.LEFT : LanternChestConnection.RIGHT;
                    direction = relFacing;
                    context.addBlockChange(BlockSnapshot.builder()
                            .from(relLocation.createSnapshot())
                            .add(LanternKeys.CHEST_CONNECTION, index == 0 ? LanternChestConnection.RIGHT : LanternChestConnection.LEFT)
                            .build());
                } else {
                    return BehaviorResult.FAIL;
                }
            } else {
                sneaking = true;
            }
        }
        if (connection == null) {
            direction = facing;
            if (!sneaking) {
                final List<Direction> dirs = getConnectionDirections(direction);
                int index = 0;
                for (Direction relDirection : dirs) {
                    relLocation = location.getBlockRelative(relDirection);
                    relState = relLocation.getBlock();
                    if (relState.getType() == blockType) {
                        final LanternChestConnection relConnection = relState.getTraitValue(LanternEnumTraits.CHEST_CONNECTION).get();
                        final Direction relFacing = relState.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get();
                        if (relFacing == facing && relConnection == LanternChestConnection.SINGLE) {
                            connection = index == 0 ? LanternChestConnection.LEFT : LanternChestConnection.RIGHT;
                            context.addBlockChange(BlockSnapshot.builder()
                                    .from(relLocation.createSnapshot())
                                    .add(LanternKeys.CHEST_CONNECTION, index == 0 ? LanternChestConnection.RIGHT : LanternChestConnection.LEFT)
                                    .build());
                            break;
                        }
                    }
                    index++;
                }
            }
            if (connection == null) {
                connection = LanternChestConnection.SINGLE;
            }
        }

        context.addBlockChange(BlockSnapshotBuilder.create().from(snapshot).location(location)
                .add(Keys.DIRECTION, direction)
                .add(LanternKeys.CHEST_CONNECTION, connection)
                .build());
        return BehaviorResult.SUCCESS;
    }
}
