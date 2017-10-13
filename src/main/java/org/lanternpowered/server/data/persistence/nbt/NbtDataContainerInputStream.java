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
import org.lanternpowered.server.data.MemoryDataContainer;
import org.lanternpowered.server.data.persistence.DataContainerInput;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.persistence.InvalidDataFormatException;

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
        return (DataContainer) readObject(null, entry, 0);
    }

    private Object readObject(@Nullable DataView container, Entry entry, int depth)
            throws IOException, InvalidDataFormatException {
        return readPayload(container, entry.type, entry.listType, depth);
    }

    @Nullable
    private Entry readEntry() throws IOException {
        final byte type = this.dis.readByte();
        if (type == NbtType.END.type) {
            return null;
        }
        String name = this.dis.readUTF();
        int index = name.lastIndexOf('$');
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
                    index = name.lastIndexOf('$');
                    if (index != -1) {
                        final String li = name.substring(index + 1);
                        if (li.equals("List")) {
                            name = name.substring(0, index);
                            listNbtType = nbtType1;
                        }
                    }
                }
                if (listNbtType == null) {
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
                byte type = this.dis.readByte();
                int length = this.dis.readInt();
                if (type == NbtType.END.type) {
                    return new short[length];
                } else if (type != NbtType.SHORT.type) {
                    throw new IOException("Attempted to deserialize a Short Array (List) but the list type wasn't a short.");
                }
                final short[] shortArray = new short[length];
                for (int i = 0; i < shortArray.length; i++) {
                    shortArray[i] = this.dis.readShort();
                }
                return shortArray;
            case SHORT_BOXED_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type) {
                    return new Short[length];
                } else if (type != NbtType.SHORT.type) {
                    throw new IOException("Attempted to deserialize a Short Array (List) but the list type wasn't a short.");
                }
                final Short[] boxedShortArray = new Short[length];
                for (int i = 0; i < boxedShortArray.length; i++) {
                    boxedShortArray[i] = this.dis.readShort();
                }
                return boxedShortArray;
            case CHAR:
                return this.dis.readUTF().charAt(0);
            case CHAR_ARRAY:
                return this.dis.readUTF().toCharArray();
            case CHAR_BOXED_ARRAY:
                return this.dis.readUTF().chars().mapToObj(c -> (char) c).toArray(Character[]::new);
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
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type) {
                    return new long[length];
                } else if (type != NbtType.LONG.type) {
                    throw new IOException("Attempted to deserialize a Long Array (List) but the list type wasn't a long.");
                }
                final long[] longArray = new long[length];
                for (int i = 0; i < longArray.length; i++) {
                    longArray[i] = this.dis.readLong();
                }
                return longArray;
            case LONG_BOXED_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type) {
                    return new Long[length];
                } else if (type != NbtType.LONG.type) {
                    throw new IOException("Attempted to deserialize a Long Array (List) but the list type wasn't a long.");
                }
                final Long[] boxedLongArray = new Long[length];
                for (int i = 0; i < boxedLongArray.length; i++) {
                    boxedLongArray[i] = this.dis.readLong();
                }
                return boxedLongArray;
            case FLOAT:
                return this.dis.readFloat();
            case FLOAT_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type) {
                    return new float[length];
                } else if (type != NbtType.FLOAT.type) {
                    throw new IOException("Attempted to deserialize a Float Array (List) but the list type wasn't a float.");
                }
                final float[] floatArray = new float[length];
                for (int i = 0; i < floatArray.length; i++) {
                    floatArray[i] = this.dis.readFloat();
                }
                return floatArray;
            case FLOAT_BOXED_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type) {
                    return new Float[length];
                } else if (type != NbtType.FLOAT.type) {
                    throw new IOException("Attempted to deserialize a Float Array (List) but the list type wasn't a float.");
                }
                final Float[] boxedFloatArray = new Float[length];
                for (int i = 0; i < boxedFloatArray.length; i++) {
                    boxedFloatArray[i] = this.dis.readFloat();
                }
                return boxedFloatArray;
            case DOUBLE:
                return this.dis.readDouble();
            case DOUBLE_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type) {
                    return new double[length];
                } else if (type != NbtType.DOUBLE.type) {
                    throw new IOException("Attempted to deserialize a Double Array (List) but the list type wasn't a double.");
                }
                final double[] doubleArray = new double[length];
                for (int i = 0; i < doubleArray.length; i++) {
                    doubleArray[i] = this.dis.readDouble();
                }
                return doubleArray;
            case DOUBLE_BOXED_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type) {
                    return new Double[length];
                } else if (type != NbtType.DOUBLE.type) {
                    throw new IOException("Attempted to deserialize a Double Array (List) but the list type wasn't a double.");
                }
                final Double[] boxedDoubleArray = new Double[length];
                for (int i = 0; i < boxedDoubleArray.length; i++) {
                    boxedDoubleArray[i] = this.dis.readDouble();
                }
                return boxedDoubleArray;
            case STRING:
                return this.dis.readUTF();
            case STRING_ARRAY:
                type = this.dis.readByte();
                length = this.dis.readInt();
                if (type == NbtType.END.type) {
                    return new String[length];
                } else if (type != NbtType.STRING.type) {
                    throw new IOException("Attempted to deserialize a String Array (List) but the list type wasn't a string.");
                }
                final String[] stringArray = new String[length];
                for (int i = 0; i < stringArray.length; i++) {
                    stringArray[i] = this.dis.readUTF();
                }
                return stringArray;
            case BOOLEAN:
                return this.dis.readBoolean();
            case BOOLEAN_ARRAY:
                int bitBytes = this.dis.readInt() - 4;
                final boolean[] booleanArray = new boolean[this.dis.readShort()];
                int j = 0;
                for (int i = 0; i < bitBytes; i++) {
                    final byte value = this.dis.readByte();
                    while (j < booleanArray.length) {
                        final int k = j % 8;
                        booleanArray[j++] = (value & (1 << k)) != 0;
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
                        final int k = j % 8;
                        boxedBooleanArray[j++] = (value & (1 << k)) != 0;
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
                    container = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
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
