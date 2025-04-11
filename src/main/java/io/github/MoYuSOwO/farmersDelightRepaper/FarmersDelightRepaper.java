package io.github.MoYuSOwO.farmersDelightRepaper;

import io.github.MoYuSOwO.farmersDelightRepaper.block.*;
import io.github.MoYuSOwO.farmersDelightRepaper.item.CustomItems;
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

    public static FarmersDelightRepaper instance;

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new CustomBlockBehavior(), this);
        BlockMapping.init(this);
        Recipes.init(this);
        CustomBlockDataBase.init();
        CustomBlockStorage.loadFromDatabase();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        serverPlayer.connection.connection.channel.pipeline().addBefore(
                "packet_handler", "block_packet_custom_handler", new BlockPacketHandler(serverPlayer)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("fd")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("你必须是一名玩家!");
                return true;
            }
            CustomItems customItems = CustomItems.toItems(args[0]);
            if (customItems != null) {
                ItemStack itemStack = customItems.get();
                player.give(itemStack);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public void onDisable() {
        CustomBlockDataBase.close();
        Bukkit.resetRecipes();
    }
}
