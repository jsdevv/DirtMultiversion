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

package com.github.dirtpowered.dirtmv.network.versions.Release29To28;

import com.github.dirtpowered.dirtmv.data.MinecraftVersion;
import com.github.dirtpowered.dirtmv.network.data.model.PacketDirection;
import com.github.dirtpowered.dirtmv.network.data.model.PacketTranslator;
import com.github.dirtpowered.dirtmv.network.data.model.ServerProtocol;
import com.github.dirtpowered.dirtmv.network.packet.PacketData;
import com.github.dirtpowered.dirtmv.network.packet.PacketUtil;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.server.ServerSession;

public class ProtocolRelease29To28 extends ServerProtocol {

    public ProtocolRelease29To28() {
        super(MinecraftVersion.R1_2_4, MinecraftVersion.R1_2_1);
    }

    @Override
    public void registerTranslators() {
        addTranslator(0x01, new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                if (dir == PacketDirection.CLIENT_TO_SERVER) {

                    return PacketUtil.createPacket(0x01, new TypeHolder[]{
                            set(Type.INT, 28), // protocol version
                            data.read(1),
                            data.read(2),
                            data.read(3),
                            data.read(4),
                            data.read(5),
                            data.read(6),
                            data.read(7),
                    });
                } else {

                    return data;
                }
            }
        });

        addTranslator(0xCA, /* PLAYER ABILITIES */ new PacketTranslator() {

            @Override
            public PacketData translate(ServerSession session, PacketDirection dir, PacketData data) {

                // cancel packet
                return new PacketData(-1);
            }
        });
    }
}
