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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import org.lanternpowered.server.data.persistence.DataContainerInput;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A data input stream that deserializes data views from the nbt format.
 */
public class NbtDataContainerInputStream implements Closeable, DataContainerInput {

    private final DataInputStream dis;
    private final int maximumDepth;

    /**
     * Creates a new nbt data view input stream.
     *
     * @param dataInputStream the data input stream
     */
    public NbtDataContainerInputStream(DataInputStream dataInputStream) {
        this(dataInputStream, Integer.MAX_VALUE);
    }

    /**
     * Creates a new nbt data view input stream.
     * 
     * @param dataInputStream the data input stream
     * @param maximumDepth the maximum depth of the data contains
     */
    public NbtDataContainerInputStream(DataInputStream dataInputStream, int maximumDepth) {
        this.dis = checkNotNull(dataInputStream, "dataInputStream");
        this.maximumDepth = maximumDepth;
    }

    /**
     * Creates a new nbt data view input stream.
     * 
     * @param inputStream the data input stream
     */
    public NbtDataContainerInputStream(InputStream inputStream) {
        this(inputStream, Integer.MAX_VALUE);
    }

    /**
     * Creates a new nbt data view input stream.
     *
     * @param inputStream the data input stream
     * @param maximumDepth the maximum depth of the data contains
     */
    public NbtDataContainerInputStream(InputStream inputStream, int maximumDepth) {
        this(checkNotNull(inputStream, "inputStream") instanceof DataInputStream ?
                (DataInputStream) inputStream : new DataInputStream(inputStream), maximumDepth);
    }

    @Override
    public void close() throws IOException, InvalidDataFormatException {
        this.dis.close();
    }

    @Override
    public DataContainer read() throws IOException, InvalidDataFormatException {
        Entry entry = readEntry();
        if (entry == null) {
            throw new IOException("There is no more data to read.");
        }
        return (DataContainer) this.readObject(null, entry, 0);
    }

    private Object readObject(@Nullable DataView container, Entry entry, int depth) throws IOException, InvalidDataFormatException {
        return this.readPayload(container, entry.type, entry.listType, depth);
    }

    @Nullable
    private Entry readEntry() throws IOException {
        final byte type = this.dis.readByte();
        if (type == NbtType.END.type) {
            return null;
        }
        String name = this.dis.readUTF();
        final int index = name.lastIndexOf('$');
        NbtType nbtType = NbtType.byIndex.get(type);
        if (nbtType == null) {
            throw new IOException("Unknown NBT Type with id: " + type);
        }
        NbtType listNbtType = null;
        if (index != -1) {
            final String suffix = name.substring(index + 1);
            name = name.substring(0, index);
            final NbtType nbtType1 = NbtType.bySuffix.get(suffix);
            if (nbtType1 != null) {
                if (nbtType == NbtType.LIST) {
                    listNbtType = nbtType1;
                } else {
                    nbtType = nbtType1;
                }
            }
        }
        return new Entry(name, nbtType, listNbtType);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object readPayload(@Nullable DataView container, NbtType nbtType, @Nullable NbtType listNbtType,
            int depth) throws IOException, InvalidDataFormatException {
        if (depth > this.maximumDepth) {
            throw new IOException("Attempted to read a data container with too high complexity,"
                    + " exceeded the maximum depth of " + this.maximumDepth);
        }
        switch (nbtType) {
            case BYTE:
                return this.dis.readByte();
            case BYTE_ARRAY:
                final byte[] byteArray = new byte[this.dis.readInt()];
                this.dis.read(byteArray);
                return byteArray;
            case BYTE_BOXED_ARRAY:
                final Byte[] boxedByteArray = new Byte[this.dis.readInt()];
                for (int i = 0; i < boxedByteArray.length; i++) {
                    boxedByteArray[i] = this.dis.readByte();
                }
                return boxedByteArray;
            case SHORT:
                return this.dis.readShort();
            case SHORT_ARRAY:
                final short[] shortArray = new short[this.dis.readInt() / 2];
                for (int i = 0; i < shortArray.length; i++) {
                    shortArray[i] = this.dis.readShort();
                }
                return shortArray;
            case SHORT_BOXED_ARRAY:
                final Short[] boxedShortArray = new Short[this.dis.readInt() / 2];
                for (int i = 0; i < boxedShortArray.length; i++) {
                    boxedShortArray[i] = this.dis.readShort();
                }
                return boxedShortArray;
            case INT:
                return this.dis.readInt();
            case INT_ARRAY:
                final int[] intArray = new int[this.dis.readInt()];
                for (int i = 0; i < intArray.length; i++) {
                    intArray[i] = this.dis.readInt();
                }
                return intArray;
            case INT_BOXED_ARRAY:
                final Integer[] boxedIntArray = new Integer[this.dis.readInt()];
                for (int i = 0; i < boxedIntArray.length; i++) {
                    boxedIntArray[i] = this.dis.readInt();
                }
                return boxedIntArray;
            case LONG:
                return this.dis.readLong();
            case LONG_ARRAY:
                final long[] longArray = new long[this.dis.readInt() / 2];
                for (int i = 0; i < longArray.length; i++) {
                    longArray[i] = this.dis.readLong();
                }
                return longArray;
            case LONG_BOXED_ARRAY:
                final Long[] boxedLongArray = new Long[this.dis.readInt() / 2];
                for (int i = 0; i < boxedLongArray.length; i++) {
                    boxedLongArray[i] = this.dis.readLong();
                }
                return boxedLongArray;
            case FLOAT:
                return this.dis.readFloat();
            case FLOAT_ARRAY:
                final float[] floatArray = new float[this.dis.readInt()];
                for (int i = 0; i < floatArray.length; i++) {
                    floatArray[i] = this.dis.readFloat();
                }
                return floatArray;
            case FLOAT_BOXED_ARRAY:
                final Float[] boxedFloatArray = new Float[this.dis.readInt()];
                for (int i = 0; i < boxedFloatArray.length; i++) {
                    boxedFloatArray[i] = this.dis.readFloat();
                }
                return boxedFloatArray;
            case DOUBLE:
                return this.dis.readDouble();
            case DOUBLE_ARRAY:
                final double[] doubleArray = new double[this.dis.readInt() / 2];
                for (int i = 0; i < doubleArray.length; i++) {
                    doubleArray[i] = this.dis.readDouble();
                }
                return doubleArray;
            case DOUBLE_BOXED_ARRAY:
                final Double[] boxedDoubleArray = new Double[this.dis.readInt() / 2];
                for (int i = 0; i < boxedDoubleArray.length; i++) {
                    boxedDoubleArray[i] = this.dis.readDouble();
                }
                return boxedDoubleArray;
            case STRING:
                return this.dis.readUTF();
            case STRING_ARRAY:
                final byte[] bytes = new byte[this.dis.readInt()];
                this.dis.read(bytes);
                final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                final DataInputStream dis = new DataInputStream(bais);
                final String[] stringArray = new String[dis.readInt()];
                for (int i = 0; i < stringArray.length; i++) {
                    stringArray[i] = dis.readUTF();
                }
                return stringArray;
            case BOOLEAN:
                return this.dis.readByte() != 0;
            case BOOLEAN_ARRAY:
                int bitBytes = this.dis.readInt() - 4;
                final boolean[] booleanArray = new boolean[this.dis.readShort()];
                int j = 0;
                for (int i = 0; i < bitBytes; i++) {
                    final byte value = this.dis.readByte();
                    while (j < booleanArray.length) {
                        final int k = j++ % 8;
                        booleanArray[j] = (value & (1 << k)) != 0;
                    }
                }
                return booleanArray;
            case BOOLEAN_BOXED_ARRAY:
                bitBytes = this.dis.readInt() - 4;
                final Boolean[] boxedBooleanArray = new Boolean[this.dis.readShort()];
                j = 0;
                for (int i = 0; i < bitBytes; i++) {
                    final byte value = this.dis.readByte();
                    while (j < boxedBooleanArray.length) {
                        final int k = j++ % 8;
                        boxedBooleanArray[j] = (value & (1 << k)) != 0;
                    }
                }
                return boxedBooleanArray;
            case LIST:
                final byte listType = this.dis.readByte();
                if (listNbtType == null) {
                    listNbtType = NbtType.byIndex.get(listType);
                    if (listNbtType == null) {
                        throw new IOException("Unknown NBT Type with id: " + listType);
                    }
                }
                final int size = this.dis.readInt();
                final List<Object> list = Lists.newArrayListWithExpectedSize(size);
                if (size == 0 || listNbtType == NbtType.END) {
                    return list;
                }
                int depth1 = depth + 1;
                for (int i = 0; i < size; i++) {
                    list.add(readPayload(null, listNbtType, null, depth1));
                }
                return list;
            case COMPOUND:
                if (container == null) {
                    container = DataContainer.createNew(DataView.SafetyMode.NO_DATA_CLONED);
                }
                depth1 = depth + 1;
                Entry entry;
                while ((entry = readEntry()) != null) {
                    if (entry.type == NbtType.COMPOUND) {
                        readObject(container.createView(DataQuery.of(entry.name)), entry, depth1);
                    } else {
                        container.set(DataQuery.of(entry.name), readObject(null, entry, depth1));
                    }
                }
                return container;
            default:
                throw new InvalidDataFormatException("Attempt to deserialize a unknown nbt tag type: " + nbtType);
        }
    }

    private static class Entry {

        private final String name;
        private final NbtType type;
        @Nullable private final NbtType listType;

        public Entry(String name, NbtType type, @Nullable NbtType listType) {
            this.listType = listType;
            this.name = name;
            this.type = type;
        }
    }
}
