package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkPacketData;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.generator.ChunkGenerator;

import java.sql.Ref;
import java.util.Arrays;

public class NMSProtocolHandler extends ChannelDuplexHandler {

    private final ServerPlayer player;

    public NMSProtocolHandler(ServerPlayer player) {
        this.player = player;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        Object modifiedMsg = handlePacket(msg);
        super.write(ctx, modifiedMsg, promise);
    }

    private Object handlePacket(Object p) throws Exception {
        if (p instanceof ClientboundBlockUpdatePacket packet) {
            BlockState state = (BlockState) ReflectionUtil.getField(packet, "blockState");
            if (state.is(Blocks.GRASS_BLOCK)) {
                ReflectionUtil.setField(packet, "blockState", Blocks.DIAMOND_BLOCK.defaultBlockState());
            }
        }
        else if (p instanceof ClientboundLevelChunkWithLightPacket packet) {
            ClientboundLevelChunkPacketData chunkData = packet.getChunkData();
            byte[] buffer = (byte[]) ReflectionUtil.getField(chunkData, "buffer");
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(buffer));
            int nonEmptyBlock = buf.readShort();
            int bitPerBlock = buf.readByte();
            if (bitPerBlock <= 8) {
                System.out.println("[Chunk Info]");
                System.out.println("nonEmptyBlock: " + nonEmptyBlock);
                int sizeOfPalette = buf.readVarInt();
                int[] palette = new int[sizeOfPalette];
                for (int i = 0; i < sizeOfPalette; i++) {
                    palette[i] = buf.readVarInt();
                }
                long[] data = buf.readLongArray();
                System.out.println("[Biome Info]");
                for (int i = 0; i < 100; i++) {
                    System.out.print((int) buf.readByte() + " ");
                }
                System.out.println();
            } else {
                System.out.println("[Chunk Info]");
                System.out.println("nonEmptyBlock: " + nonEmptyBlock);
            }
        }
        return p;
    }

}
