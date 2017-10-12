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

import org.lanternpowered.server.data.persistence.DataContainerOutput;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataSerializable;
import org.spongepowered.api.data.DataView;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A data output stream that serializes data views into the nbt format.
 */
@SuppressWarnings("unchecked")
public class NbtDataContainerOutputStream implements Closeable, Flushable, DataContainerOutput {

    private final DataOutputStream dos;

    /**
     * Creates a new nbt data view output stream.
     * 
     * @param dataOutputStream the data output stream
     */
    public NbtDataContainerOutputStream(DataOutputStream dataOutputStream) {
        this.dos = checkNotNull(dataOutputStream, "dataOutputStream");
    }

    /**
     * Creates a new nbt data view output stream.
     * 
     * @param outputStream the output stream
     */
    public NbtDataContainerOutputStream(OutputStream outputStream) {
        this(checkNotNull(outputStream, "outputStream") instanceof DataOutputStream ?
                (DataOutputStream) outputStream : new DataOutputStream(outputStream));
    }

    @Override
    public void close() throws IOException {
        this.dos.close();
    }

    @Override
    public void flush() throws IOException {
        this.dos.flush();
    }

    @Override
    public void write(DataView dataView) throws IOException {
        writeEntry("", checkNotNull(dataView, "dataView"));
    }

    @SuppressWarnings("unchecked")
    private void writePayload(NbtType nbtType, Object object) throws IOException {
        switch (nbtType) {
            case BYTE:
                this.dos.writeByte((Byte) object);
                break;
            case BYTE_ARRAY:
                final byte[] byteArray = (byte[]) object;
                this.dos.writeInt(byteArray.length);
                this.dos.write(byteArray);
                break;
            case BYTE_BOXED_ARRAY:
                final Byte[] boxedByteArray = (Byte[]) object;
                this.dos.writeInt(boxedByteArray.length);
                for (Byte boxedByteValue : boxedByteArray) {
                    this.dos.write(boxedByteValue);
                }
                break;
            case SHORT:
                this.dos.writeShort((Short) object);
                break;
            case SHORT_ARRAY:
                final short[] shortArray = (short[]) object;
                this.dos.writeInt(shortArray.length * 2);
                for (short shortValue : shortArray) {
                    this.dos.writeShort(shortValue);
                }
                break;
            case SHORT_BOXED_ARRAY:
                final Short[] boxedShortArray = (Short[]) object;
                this.dos.writeInt(boxedShortArray.length * 2);
                for (Short shortValue : boxedShortArray) {
                    this.dos.writeShort(shortValue);
                }
                break;
            case CHAR:
                this.dos.writeChar((Character) object);
                break;
            case CHAR_ARRAY:
                final char[] charArray = (char[]) object;
                this.dos.writeInt(charArray.length * 2);
                for (char charValue : charArray) {
                    this.dos.writeChar(charValue);
                }
                break;
            case CHAR_BOXED_ARRAY:
                final Character[] boxedCharacterArray = (Character[]) object;
                this.dos.writeInt(boxedCharacterArray.length * 2);
                for (Character charValue : boxedCharacterArray) {
                    this.dos.writeChar(charValue);
                }
                break;
            case INT:
                this.dos.writeInt((Integer) object);
                break;
            case INT_ARRAY:
                final int[] intArray = (int[]) object;
                this.dos.writeInt(intArray.length);
                for (int intValue : intArray) {
                    this.dos.writeInt(intValue);
                }
                break;
            case INT_BOXED_ARRAY:
                final Integer[] boxedIntArray = (Integer[]) object;
                this.dos.writeInt(boxedIntArray.length);
                for (Integer intValue : boxedIntArray) {
                    this.dos.writeInt(intValue);
                }
                break;
            case LONG:
                this.dos.writeLong((Long) object);
                break;
            case LONG_ARRAY:
                final long[] longArray = (long[]) object;
                this.dos.writeInt(longArray.length * 2);
                for (long longValue : longArray) {
                    this.dos.writeLong(longValue);
                }
                break;
            case LONG_BOXED_ARRAY:
                final Long[] boxedLongArray = (Long[]) object;
                this.dos.writeInt(boxedLongArray.length * 2);
                for (Long longValue : boxedLongArray) {
                    this.dos.writeLong(longValue);
                }
                break;
            case FLOAT:
                this.dos.writeFloat((Float) object);
                break;
            case FLOAT_ARRAY:
                final float[] floatArray = (float[]) object;
                this.dos.writeInt(floatArray.length);
                for (float floatValue : floatArray) {
                    this.dos.writeFloat(floatValue);
                }
                break;
            case FLOAT_BOXED_ARRAY:
                final Float[] boxedFloatArray = (Float[]) object;
                this.dos.writeInt(boxedFloatArray.length);
                for (Float floatValue : boxedFloatArray) {
                    this.dos.writeFloat(floatValue);
                }
                break;
            case DOUBLE:
                this.dos.writeDouble((Double) object);
                break;
            case DOUBLE_ARRAY:
                final double[] doubleArray = (double[]) object;
                this.dos.writeInt(doubleArray.length * 2);
                for (double doubleValue : doubleArray) {
                    this.dos.writeDouble(doubleValue);
                }
                break;
            case DOUBLE_BOXED_ARRAY:
                final Double[] boxedDoubleArray = (Double[]) object;
                this.dos.writeInt(boxedDoubleArray.length * 2);
                for (Double doubleValue : boxedDoubleArray) {
                    this.dos.writeDouble(doubleValue);
                }
                break;
            case STRING:
                this.dos.writeUTF((String) object);
                break;
            case STRING_ARRAY:
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                final DataOutputStream dos = new DataOutputStream(baos);
                final String[] stringArray = (String[]) object;
                dos.writeInt(stringArray.length);
                for (String string : stringArray) {
                    dos.writeUTF(string);
                }
                final byte[] bytes = baos.toByteArray();
                this.dos.writeInt(bytes.length);
                this.dos.write(bytes);
                break;
            case BOOLEAN:
                this.dos.writeBoolean((Boolean) object);
                break;
            case BOOLEAN_ARRAY:
                final boolean[] booleanArray = (boolean[]) object;
                int length = booleanArray.length / 8;
                if (booleanArray.length % 8 != 0) {
                    length++;
                }
                this.dos.writeInt(length + 4);
                this.dos.writeShort(booleanArray.length);
                int j = 0;
                for (int i = 0; i < length; i++) {
                    byte value = 0;
                    while (j < booleanArray.length) {
                        final int k = j % 8;
                        if (booleanArray[j++]) {
                            value |= 1 << k;
                        }
                    }
                    this.dos.writeByte(value);
                }
                break;
            case BOOLEAN_BOXED_ARRAY:
                final Boolean[] boxedBooleanArray = (Boolean[]) object;
                length = boxedBooleanArray.length / 8;
                if (boxedBooleanArray.length % 8 != 0) {
                    length++;
                }
                this.dos.writeInt(length + 4);
                this.dos.writeShort(boxedBooleanArray.length);
                j = 0;
                for (int i = 0; i < length; i++) {
                    byte value = 0;
                    while (j < boxedBooleanArray.length) {
                        final int k = j % 8;
                        if (boxedBooleanArray[j++]) {
                            value |= 1 << k;
                        }
                    }
                    this.dos.writeByte(value);
                }
                break;
            case LIST:
                writeList(nbtType, (List<Object>) object);
                break;
            case COMPOUND:
                // Convert the object in something we can serialize
                if (object instanceof DataView) {
                    object = ((DataView) object).getValues(false);
                } else if (object instanceof DataSerializable) {
                    object = ((DataSerializable) object).toContainer().getValues(false);
                }
                for (Entry<DataQuery, Object> entry : ((Map<DataQuery, Object>) object).entrySet()) {
                    writeEntry(entry.getKey().asString('.'), entry.getValue());
                }
                this.dos.writeByte(NbtType.END.type);
                break;
            default:
                throw new IOException("Attempted to serialize a unsupported object type: " + object.getClass().getName());
        }
    }

    private void writeList(NbtType nbtType, List<Object> list) throws IOException {
        this.dos.writeByte(nbtType.type);
        this.dos.writeInt(list.size());
        for (Object object0 : list) {
            writePayload(nbtType, object0);
        }
    }

    private void writeEntry(String key, Object object) throws IOException {
        NbtType nbtType = typeFor(object);
        this.dos.writeByte(nbtType.type);
        if (nbtType == NbtType.LIST) {
            final List<Object> list = (List<Object>) object;
            if (list.isEmpty()) {
                nbtType = NbtType.END;
            } else {
                nbtType = typeFor(list.get(0));
                if (nbtType.suffix != null) {
                    key += '$' + nbtType.suffix;
                }
            }
            this.dos.writeUTF(key);
            writeList(nbtType, list);
        } else {
            if (nbtType.suffix != null) {
                key += '$' + nbtType.suffix;
            }
            this.dos.writeUTF(key);
            try {
                writePayload(nbtType, object);
            } catch (Exception e) {
                throw new IOException("Exception while serializing key: " + key, e);
            }
        }
    }

    private NbtType typeFor(Object object) {
        if (object instanceof Boolean) {
            return NbtType.BOOLEAN;
        } else if (object instanceof Boolean[]) {
            return NbtType.BOOLEAN_BOXED_ARRAY;
        } else if (object instanceof boolean[]) {
            return NbtType.BOOLEAN_ARRAY;
        } else if (object instanceof Byte) {
            return NbtType.BYTE;
        } else if (object instanceof Byte[]) {
            return NbtType.BYTE_BOXED_ARRAY;
        } else if (object instanceof byte[]) {
            return NbtType.BYTE_ARRAY;
        } else if (object instanceof Map || object instanceof DataView) {
            return NbtType.COMPOUND;
        } else if (object instanceof Double) {
            return NbtType.DOUBLE;
        } else if (object instanceof Double[]) {
            return NbtType.DOUBLE_BOXED_ARRAY;
        } else if (object instanceof double[]) {
            return NbtType.DOUBLE_ARRAY;
        } else if (object instanceof Float) {
            return NbtType.FLOAT;
        } else if (object instanceof Float[]) {
            return NbtType.FLOAT_BOXED_ARRAY;
        } else if (object instanceof float[]) {
            return NbtType.FLOAT_ARRAY;
        } else if (object instanceof Integer) {
            return NbtType.INT;
        } else if (object instanceof Integer[]) {
            return NbtType.INT_BOXED_ARRAY;
        } else if (object instanceof int[]) {
            return NbtType.INT_ARRAY;
        } else if (object instanceof List) {
            return NbtType.LIST;
        } else if (object instanceof Long) {
            return NbtType.LONG;
        } else if (object instanceof Long[]) {
            return NbtType.LONG_BOXED_ARRAY;
        } else if (object instanceof long[]) {
            return NbtType.LONG_ARRAY;
        } else if (object instanceof Short) {
            return NbtType.SHORT;
        } else if (object instanceof Short[]) {
            return NbtType.SHORT_BOXED_ARRAY;
        } else if (object instanceof short[]) {
            return NbtType.SHORT_ARRAY;
        } else if (object instanceof String) {
            return NbtType.STRING;
        } else if (object instanceof String[]) {
            return NbtType.STRING_ARRAY;
        } else if (object instanceof Character) {
            return NbtType.CHAR;
        } else if (object instanceof Character[]) {
            return NbtType.CHAR_BOXED_ARRAY;
        } else if (object instanceof char[]) {
            return NbtType.CHAR_ARRAY;
        }
        return NbtType.UNKNOWN;
    }

}
