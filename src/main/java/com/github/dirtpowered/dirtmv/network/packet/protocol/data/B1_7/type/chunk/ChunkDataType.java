/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_7.type.chunk;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.V1_7Chunk;
import io.netty.buffer.ByteBuf;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ChunkDataType extends DataType<V1_7Chunk> {

    public ChunkDataType() {
        super(Type.V1_7B_CHUNK);
    }

    @Override
    public V1_7Chunk read(ByteBuf buffer) {
        int x = buffer.readInt();
        int y = buffer.readShort();
        int z = buffer.readInt();

        int xSize = buffer.readByte() + 1;
        int ySize = buffer.readByte() + 1;
        int zSize = buffer.readByte() + 1;

        byte[] chunk;

        int chunkSize = buffer.readInt();
        byte[] buf = new byte[chunkSize];

        chunk = new byte[xSize * ySize * zSize * 5 / 2];
        buffer.readBytes(buf);

        Inflater inflater = new Inflater();
        inflater.setInput(buf);

        try {
            inflater.inflate(chunk);
        } catch (DataFormatException ignored) {
        } finally {
            inflater.end();
        }

        return new V1_7Chunk(x, y, z, xSize, ySize, zSize, chunk);
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) {
        V1_7Chunk data1 = (V1_7Chunk) typeHolder.getObject();

        buffer.writeInt(data1.getX());
        buffer.writeShort(data1.getY());
        buffer.writeInt(data1.getZ());

        buffer.writeByte(data1.getXSize() - 1);
        buffer.writeByte(data1.getYSize() - 1);
        buffer.writeByte(data1.getZSize() - 1);

        byte[] data = data1.getChunk();
        Deflater deflater = new Deflater(Deflater.BEST_SPEED);

        try {
            deflater.setInput(data);
            deflater.finish();
            byte[] chunk = new byte[(data1.getXSize() + 1) * (data1.getZSize() + 1) * (data1.getYSize() + 1) * 5 / 2];
            int chunkSize = deflater.deflate(chunk);

            buffer.writeInt(chunkSize);
            buffer.writeBytes(chunk, 0, chunkSize);
        } finally {
            deflater.end();
        }
    }
}
