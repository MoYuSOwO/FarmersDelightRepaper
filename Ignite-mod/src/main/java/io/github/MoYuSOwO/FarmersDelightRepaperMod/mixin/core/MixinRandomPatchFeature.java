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

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.RandomPatchFeature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RandomPatchFeature.class)
public abstract class MixinRandomPatchFeature extends Feature<RandomPatchConfiguration> {
  public MixinRandomPatchFeature(Codec<RandomPatchConfiguration> configCodec) {
    super(configCodec);
  }


  @Overwrite
  @Override
  public boolean place(FeaturePlaceContext<RandomPatchConfiguration> context) {
    RandomPatchConfiguration randomPatchConfiguration = context.config();
    RandomSource randomSource = context.random();
    BlockPos blockPos = context.origin();
    WorldGenLevel worldGenLevel = context.level();
    int i = 0;
    BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
    int i1 = randomPatchConfiguration.xzSpread() + 1;
    int i2 = randomPatchConfiguration.ySpread() + 1;

    for (int i3 = 0; i3 < randomPatchConfiguration.tries(); i3++) {
      mutableBlockPos.setWithOffset(
        blockPos,
        randomSource.nextInt(i1) - randomSource.nextInt(i1),
        randomSource.nextInt(i2) - randomSource.nextInt(i2),
        randomSource.nextInt(i1) - randomSource.nextInt(i1)
      );
      if (randomPatchConfiguration.feature().value().place(worldGenLevel, context.chunkGenerator(), randomSource, mutableBlockPos)) {
        BlockState state = worldGenLevel.getBlockState(mutableBlockPos);
        if (state.getBlock() instanceof PinkPetalsBlock) {
          BlockState newState = state.setValue(PinkPetalsBlock.FACING, Direction.NORTH);
          worldGenLevel.setBlock(mutableBlockPos, newState, 3);
        }
        i++;
      }
    }

    return i > 0;
  }
}
