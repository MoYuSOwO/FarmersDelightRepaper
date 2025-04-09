package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import io.netty.channel.ChannelPipeline;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.PinkPetals;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.channels.Channel;


public final class FarmersDelightRepaperPlugin extends JavaPlugin implements Listener {
    public Recipes recipes;
    public Crop crop;
    public ProtocolHandler protocolHandler;
    public CookingPot cookingPot;
    public CuttingBlock cuttingBlock;


    @Override
    public void onEnable() {
        Item.loadItem();
        recipes = new Recipes(this);
        crop = new Crop();
        protocolHandler = new ProtocolHandler(this);
        cookingPot = new CookingPot();
        cuttingBlock = new CuttingBlock();
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(recipes, this);
        Bukkit.getPluginManager().registerEvents(crop, this);
        Bukkit.getPluginManager().registerEvents(cookingPot, this);
        Bukkit.getPluginManager().registerEvents(cuttingBlock, this);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        CraftPlayer craftPlayer = (CraftPlayer) player;
        ServerPlayer serverPlayer = craftPlayer.getHandle();
        serverPlayer.connection.connection.channel.pipeline().addBefore(
                "packet_handler", "NMS_custom_handler", new NMSProtocolHandler(serverPlayer)
        );
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (command.getName().equalsIgnoreCase("fd")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("你必须是一名玩家!");
                return true;
            }
            if (args[0].equals("check")) {
                Block target = player.getTargetBlockExact(10);
                if (target != null && target.getBlockData() instanceof PinkPetals pinkPetals) {
                    player.sendMessage("当前方向: " + pinkPetals.getFacing()
                            + " / 花数: " + pinkPetals.getFlowerAmount());
                }
                return true;
            }
            else if (args[0].equals("set")) {
                Block target = player.getTargetBlockExact(10);
                if (target != null && target.getBlockData() instanceof PinkPetals pinkPetals) {
                    switch (args[1]) {
                        case "south" -> pinkPetals.setFacing(BlockFace.SOUTH);
                        case "west" -> pinkPetals.setFacing(BlockFace.WEST);
                        case "east" -> pinkPetals.setFacing(BlockFace.EAST);
                        default -> pinkPetals.setFacing(BlockFace.NORTH);
                    }
                    target.setBlockData(pinkPetals);
                    target.getState().update(true, true);
                    return true;
                }
                getLogger().info("NONE");
                return true;
            }
            else if (Item.isNewItem(Item.getItemStack(Item.toType(args[0])))) {
                player.give(Item.getItemStack(Item.toType(args[0])));
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
