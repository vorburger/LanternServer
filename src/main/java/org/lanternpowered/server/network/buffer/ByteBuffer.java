/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.network.buffer;

import org.lanternpowered.server.network.buffer.objects.Type;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.network.ChannelBuf;

import java.nio.ByteOrder;
import java.util.UUID;

import javax.annotation.Nullable;

public interface ByteBuffer extends ChannelBuf {

    @Override
    ByteBuffer order(ByteOrder order);

    @Override
    ByteBuffer setReadIndex(int index);

    @Override
    ByteBuffer setWriteIndex(int index);

    @Override
    ByteBuffer setIndex(int readIndex, int writeIndex);

    @Override
    ByteBuffer clear();

    @Override
    ByteBuffer markRead();

    @Override
    ByteBuffer markWrite();

    @Override
    ByteBuffer resetRead();

    @Override
    ByteBuffer resetWrite();

    @Override
    ByteBuffer slice();

    @Override
    ByteBuffer slice(int index, int length);

    @Override
    ByteBuffer writeBoolean(boolean data);

    @Override
    ByteBuffer setBoolean(int index, boolean data);

    @Override
    ByteBuffer writeByte(byte data);

    @Override
    ByteBuffer setByte(int index, byte data);

    @Override
    ByteBuffer writeByteArray(byte[] data);

    @Override
    ByteBuffer writeByteArray(byte[] data, int start, int length);

    @Override
    ByteBuffer setByteArray(int index, byte[] data);

    @Override
    ByteBuffer setByteArray(int index, byte[] data, int start, int length);

    @Override
    ByteBuffer writeBytes(byte[] data);

    @Override
    ByteBuffer writeBytes(byte[] data, int start, int length);

    @Override
    ByteBuffer setBytes(int index, byte[] data);

    @Override
    ByteBuffer setBytes(int index, byte[] data, int start, int length);

    @Override
    ByteBuffer writeShort(short data);

    @Override
    ByteBuffer setShort(int index, short data);

    @Override
    ByteBuffer writeChar(char data);

    @Override
    ByteBuffer setChar(int index, char data);

    @Override
    ByteBuffer writeInteger(int data);

    @Override
    ByteBuffer setInteger(int index, int data);

    @Override
    ByteBuffer writeLong(long data);

    @Override
    ByteBuffer setLong(int index, long data);

    @Override
    ByteBuffer writeFloat(float data);

    @Override
    ByteBuffer setFloat(int index, float data);

    @Override
    ByteBuffer writeDouble(double data);

    @Override
    ByteBuffer setDouble(int index, double data);

    @Override
    ByteBuffer writeVarInt(int value);

    @Override
    ByteBuffer setVarInt(int index, int value);

    @Override
    ByteBuffer writeString(String data);

    @Override
    ByteBuffer setString(int index, String data);

    @Override
    ByteBuffer writeUTF(String data);

    @Override
    ByteBuffer setUTF(int index, String data);

    @Override
    ByteBuffer writeUniqueId(UUID data);

    @Override
    ByteBuffer setUniqueId(int index, UUID data);

    @Override
    ByteBuffer writeDataView(@Nullable DataView data);

    @Override
    ByteBuffer setDataView(int index, @Nullable DataView data);

    @Nullable
    @Override
    DataView readDataView();

    @Nullable
    @Override
    DataView getDataView(int index);

    ByteBuffer writeBytes(ByteBuffer byteBuffer);

    /**
     * Transfers this buffer's data to the specified byte array, starting
     * from the current readerIndex. Transferring until either this buffer
     * is at the end of the reader index or the specified array is filled.
     *
     * @param dst The destination byte array
     * @return This stream for chaining
     */
    ByteBuffer readBytes(byte[] dst);

    ByteBuffer readBytes(byte[] dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified byte buffer, starting
     * from the current readerIndex.
     *
     * @param byteBuffer The target byte buffer
     * @return This stream for chaining
     */
    ByteBuffer readBytes(ByteBuffer byteBuffer);

    ByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length);

    /**
     * Sets the specified varlong at the current writerIndex and increases the
     * writerIndex by the number of bytes written.
     *
     * <p>The number of bytes written depends on the size of the value.</p>
     *
     * @param value The varlong value
     * @return This stream for chaining
     */
    ByteBuffer writeVarLong(long value);

    /**
     * Sets the specified varlong at the specified absolute index in this buffer.
     * This method does not modify readerIndex or writerIndex of this buffer.
     *
     * <p>The number of bytes written depends on the size of the value.</p>
     *
     * @param index The index
     * @param value The varlong value
     * @return This stream for chaining
     */
    ByteBuffer setVarLong(int index, long value);

    /**
     * Gets a varlong at the current readerIndex and increases the readerIndex by
     * the number of bytes read.
     *
     * <p>The number of bytes read depends on the size of the value.</p>
     *
     * @return The varlong value
     */
    long readVarLong();

    /**
     * Gets a varlong at the specified absolute index in this buffer.
     *
     * <p>The number of bytes read depends on the size of the value.</p>
     *
     * @param index The index
     * @return The varlong value
     */
    long getVarLong(int index);

    /**
     * Writes the specified value for the {@link Type}.
     *
     * @param type The type
     * @param value The value
     * @param <V> The value type
     * @return This stream for chaining
     */
    <V> ByteBuffer write(Type<V> type, V value);

    <V> ByteBuffer set(int index, Type<V> type, V value);

    <V> V read(Type<V> type);

    <V> V get(int index, Type<V> type);

    /**
     * Makes sure the number of the writable bytes is equal to
     * or greater than the specified value.
     *
     * @param minWritableBytes The minimum writable bytes
     * @return This stream for chaining
     */
    ByteBuffer ensureWritable(int minWritableBytes);

    /**
     * Decreases the reference count by 1 and deallocates this object
     * if the reference count reaches at 0.
     *
     * @return If the reference count became 0 and this object has been deallocated
     */
    boolean release();

    ByteBuffer copy();
}