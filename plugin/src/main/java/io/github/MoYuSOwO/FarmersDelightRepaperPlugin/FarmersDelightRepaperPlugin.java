package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.PinkPetals;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;


public final class FarmersDelightRepaperPlugin extends JavaPlugin implements Listener {
    public Recipes recipes;
    public Crop crop;
    public ProtocolHandler protocolHandler;
    public CookingPot cookingPot;
    public CuttingBlock cuttingBlock;


    @Override
    public void onEnable() {
        recipes = new Recipes();
        crop = new Crop();
        protocolHandler = new ProtocolHandler(this);
        cookingPot = new CookingPot();
        cuttingBlock = new CuttingBlock();
        Item.loadItem();
        Bukkit.getPluginManager().registerEvents(recipes, this);
        Bukkit.getPluginManager().registerEvents(crop, this);
        Bukkit.getPluginManager().registerEvents(cookingPot, this);
        Bukkit.getPluginManager().registerEvents(cuttingBlock, this);
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
