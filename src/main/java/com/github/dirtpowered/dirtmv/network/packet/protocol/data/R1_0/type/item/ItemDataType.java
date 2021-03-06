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

package com.github.dirtpowered.dirtmv.network.packet.protocol.data.R1_0.type.item;

import com.github.dirtpowered.dirtmv.network.packet.DataType;
import com.github.dirtpowered.dirtmv.network.packet.Type;
import com.github.dirtpowered.dirtmv.network.packet.TypeHolder;
import com.github.dirtpowered.dirtmv.network.packet.protocol.data.objects.ItemStack;
import com.github.dirtpowered.dirtmv.utils.item.LegacyItemList;
import com.github.dirtpowered.dirtmv.utils.nbt.NBTUtils;
import com.mojang.nbt.CompoundTag;
import io.netty.buffer.ByteBuf;

public class ItemDataType extends DataType<ItemStack> {

    public ItemDataType() {
        super(Type.V1_0R_ITEM);
    }

    @Override
    public ItemStack read(ByteBuf buffer) {
        int itemId = buffer.readShort();

        if (itemId >= 0) {
            int amount = buffer.readByte();
            int data = buffer.readShort();

            CompoundTag compoundTag = null;

            if (LegacyItemList.isEnchantable(itemId))
                compoundTag = NBTUtils.readNBT(buffer);

            return new ItemStack(itemId, amount, data, compoundTag);
        }

        return null;
    }

    @Override
    public void write(TypeHolder typeHolder, ByteBuf buffer) {
        ItemStack itemStack = (ItemStack) typeHolder.getObject();

        if (itemStack == null) {
            buffer.writeShort(-1);
        } else {
            buffer.writeShort(itemStack.getItemId());
            buffer.writeByte(itemStack.getAmount());
            buffer.writeShort(itemStack.getData());

            if (LegacyItemList.isEnchantable(itemStack.getItemId())) {
                NBTUtils.writeNBT(itemStack.getCompoundTag(), buffer);
            }
        }
    }
}
