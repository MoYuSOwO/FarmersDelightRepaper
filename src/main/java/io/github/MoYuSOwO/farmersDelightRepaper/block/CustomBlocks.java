package io.github.MoYuSOwO.farmersDelightRepaper.block;

import io.github.MoYuSOwO.farmersDelightRepaper.item.CustomItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public enum CustomBlocks {
    CUTTING_BOARD(
            Blocks.STONE.defaultBlockState(),
            Blocks.OAK_LEAVES.defaultBlockState()
                    .setValue(BlockStateProperties.DISTANCE, 2)
                    .setValue(BlockStateProperties.WATERLOGGED, false)
                    .setValue(BlockStateProperties.PERSISTENT, true),
            2.0F,
            new BlockDrops[]{
                    new BlockDrops("CUTTING_BOARD", 1, 1)
            }
    ),
    COOKING_POT(
            Blocks.STONE.defaultBlockState(),
            Blocks.OAK_LEAVES.defaultBlockState()
                    .setValue(BlockStateProperties.DISTANCE, 3)
                    .setValue(BlockStateProperties.WATERLOGGED, false)
                    .setValue(BlockStateProperties.PERSISTENT, true),
            2.0F,
            new BlockDrops[]{
                    new BlockDrops("COOKING_POT", 1, 1)
            }
    ),
    ONIONS_STAGE_0(
            Blocks.WHEAT.defaultBlockState()
                    .setValue(BlockStateProperties.AGE_7, 0),
            Blocks.TRIPWIRE.defaultBlockState()
                    .setValue(BlockStateProperties.ATTACHED, true)
                    .setValue(BlockStateProperties.DISARMED, true)
                    .setValue(BlockStateProperties.EAST, false)
                    .setValue(BlockStateProperties.WEST, false)
                    .setValue(BlockStateProperties.SOUTH, false)
                    .setValue(BlockStateProperties.NORTH, false)
                    .setValue(BlockStateProperties.POWERED, false),
            new BlockDrops[]{
                    new BlockDrops("ONION", 1, 1),
            }
    ),
    ONIONS_STAGE_1(
            Blocks.WHEAT.defaultBlockState()
                    .setValue(BlockStateProperties.AGE_7, 1),
            Blocks.TRIPWIRE.defaultBlockState()
                    .setValue(BlockStateProperties.ATTACHED, false)
                    .setValue(BlockStateProperties.DISARMED, true)
                    .setValue(BlockStateProperties.EAST, false)
                    .setValue(BlockStateProperties.WEST, false)
                    .setValue(BlockStateProperties.SOUTH, false)
                    .setValue(BlockStateProperties.NORTH, false)
                    .setValue(BlockStateProperties.POWERED, false),
            new BlockDrops[]{
                    new BlockDrops("ONION", 1, 1)
            }
    ),
    ONIONS_STAGE_2(
            Blocks.WHEAT.defaultBlockState()
                    .setValue(BlockStateProperties.AGE_7, 2),
            Blocks.SUGAR_CANE.defaultBlockState()
                    .setValue(BlockStateProperties.AGE_15, 1),
            new BlockDrops[]{
                    new BlockDrops("ONION", 1, 1)
            }
    ),
    ONIONS_STAGE_3(
            Blocks.WHEAT.defaultBlockState()
                    .setValue(BlockStateProperties.AGE_7, 3),
            Blocks.SUGAR_CANE.defaultBlockState()
                    .setValue(BlockStateProperties.AGE_15, 2),
            new BlockDrops[]{
                    new BlockDrops("ONION", 2, 5)
            }
    );

    private static final List<CustomBlocks> cropBlocks = List.of(
            ONIONS_STAGE_0,
            ONIONS_STAGE_1,
            ONIONS_STAGE_2,
            ONIONS_STAGE_3
    );

    private static final Set<CustomBlocks> maxCropStage = Set.of(
            ONIONS_STAGE_3
    );

    public static boolean isCropBlock(CustomBlocks customBlocks) {
        return cropBlocks.contains(customBlocks);
    }

    public static CustomBlocks getNaturalNextCropStage(CustomBlocks cropBlock) {
        if (!cropBlocks.contains(cropBlock)) throw new IllegalArgumentException("The block should be cropBlocks!");
        if (maxCropStage.contains(cropBlock)) return null;
        else {
            int id = cropBlocks.indexOf(cropBlock);
            switch (id) {
                case 0, 1, 2 -> {
                    if (ThreadLocalRandom.current().nextDouble() < 0.75) return cropBlocks.get(id + 1);
                    else return cropBlocks.get(id);
                }
                default -> {
                    return cropBlocks.get(id);
                }
            }
        }
    }

    public static CustomBlocks getNextCropStage(CustomBlocks cropBlock) {
        if (!cropBlocks.contains(cropBlock)) throw new IllegalArgumentException("The block should be cropBlocks!");
        if (maxCropStage.contains(cropBlock)) return null;
        else {
            int id = cropBlocks.indexOf(cropBlock);
            switch (id) {
                case 0, 1, 2 -> {
                    return cropBlocks.get(id + 1);
                }
                default -> {
                    return cropBlocks.get(id);
                }
            }
        }
    }

    public static CustomBlocks toBlocks(String name) {
        try {
            return Optional.of(CustomBlocks.valueOf(name.toUpperCase())).get();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private final int placeHolderBlockStateId, showBlockStateId;
    private final Float hardness;
    public final BlockDrops[] drops;

    public record BlockDrops(String dropItemId, int minCount, int maxCount) {}

    CustomBlocks(int placeHolderBlockStateId, int showBlockStateId, Float hardness, BlockDrops[] drops) {
        this.placeHolderBlockStateId = placeHolderBlockStateId;
        this.showBlockStateId = showBlockStateId;
        this.hardness = hardness;
        this.drops = drops;
    }

    CustomBlocks(BlockState placeHolderBlockState, BlockState showBlockState, Float hardness, BlockDrops[] drops) {
        this(Block.getId(placeHolderBlockState), Block.getId(showBlockState), hardness, drops);
    }

    CustomBlocks(int placeHolderBlockStateId, int showBlockStateId, BlockDrops[] drops) {
        this(placeHolderBlockStateId, showBlockStateId, null, drops);
    }

    CustomBlocks(BlockState placeHolderBlockState, BlockState showBlockState, BlockDrops[] drops) {
        this(Block.getId(placeHolderBlockState), Block.getId(showBlockState), null, drops);
    }

    public int getPlaceHolderBlockStateId() {
        return this.placeHolderBlockStateId;
    }

    public int getShowBlockStateId() {
        return this.showBlockStateId;
    }

    public BlockState getPlaceHolderBlockState() {
        return Block.stateById(placeHolderBlockStateId);
    }

    public BlockState getShowBlockState() {
        return Block.stateById(showBlockStateId);
    }

    public boolean canBreakImmediately() {
        return hardness == null;
    }

    public int getDropsCount() { return this.drops.length; }

    public ItemStack getDrop(int index) {
        BlockDrops blockDrops = this.drops[index];
        CustomItems dropItem = CustomItems.toItems(blockDrops.dropItemId);
        return dropItem.get(ThreadLocalRandom.current().nextInt(blockDrops.minCount, blockDrops.maxCount + 1));
    }
}
