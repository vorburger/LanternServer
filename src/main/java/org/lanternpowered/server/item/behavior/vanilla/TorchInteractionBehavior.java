package org.lanternpowered.server.item.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;

public class TorchInteractionBehavior implements InteractWithItemBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        //TODO
        return BehaviorResult.PASS;
    }
}
