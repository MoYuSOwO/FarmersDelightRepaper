package io.github.MoYuSOwO.farmersDelightRepaper.block;

import io.github.MoYuSOwO.farmersDelightRepaper.FarmersDelightRepaper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class CustomBlockDataBase {
    private static Connection connection;

    private CustomBlockDataBase() {}

    public static void init() {
        if (!FarmersDelightRepaper.instance.getDataFolder().exists()) {
            FarmersDelightRepaper.instance.getDataFolder().mkdirs();
        }
        String dbPath = FarmersDelightRepaper.instance.getDataFolder().getAbsolutePath() + "/data.db";
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            try (Statement stmt = connection.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS blocks (" +
                        "world_uuid TEXT NOT NULL," +
                        "x INTEGER NOT NULL," +
                        "y INTEGER NOT NULL," +
                        "z INTEGER NOT NULL," +
                        "custom_block_name TEXT NOT NULL," +
                        "PRIMARY KEY (world_uuid, x, y, z)" +
                        ");";
                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            FarmersDelightRepaper.instance.getLogger().severe("database Init Error: " + e);
        }
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            FarmersDelightRepaper.instance.getLogger().severe("database Close Error: " + e);
        }
    }

    public static void loadAllBlocks(Map<Level, Map<ChunkPos, Map<BlockPos, CustomBlocks>>> blockRecords) {
        String sql = "SELECT * FROM blocks";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                UUID worldUUID = UUID.fromString(rs.getString("world_uuid"));
                World world = Bukkit.getWorld(worldUUID);
                if (world == null) continue;
                Level level = ((CraftWorld) world).getHandle();
                BlockPos pos = new BlockPos(
                        rs.getInt("x"),
                        rs.getInt("y"),
                        rs.getInt("z")
                );
                CustomBlocks block = CustomBlocks.valueOf(rs.getString("custom_block_name"));
                ChunkPos chunkPos = new ChunkPos(pos);
                blockRecords.putIfAbsent(level, new HashMap<>());
                blockRecords.get(level).putIfAbsent(chunkPos, new HashMap<>());
                blockRecords.get(level).get(chunkPos).put(pos, block);
            }
        } catch (SQLException e) {
            FarmersDelightRepaper.instance.getLogger().severe("加载数据失败: " + e.getMessage());
        }
    }

    public static void saveBlock(World world, BlockPos pos, CustomBlocks block) {
        String sql = "INSERT OR REPLACE INTO blocks (world_uuid, x, y, z, custom_block_name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, world.getUID().toString());
            pstmt.setInt(2, pos.getX());
            pstmt.setInt(3, pos.getY());
            pstmt.setInt(4, pos.getZ());
            pstmt.setString(5, block.name());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            FarmersDelightRepaper.instance.getLogger().severe("保存方块失败: " + e.getMessage());
        }
    }

    public static void removeBlock(World world, BlockPos pos) {
        String sql = "DELETE FROM blocks WHERE world_uuid = ? AND x = ? AND y = ? AND z = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, world.getUID().toString());
            pstmt.setInt(2, pos.getX());
            pstmt.setInt(3, pos.getY());
            pstmt.setInt(4, pos.getZ());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            FarmersDelightRepaper.instance.getLogger().severe("删除方块失败: " + e.getMessage());
        }
    }
}
