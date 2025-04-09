package io.github.MoYuSOwO.farmersDelightRepaper.block;

import io.github.MoYuSOwO.farmersDelightRepaper.util.ReflectionUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundSectionBlocksUpdatePacket;
import net.minecraft.util.BitStorage;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.block.state.BlockState;

public class BlockPacketHandler extends ChannelDuplexHandler {

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof ClientboundBlockUpdatePacket packet) {
            BlockState state = (BlockState) ReflectionUtil.getField(packet, "blockState");
            BlockState toState = BlockMapping.getMappedState(state);
            if (toState != null) {
                ReflectionUtil.setField(packet, "blockState", toState);
            }
        } else if (msg instanceof ClientboundLevelChunkWithLightPacket packet) {
            ClientboundLevelChunkPacketData chunkData = packet.getChunkData();
            byte[] buffer = (byte[]) ReflectionUtil.getField(chunkData, "buffer");
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(buffer));
            FriendlyByteBuf newBuf = new FriendlyByteBuf(Unpooled.buffer());
            while (buf.readerIndex() < buffer.length) {
                int nonEmptyBlock = buf.readShort();
                newBuf.writeShort(nonEmptyBlock);
                int bitsPerBlock = buf.readByte();
                newBuf.writeByte(bitsPerBlock);
                if (bitsPerBlock == 0) {
                    int stateId = buf.readVarInt();
                    Integer toStateId = BlockMapping.getMappedStateId(stateId);
                    if (toStateId != null) {
                        newBuf.writeVarInt(toStateId);
                    } else {
                        newBuf.writeVarInt(stateId);
                    }
                    long[] data = buf.readLongArray();
                    newBuf.writeLongArray(data);
                } else if (bitsPerBlock <= 8) {
                    int sizeOfPalette = buf.readVarInt();
                    newBuf.writeVarInt(sizeOfPalette);
                    int[] palette = new int[sizeOfPalette];
                    for (int i = 0; i < sizeOfPalette; i++) {
                        palette[i] = buf.readVarInt();
                        Integer toStateId = BlockMapping.getMappedStateId(palette[i]);
                        if (toStateId != null) {
                            newBuf.writeVarInt(toStateId);
                        } else {
                            newBuf.writeVarInt(palette[i]);
                        }
                    }
                    long[] data = buf.readLongArray();
                    newBuf.writeLongArray(data);
                } else {
                    long[] data = buf.readLongArray();
                    BitStorage storage = new SimpleBitStorage(bitsPerBlock, 4096, data);
                    for (int pos = 0; pos < 4096; pos++) {
                        int stateId = storage.get(pos);
                        Integer toStateId = BlockMapping.getMappedStateId(stateId);
                        if (toStateId != null) {
                            storage.set(pos, toStateId);
                        }
                    }
                    newBuf.writeLongArray(storage.getRaw());
                }
                int bitPerBiome = buf.readByte();
                newBuf.writeByte(bitPerBiome);
                if (bitPerBiome == 0) {
                    int sizeOfPalette = buf.readVarInt();
                    newBuf.writeVarInt(sizeOfPalette);
                    long[] data = buf.readLongArray();
                    newBuf.writeLongArray(data);
                } else if (bitPerBiome <= 3) {
                    int sizeOfPalette = buf.readVarInt();
                    newBuf.writeVarInt(sizeOfPalette);
                    int[] palette = new int[sizeOfPalette];
                    for (int i = 0; i < sizeOfPalette; i++) {
                        palette[i] = buf.readVarInt();
                        newBuf.writeVarInt(palette[i]);
                    }
                    long[] data = buf.readLongArray();
                    newBuf.writeLongArray(data);
                } else {
                    long[] data = buf.readLongArray();
                    newBuf.writeLongArray(data);
                }
            }
            ReflectionUtil.setField(chunkData, "buffer", newBuf.array());
        } else if (msg instanceof ClientboundSectionBlocksUpdatePacket packet) {
            BlockState[] states = (BlockState[]) ReflectionUtil.getField(packet, "states");
            for (int i = 0; i < states.length; i++) {
                BlockState toState = BlockMapping.getMappedState(states[i]);
                if (toState != null) {
                    states[i] = toState;
                }
            }
            ReflectionUtil.setField(packet, "states", states);
        }
        super.write(ctx, msg, promise);
    }
}
