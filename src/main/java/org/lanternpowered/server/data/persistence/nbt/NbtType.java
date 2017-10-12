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
package org.lanternpowered.server.data.persistence.nbt;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

enum NbtType {
    // Official types
    END                     (0),
    BYTE                    (1),
    SHORT                   (2),
    INT                     (3),
    LONG                    (4),
    FLOAT                   (5),
    DOUBLE                  (6),
    BYTE_ARRAY              (7),
    STRING                  (8),
    LIST                    (9),
    COMPOUND                (10),
    INT_ARRAY               (11),

    // Sponge and lantern types, but remaining
    // compatible with the official ones.
    BOOLEAN                 (1, "Boolean"),
    BOOLEAN_ARRAY           (7, "BoolAr"),
    BOOLEAN_BOXED_ARRAY     (7, "BoolBAr"),
    BYTE_BOXED_ARRAY        (7, "ByBAr"),
    SHORT_ARRAY             (7, "ShAr"),
    SHORT_BOXED_ARRAY       (7, "ShBAr"),
    INT_BOXED_ARRAY         (11, "IntBAr"),
    LONG_ARRAY              (11, "LngAr"),
    LONG_BOXED_ARRAY        (11, "LngBAr"),
    FLOAT_ARRAY             (11, "FltAr"),
    FLOAT_BOXED_ARRAY       (11, "FltBAr"),
    DOUBLE_ARRAY            (11, "DblAr"),
    DOUBLE_BOXED_ARRAY      (11, "DblBAr"),
    STRING_ARRAY            (7, "StrAr"),
    CHAR                    (2, "Char"),
    CHAR_ARRAY              (7, "CharAr"),
    CHAR_BOXED_ARRAY        (7, "CharBAr"),

    UNKNOWN                 (99),
    ;

    static final Map<String, NbtType> bySuffix = new HashMap<>();
    static final Int2ObjectMap<NbtType> byIndex = new Int2ObjectOpenHashMap<>();

    final int type;
    @Nullable final String suffix;

    NbtType(int type) {
        this(type, null);
    }

    NbtType(int type, @Nullable String suffix) {
        this.suffix = suffix;
        this.type = type;
    }

    static {
        for (NbtType nbtType : values()) {
            bySuffix.put(nbtType.suffix, nbtType);
            if (nbtType.suffix == null) {
                byIndex.put(nbtType.type, nbtType);
            }
        }
    }
}
