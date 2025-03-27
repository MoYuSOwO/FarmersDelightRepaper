package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public class ProtocolHandler {
    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    private final JavaPlugin plugin;

    public ProtocolHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        registerPacketListener();
    }

    private void registerPacketListener() {
        protocolManager.addPacketListener(
                new PacketAdapter(plugin, ListenerPriority.NORMAL, PacketType.Play.Server.BLOCK_CHANGE) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        handleBlockChangePacket(event);
                    }
                }
        );
    }


    private void handleBlockChangePacket(PacketEvent event) {
        HashSet<Material> coral = new HashSet<>();
        coral.add(Material.DEAD_TUBE_CORAL);
        coral.add(Material.DEAD_BRAIN_CORAL);
        coral.add(Material.DEAD_BUBBLE_CORAL);
        coral.add(Material.DEAD_FIRE_CORAL);
        coral.add(Material.DEAD_HORN_CORAL);
        WrappedBlockData wrappedBlockData = event.getPacket().getBlockData().read(0);
        if (coral.contains(wrappedBlockData.getType())) {
            plugin.getLogger().info(wrappedBlockData.toString());
        }
    }
}
