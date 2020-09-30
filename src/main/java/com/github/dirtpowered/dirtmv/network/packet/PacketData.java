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

package com.github.dirtpowered.dirtmv.network.packet;

import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_7.V1_7BProtocol;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.B1_8.V1_8BProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

import java.io.IOException;

public class PacketData {

    @Getter
    private int opCode;

    @Getter
    private TypeHolder[] objects;

    public PacketData(int opCode, TypeHolder... objects) {
        this.opCode = opCode;
        this.objects = objects;
    }

    public TypeHolder read(int index) {
        return objects[index];
    }

    public ByteBuf toMessage() throws IOException {
        ByteBuf buffer = Unpooled.buffer();

        for (TypeHolder typeHolder : objects) {
            switch (typeHolder.getType()) {
                case INT:
                    Protocol.INT.write(typeHolder, buffer);
                    break;
                case BYTE:
                    Protocol.BYTE.write(typeHolder, buffer);
                    break;
                case STRING:
                    Protocol.STRING.write(typeHolder, buffer);
                    break;
                case LONG:
                    Protocol.LONG.write(typeHolder, buffer);
                    break;
                case SHORT:
                    Protocol.SHORT.write(typeHolder, buffer);
                    break;
                case FLOAT:
                    Protocol.FLOAT.write(typeHolder, buffer);
                    break;
                case DOUBLE:
                    Protocol.DOUBLE.write(typeHolder, buffer);
                    break;
                case V1_7B_METADATA:
                    V1_7BProtocol.METADATA.write(typeHolder, buffer);
                    break;
                case V1_7B_ITEM:
                    V1_7BProtocol.ITEM.write(typeHolder, buffer);
                    break;
                case V1_7B_ITEM_ARRAY:
                    V1_7BProtocol.ITEM_ARRAY.write(typeHolder, buffer);
                    break;
                case V1_7B_CHUNK:
                    V1_7BProtocol.CHUNK.write(typeHolder, buffer);
                    break;
                case UTF8_STRING:
                    Protocol.UTF8_STRING.write(typeHolder, buffer);
                    break;
                case BYTE_BYTE_ARRAY:
                    Protocol.BYTE_BYTE_ARRAY.write(typeHolder, buffer);
                    break;
                case V1_8B_ITEM:
                    V1_8BProtocol.ITEM.write(typeHolder, buffer);
                    break;
                case POSITION_ARRAY:
                    V1_7BProtocol.POSITION_ARRAY.write(typeHolder, buffer);
                    break;
                case MOTION:
                    V1_7BProtocol.MOTION.write(typeHolder, buffer);
                    break;
            }
        }

        return buffer;
    }
}
