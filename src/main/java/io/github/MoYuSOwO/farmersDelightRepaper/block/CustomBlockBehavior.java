package io.github.MoYuSOwO.farmersDelightRepaper.block;

import io.github.MoYuSOwO.farmersDelightRepaper.item.CustomItems;
import io.papermc.paper.event.block.BlockBreakBlockEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CustomBlockBehavior implements Listener {

    private static final HashMap<Block, CustomBlocks> grownCrop = new HashMap<>();

    @EventHandler
    private static void onCustomPlantPlace(PlayerInteractEvent event) throws Exception {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!CustomItems.isCustomItem(event.getItem())) return;
        CustomItems customItems = CustomItems.getItems(event.getItem());
        if (!customItems.canPlace()) return;
        if (!customItems.isCrop()) return;
        if (event.getClickedBlock().getType() != Material.FARMLAND) return;
        if (event.getBlockFace() != BlockFace.UP) return;
        if (event.getClickedBlock().getRelative(BlockFace.UP).getType() != Material.AIR) return;
        event.setCancelled(true);
        CustomBlockBehavior.place(event.getClickedBlock().getRelative(BlockFace.UP), customItems.getPlacedBlockId());
    }

    @EventHandler
    private static void onCustomBlockBreak(BlockBreakEvent event) {
        if (!CustomBlockStorage.is(event.getBlock())) return;
        if (event.isCancelled()) return;
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
        CustomBlocks customBlocks = CustomBlockStorage.get(event.getBlock());
        for (int i = 0; i < customBlocks.getDropsCount(); i++) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), customBlocks.getDrop(i));
        }
        CustomBlockStorage.remove(event.getBlock());
    }

    @EventHandler
    private static void onBlockUnderCustomCropBreak(BlockBreakEvent event) {
        if (!CustomBlockStorage.is(event.getBlock().getRelative(BlockFace.UP))) return;
        if (event.isCancelled()) return;
        event.setCancelled(true);
        event.getBlock().setType(Material.AIR);
        event.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
        CustomBlocks customBlocks = CustomBlockStorage.get(event.getBlock().getRelative(BlockFace.UP));
        for (int i = 0; i < customBlocks.getDropsCount(); i++) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getRelative(BlockFace.UP).getLocation(), customBlocks.getDrop(i));
        }
        CustomBlockStorage.remove(event.getBlock().getRelative(BlockFace.UP));
    }

    @EventHandler
    private static void onWaterFlowOverCustomCrop(BlockBreakBlockEvent event) {
        if (!CustomBlockStorage.is(event.getBlock())) return;
        while (!event.getDrops().isEmpty()) event.getDrops().removeFirst();
        CustomBlocks customBlocks = CustomBlockStorage.get(event.getBlock());
        for (int i = 0; i < customBlocks.getDropsCount(); i++) {
            event.getDrops().add(customBlocks.getDrop(i));
        }
        CustomBlockStorage.remove(event.getBlock());
    }

    @EventHandler
    private static void onEntityChangeFarmland(EntityChangeBlockEvent event) {
        if (!CustomBlockStorage.is(event.getBlock().getRelative(BlockFace.UP))) return;
        if (event.getBlock().getType() != Material.FARMLAND) return;
        if (event.isCancelled()) return;
        event.setCancelled(true);
        event.getBlock().getRelative(BlockFace.UP).setType(Material.AIR);
        event.getBlock().setType(Material.DIRT);
        CustomBlocks customBlocks = CustomBlockStorage.get(event.getBlock().getRelative(BlockFace.UP));
        for (int i = 0; i < customBlocks.getDropsCount(); i++) {
            event.getBlock().getWorld().dropItemNaturally(event.getBlock().getRelative(BlockFace.UP).getLocation(), customBlocks.getDrop(i));
        }
        CustomBlockStorage.remove(event.getBlock().getRelative(BlockFace.UP));
    }

    @EventHandler
    private static void onCustomCropGrow(BlockGrowEvent event) {
        if (!CustomBlockStorage.is(event.getBlock())) return;
        if (event.isCancelled()) return;
        event.setCancelled(true);
        CustomBlocks cropBlock = CustomBlockStorage.get(event.getBlock());
        CustomBlocks nextState = CustomBlocks.getNaturalNextCropStage(cropBlock);
        if (nextState != null) {
            grownCrop.put(event.getBlock(), cropBlock);
            replace(event.getBlock(), nextState);
        }
    }

    @EventHandler
    private static void onCustomCropFertilize(BlockFertilizeEvent event) {
        if (event.isCancelled()) return;
        for (BlockState blockState : event.getBlocks()) {
            if (CustomBlockStorage.is(blockState.getBlock())) {
                if (CustomBlocks.isCropBlock(CustomBlockStorage.get(blockState.getBlock()))) {
                    replace(blockState.getBlock(), grownCrop.get(blockState.getBlock()));
                    grownCrop.remove(blockState.getBlock());
                    event.setCancelled(true);
                    CustomBlocks blocks = CustomBlockStorage.get(blockState.getBlock());
                    CustomBlocks nextState = CustomBlocks.getNextCropStage(blocks);
                    if (nextState != null) {
                        replace(blockState.getBlock(), nextState);
                        playBoneMealEffects(blockState.getLocation());
                    }
                }
            }
        }
    }

    private static void place(Block bukkitBlock, CustomBlocks target) {
        CraftWorld craftWorld = (CraftWorld) bukkitBlock.getWorld();
        Level nmsWorld = craftWorld.getHandle();
        BlockPos pos = new BlockPos(bukkitBlock.getX(), bukkitBlock.getY(), bukkitBlock.getZ());
        CustomBlockStorage.place(nmsWorld, pos, target);
        nmsWorld.setBlock(pos, target.getPlaceHolderBlockState(), 3);
    }

    private static void replace(Block bukkitBlock, CustomBlocks target) {
        CraftWorld craftWorld = (CraftWorld) bukkitBlock.getWorld();
        Level nmsWorld = craftWorld.getHandle();
        BlockPos pos = new BlockPos(bukkitBlock.getX(), bukkitBlock.getY(), bukkitBlock.getZ());
        CustomBlockStorage.replace(nmsWorld, pos, target);
        nmsWorld.setBlock(pos, target.getPlaceHolderBlockState(), 3);
    }

    private static void playBoneMealEffects(Location loc) {
        World world = loc.getWorld();
        world.spawnParticle(
                Particle.HAPPY_VILLAGER,
                loc.clone().add(0.5, 0.5, 0.5),
                15, 0.3, 0.3, 0.3, 0.5
        );
        for (int i = 0; i < 5; i++) {
            world.spawnParticle(
                    Particle.HAPPY_VILLAGER,
                    loc.clone().add(
                            0.5 + ThreadLocalRandom.current().nextGaussian() * 0.3,
                            0.1,
                            0.5 + ThreadLocalRandom.current().nextGaussian() * 0.3
                    ),
                    2, 0, 0.1, 0, 0.2
            );
        }
        world.playSound(loc, Sound.ITEM_BONE_MEAL_USE, 1.0f, 1.2f);
    }
}
