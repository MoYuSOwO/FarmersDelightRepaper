package io.github.MoYuSOwO.farmersDelightRepaper;

import io.github.MoYuSOwO.farmersDelightRepaper.block.BlockMapping;
import io.github.MoYuSOwO.farmersDelightRepaper.block.BlockPacketHandler;
import io.github.MoYuSOwO.farmersDelightRepaper.item.Items;
import io.github.MoYuSOwO.farmersDelightRepaper.item.Recipes;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class FarmersDelightRepaper extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        BlockMapping.init(this);
        Recipes.init(this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        serverPlayer.connection.connection.channel.pipeline().addBefore(
                "packet_handler", "block_packet_custom_handler", new BlockPacketHandler()
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("fd")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("你必须是一名玩家!");
                return true;
            }
            if (Items.toItems(args[0]).isPresent()) {
                ItemStack itemStack = Items.get(Items.toItems(args[0]).get());
                player.give(itemStack);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.resetRecipes();
    }
}
