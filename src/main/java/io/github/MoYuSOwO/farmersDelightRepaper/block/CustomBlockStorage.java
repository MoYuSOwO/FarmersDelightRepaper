package io.github.MoYuSOwO.farmersDelightRepaper.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class CustomBlockStorage {

    private static final Map<Level, Map<ChunkPos, Map<BlockPos, CustomBlocks>>> blockRecords = new HashMap<>();
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private CustomBlockStorage() {}

    public static void loadFromDatabase() {
        CustomBlockDataBase.loadAllBlocks(blockRecords);
    }

    public static void replace(Level level, BlockPos blockPos, CustomBlocks block) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        lock.writeLock().lock();
        try {
            blockRecords.get(level).get(chunkPos).replace(blockPos, block);
            CustomBlockDataBase.saveBlock(level.getWorld(), blockPos, block);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void place(Level level, BlockPos blockPos, CustomBlocks block) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        lock.writeLock().lock();
        try {
            blockRecords.computeIfAbsent(level, k -> new HashMap<>()).computeIfAbsent(chunkPos, k -> new HashMap<>()).put(blockPos, block);
            CustomBlockDataBase.saveBlock(level.getWorld(), blockPos, block);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void remove(Level level, BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        lock.writeLock().lock();
        try {
            blockRecords.get(level).get(chunkPos).remove(blockPos);
            CustomBlockDataBase.removeBlock(level.getWorld(), blockPos);
            if (blockRecords.get(level).get(chunkPos).isEmpty()) {
                blockRecords.get(level).remove(chunkPos);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void remove(Level level, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        remove(level, blockPos);
    }

    public static void remove(Block block) {
        CraftWorld world = (CraftWorld) block.getWorld();
        remove(world.getHandle(), block.getX(), block.getY(), block.getZ());
    }

    public static @NotNull CustomBlocks get(Level level, BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        lock.readLock().lock();
        try {
            CustomBlocks customBlocks = blockRecords.get(level).get(chunkPos).get(blockPos);
            if (customBlocks == null) throw new IllegalArgumentException("Please use is method to check first!");
            else return customBlocks;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static CustomBlocks get(Level level, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        return get(level, blockPos);
    }

    public static CustomBlocks get(Block block) {
        CraftWorld world = (CraftWorld) block.getWorld();
        return get(world.getHandle(), block.getX(), block.getY(), block.getZ());
    }

    public static Map<BlockPos, CustomBlocks> get(Level level, ChunkPos chunkPos) {
        lock.readLock().lock();
        try {
            Map<ChunkPos, Map<BlockPos, CustomBlocks>> levelMap = blockRecords.getOrDefault(level, null);
            if (levelMap == null) return Collections.emptyMap();
            Map<BlockPos, CustomBlocks> chunkMap = levelMap.getOrDefault(chunkPos, null);
            return chunkMap != null ? Map.copyOf(chunkMap) : Collections.emptyMap();
        } finally {
            lock.readLock().unlock();
        }
    }

    public static Map<BlockPos, CustomBlocks> get(Level level, int chunkX, int chunkZ) {
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        return get(level, chunkPos);
    }

    public static Map<ChunkPos, Map<BlockPos, CustomBlocks>> get(Level level) {
        lock.readLock().lock();
        try {
            Map<ChunkPos, Map<BlockPos, CustomBlocks>> levelMap = blockRecords.getOrDefault(level, null);
            return levelMap != null ? Map.copyOf(levelMap) : Collections.emptyMap();
        } finally {
            lock.readLock().unlock();
        }
    }

    public static boolean is(Level level, BlockPos blockPos) {
        ChunkPos chunkPos = new ChunkPos(blockPos);
        lock.readLock().lock();
        try {
            return blockRecords.containsKey(level) && blockRecords.get(level).containsKey(chunkPos) && blockRecords.get(level).get(chunkPos).containsKey(blockPos);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static boolean is(Level level, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        return is(level, blockPos);
    }

    public static boolean is(Block block) {
        CraftWorld world = (CraftWorld) block.getWorld();
        return is(world.getHandle(), block.getX(), block.getY(), block.getZ());
    }

    public static boolean has(Level level, ChunkPos chunkPos) {
        lock.readLock().lock();
        try {
            return blockRecords.containsKey(level) && blockRecords.get(level).containsKey(chunkPos);
        } finally {
            lock.readLock().unlock();
        }
    }

    public static boolean has(Level level, int chunkX, int chunkZ) {
        ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
        return has(level, chunkPos);
    }

    public static boolean has(Level level) {
        lock.readLock().lock();
        try {
            return blockRecords.containsKey(level);
        } finally {
            lock.readLock().unlock();
        }
    }
}
