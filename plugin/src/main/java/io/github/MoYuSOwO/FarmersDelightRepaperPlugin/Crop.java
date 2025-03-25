package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.block.data.type.PinkPetals;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ThreadLocalRandom;

public class Crop implements Listener {

    public final JavaPlugin plugin;

    public Crop(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private boolean isCrop(Block block) {
        if (block.getType() != Material.PINK_PETALS) return false;
        if (block.getBlockData() instanceof PinkPetals pinkPetals) {
            if (pinkPetals.getFacing() == BlockFace.NORTH) return false;
            else return true;
        }
        return false;
    }

    private Item.Type getCropType(Block block) {
        if (block.getBlockData() instanceof PinkPetals pinkPetals) {
            switch (pinkPetals.getFacing()) {
                case SOUTH -> {
                    return Item.Type.ONION;
                }
                default -> {
                    return null;
                }
            }
        }
        return null;
    }

    private Integer getCropStage(Block block) {
        if (block.getBlockData() instanceof PinkPetals pinkPetals) {
            return pinkPetals.getFlowerAmount();
        }
        return null;
    }

    @EventHandler
    public void onCropPlace(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack itemInHand = event.getItem();
            if (itemInHand != null && Item.isNewItem(event.getItem(), Item.Type.ONION)) {
                Block clickedBlock = event.getClickedBlock();
                if (clickedBlock.getType() == Material.FARMLAND && event.getBlockFace() == BlockFace.UP) {
                    Block targetBlock = event.getClickedBlock().getRelative(BlockFace.UP);
                    if (targetBlock.getType() == Material.AIR) {
                        targetBlock.setType(Material.PINK_PETALS);
                        if (targetBlock.getBlockData() instanceof PinkPetals pinkPetals) {
                            pinkPetals.setFacing(BlockFace.SOUTH);
                            pinkPetals.setFlowerAmount(1);
                            targetBlock.setBlockData(pinkPetals);
                            targetBlock.getState().update(true, true);
                        }
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onCropBreak(BlockBreakEvent event) {
        if (isCrop(event.getBlock())) {
            switch (getCropType(event.getBlock())) {
                case ONION -> {
                    if (getCropStage(event.getBlock()) != 4) {
                        event.setDropItems(false);
                        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Item.getItemStack(Item.Type.ONION));
                        return;
                    }
                    ItemStack onion = Item.getItemStack(Item.Type.ONION);
                    onion.add(ThreadLocalRandom.current().nextInt(2, 5));
                    event.setDropItems(false);
                    event.getPlayer().getWorld().dropItemNaturally(event.getBlock().getLocation(), onion);
                }
                case RICE -> {}
                case null -> {}
                default -> {}
            }
        }
        else if (isCrop(event.getBlock().getRelative(BlockFace.UP))) {
            switch (getCropType(event.getBlock().getRelative(BlockFace.UP))) {
                case ONION -> {
                    if (getCropStage(event.getBlock().getRelative(BlockFace.UP)) != 4) {
                        event.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Item.getItemStack(Item.Type.ONION));
                        return;
                    }
                    ItemStack onion = Item.getItemStack(Item.Type.ONION);
                    onion.add(ThreadLocalRandom.current().nextInt(2, 5));
                    event.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
                    event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), onion);
                }
                case RICE -> {}
                case null -> {}
                default -> {}
            }
        }
    }

    @EventHandler
    public void stopFarmlandChange(BlockFadeEvent event) {
        if (isCrop(event.getBlock().getRelative(BlockFace.UP))) event.setCancelled(true);
    }

    @EventHandler
    public void dropWhenFarmlandChange(EntityChangeBlockEvent event) {
        if (!isCrop(event.getBlock().getRelative(BlockFace.UP))) return;
        if (event.getEntityType() != EntityType.PLAYER) return;
        Block crop = event.getBlock().getRelative(BlockFace.UP);
        switch (getCropType(crop)) {
            case ONION -> {
                if (getCropStage(crop) != 4) {
                    crop.setType(Material.AIR);
                    crop.getWorld().dropItemNaturally(crop.getLocation(), Item.getItemStack(Item.Type.ONION));
                    return;
                }
                ItemStack onion = Item.getItemStack(Item.Type.ONION);
                onion.add(ThreadLocalRandom.current().nextInt(2, 5));
                crop.setType(Material.AIR);
                crop.getWorld().dropItemNaturally(crop.getLocation(), onion);
            }
            case RICE -> {}
            default -> {}
        }
    }

    @EventHandler
    public void onWaterFlow(BlockFromToEvent event) {
        Block source = event.getBlock();
        Block target = event.getToBlock();
        Item.Type type = getCropType(target);
        if (isCrop(target) && source.getType() == Material.WATER) {
            event.setCancelled(true);
            target.setType(Material.WATER);
            Levelled sourceData = (Levelled) source.getBlockData();
            Levelled targetData = (Levelled) source.getBlockData();
            targetData.setLevel(sourceData.getLevel() + 1);
            target.setBlockData(targetData);
            target.getState().update();
            target.getWorld().dropItemNaturally(target.getLocation(), Item.getItemStack(type));
        }
    }

    @EventHandler
    public void onBlockPlaceOnCrop(PlayerBucketEmptyEvent event) {
        if (event.getBucket() != Material.WATER_BUCKET) return;
        if (event.getBlockFace() != BlockFace.UP) return;
        if (!isCrop(event.getBlockClicked().getRelative(BlockFace.UP))) return;
        if (event.getBlockClicked().getRelative(BlockFace.UP).getRelative(BlockFace.UP).getType() == Material.AIR) {
            event.getBlockClicked().getRelative(BlockFace.UP).getRelative(BlockFace.UP).setType(Material.WATER);
        }
        event.setCancelled(true);
    }
}
