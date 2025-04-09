package io.github.MoYuSOwO.farmersDelightRepaper.block;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BlockMapping {
    private static final Map<Integer, Integer> blockMapping = new ConcurrentHashMap<>();

    public static void init(JavaPlugin plugin) {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        File configFile = new File(plugin.getDataFolder(), "mappings.yml");
        if (!configFile.exists()) {
            try (InputStream in = plugin.getResource("mappings.yml")) {
                Files.copy(in, configFile.toPath());
                plugin.getLogger().info("默认配置文件已生成！");
            } catch (Exception e) {
                plugin.getLogger().severe("无法生成默认配置: " + e.getMessage());
            }
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        for (String key : config.getKeys(false)) {
            String value = config.getString(key);
            Integer fromState = parseBlockState(key, plugin);
            Integer toState = parseBlockState(value, plugin);
            if (fromState != null && toState != null) {
                blockMapping.put(fromState, toState);
            }
        }
    }

    private static Integer parseBlockState(String input, JavaPlugin plugin) {
        try {
            String[] parts = input.split("\\[");
            ResourceLocation blockId = ResourceLocation.parse(parts[0]);
            Block block = BuiltInRegistries.BLOCK.getValue(blockId);
            String propertiesStr = parts[1].replace("]", "");
            BlockState state = block.defaultBlockState();
            for (String propPair : propertiesStr.split(",")) {
                String[] kv = propPair.split("=");
                Property<?> property = block.getStateDefinition().getProperty(kv[0]);
                if (property != null) {
                    state = setProperty(state, property, kv[1]);
                }
            }
            return Block.getId(state);
        } catch (Exception e) {
            plugin.getLogger().warning("无法解析 BlockState: " + input);
            return null;
        }
    }

    private static <T extends Comparable<T>> BlockState setProperty(
            BlockState state, Property<T> property, String value
    ) {
        return state.setValue(property, property.getValue(value).orElseThrow());
    }

    public static Integer getMappedStateId(Integer original) {
        return blockMapping.getOrDefault(original, null);
    }

    public static Integer getMappedStateId(BlockState original) {
        return blockMapping.getOrDefault(Block.getId(original), null);
    }

    public static BlockState getMappedState(Integer original) {
        Integer stateId = blockMapping.getOrDefault(original, null);
        if (stateId != null) return Block.stateById(stateId);
        else return null;
    }

    public static BlockState getMappedState(BlockState original) {
        Integer stateId = blockMapping.getOrDefault(Block.getId(original), null);
        if (stateId != null) return Block.stateById(stateId);
        else return null;
    }
}
