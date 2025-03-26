/*
 * This file is part of Ignite, licensed under the MIT License (MIT).
 *
 * Copyright (c) vectrix.space <https://vectrix.space/>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.github.MoYuSOwO.FarmersDelightRepaperMod.mixin.core;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PinkPetalsBlock.class)
public abstract class MixinPinkPetals extends BushBlock implements BonemealableBlock {

  protected MixinPinkPetals(Properties settings) {
    super(settings);
  }

  @Shadow @Final public static EnumProperty<Direction> FACING;
  @Shadow @Final public static IntegerProperty AMOUNT;
  @Shadow @Final public static int MAX_FLOWERS;
  @Unique private static final int farmerDelightPaperMod$MODIFIER = 100;

  /**
   * @author MoYuSOwO
   * @reason cancel natural rotation behavior
   */
  @Overwrite
  @Override
  public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
    return state;
  }

  /**
   * @author MoYuSOwO
   * @reason cancel natural rotation behavior
   */
  @Overwrite
  @Override
  public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
    return state;
  }

  /**
   * @author MoYuSOwO
   * @reason force to the NORTH when place
   */
  @Overwrite
  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());
    return blockState.is(this)
      ? blockState.setValue(AMOUNT, Math.min(4, blockState.getValue(AMOUNT) + 1))
      : this.defaultBlockState().setValue(FACING, Direction.NORTH);
  }


  /**
   * @author MoYuSOwO
   * @reason cancel flower drop if is not origin PinkPetal
   */
  @Overwrite
  @Override
  public void performBonemeal(ServerLevel world, RandomSource random, BlockPos pos, BlockState state) {
    int i = state.getValue(AMOUNT);
    if (state.getValue(FACING) != Direction.NORTH) {
      if (i < 4) {
        world.setBlock(pos, state.setValue(AMOUNT, Integer.valueOf(i + 1)), 2);
      }
      return;
    }
    if (i < 4) {
      world.setBlock(pos, state.setValue(AMOUNT, Integer.valueOf(i + 1)), 2);
    } else {
      popResource(world, pos, new ItemStack(this));
    }
  }

  @Unique
  @Override
  protected boolean isRandomlyTicking(BlockState state) {
    if (state.getValue(FACING) == Direction.NORTH) return false;
    return state.getValue(AMOUNT) != 4;
  }


  @Unique
  @Override
  protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
    if (level.getRawBrightness(pos, 0) >= 9) {
      int age = state.getValue(AMOUNT);
      if (age < MAX_FLOWERS) {
        float growthSpeed = farmerDelightPaperMod$getGrowthSpeed(this, level, pos);
        if (random.nextFloat() < (farmerDelightPaperMod$MODIFIER / (100.0f * (Math.floor((25.0F / growthSpeed) + 1))))) {
          org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(level, pos, state.setValue(AMOUNT, state.getValue(AMOUNT) + 1), 2);
        }
      }
    }
  }

  @Unique
  private static float farmerDelightPaperMod$getGrowthSpeed(Block block, BlockGetter level, BlockPos pos) {
    float f = 1.0F;
    BlockPos blockPos = pos.below();

    for (int i = -1; i <= 1; i++) {
      for (int i1 = -1; i1 <= 1; i1++) {
        float f1 = 0.0F;
        BlockState blockState = level.getBlockState(blockPos.offset(i, 0, i1));
        if (blockState.is(Blocks.FARMLAND)) {
          f1 = 1.0F;
          if (blockState.getValue(FarmBlock.MOISTURE) > 0) {
            f1 = 3.0F;
          }
        }

        if (i != 0 || i1 != 0) {
          f1 /= 4.0F;
        }

        f += f1;
      }
    }

    BlockPos blockPos1 = pos.north();
    BlockPos blockPos2 = pos.south();
    BlockPos blockPos3 = pos.west();
    BlockPos blockPos4 = pos.east();
    boolean flag = level.getBlockState(blockPos3).is(block) || level.getBlockState(blockPos4).is(block);
    boolean flag1 = level.getBlockState(blockPos1).is(block) || level.getBlockState(blockPos2).is(block);
    if (flag && flag1) {
      f /= 2.0F;
    } else {
      boolean flag2 = level.getBlockState(blockPos3.north()).is(block)
        || level.getBlockState(blockPos4.north()).is(block)
        || level.getBlockState(blockPos4.south()).is(block)
        || level.getBlockState(blockPos3.south()).is(block);
      if (flag2) {
        f /= 2.0F;
      }
    }

    return f;
  }

}
