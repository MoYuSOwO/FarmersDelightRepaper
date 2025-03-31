package io.github.MoYuSOwO.FarmersDelightRepaperMod.mixin.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.redstone.Redstone;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LeavesBlock.class)
public abstract class MixinLeavesBlock extends Block implements SimpleWaterloggedBlock {
  public MixinLeavesBlock(Properties properties) {
    super(properties);
  }

  @Shadow
  private static int getDistanceAt(BlockState neighbor) {
    return 0;
  }

  @Shadow @Final public static IntegerProperty DISTANCE;
  @Shadow @Final public static BooleanProperty PERSISTENT;
  @Shadow @Final public static BooleanProperty WATERLOGGED;

  /**
   * @author MoYuSOwO
   * @reason to remap the distance to 1 or 7 due to no image difference
   */
  @Overwrite
  private static BlockState updateDistance(BlockState state, LevelAccessor level, BlockPos pos) {
    if (state.getValue(PERSISTENT)) return state; // Inject

    int i = 7;
    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

    for (Direction direction : Direction.values()) {
      mutableBlockPos.setWithOffset(pos, direction);
      i = Math.min(i, getDistanceAt(level.getBlockState(mutableBlockPos)) + 1);
      if (i == 1) {
        break;
      }
    }

    if (i != 7) i = 1; //Inject

    return state.setValue(DISTANCE, Integer.valueOf(i));
  }

  /**
   * @author MoYuSOwO
   * @reason let the placed leaves distance=7 due to useless
   */
  @Overwrite
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
    BlockState blockState = this.defaultBlockState()
      .setValue(DISTANCE, 7)
      .setValue(PERSISTENT, Boolean.valueOf(true))
      .setValue(WATERLOGGED, Boolean.valueOf(fluidState.getType() == Fluids.WATER));
    return blockState;
  }
}
