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
package org.lanternpowered.server.data.type;

import org.lanternpowered.server.catalog.InternalCatalogType;
import org.lanternpowered.server.catalog.SimpleCatalogType;
import org.spongepowered.api.data.type.RailDirection;

public enum LanternRailDirection implements SimpleCatalogType, RailDirection, InternalCatalogType {

    NORTH_SOUTH         ("north_south"),
    EAST_WEST           ("east_west"),
    ASCENDING_EAST      ("ascending_east"),
    ASCENDING_WEST      ("ascending_west"),
    ASCENDING_NORTH     ("ascending_north"),
    ASCENDING_SOUTH     ("ascending_south"),
    SOUTH_EAST          ("south_east"),
    SOUTH_WEST          ("south_west"),
    NORTH_WEST          ("north_west"),
    NORTH_EAST          ("north_east"),
    ;

    private final String id;
    private RailDirection next;

    LanternRailDirection(String id) {
        this.id = id;
    }

    @Override
    public RailDirection cycleNext() {
        return this.next;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public int getInternalId() {
        return ordinal();
    }

    static {
        final LanternRailDirection[] values = values();
        for (int i = 0; i < values.length; i++) {
            values[i].next = values[(i + 1) % values.length];
        }
    }
}
