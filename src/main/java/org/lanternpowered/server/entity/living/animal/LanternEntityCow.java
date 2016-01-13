/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.entity.living.animal;

import org.lanternpowered.server.entity.living.LanternEntityAgent;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.mutable.entity.AgeableData;
import org.spongepowered.api.data.manipulator.mutable.entity.BreedableData;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.animal.Cow;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

public class LanternEntityCow extends LanternEntityAgent implements Cow {

    static {
        defaultManipulators.add(AgeableData.class);
        defaultManipulators.add(BreedableData.class);
    }

    public LanternEntityCow(DataView container, Transform<World> position) {
        super(container, position);
    }

    @Override
    public EntityType getType() {
        return EntityTypes.COW;
    }

    @Override
    public Text getTeamRepresentation() {
        return null; //TODO: Implement
    }

    @Override
    public void setScaleForAge() {
        //TODO: Implement
    }

    @Override
    public DataHolder copy() {
        return new LanternEntityCow(this.toContainer(), this.getTransform());
    }

}
