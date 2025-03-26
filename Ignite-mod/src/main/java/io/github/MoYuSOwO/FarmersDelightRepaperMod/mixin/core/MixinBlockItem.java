package io.github.MoYuSOwO.FarmersDelightRepaperMod.mixin.core;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockItem.class)
public abstract class MixinBlockItem extends Item {
  public MixinBlockItem(Properties properties) {
    super(properties);
  }

  @Shadow
  protected abstract BlockState getPlacementState(BlockPlaceContext context);

  @Redirect(
    method = "place",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/world/item/BlockItem;getPlacementState(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"
    )
  )
  private BlockState redirectPlacementState(BlockItem instance, BlockPlaceContext context) {
    BlockState originalState = getPlacementState(context);
    ItemStack stack = context.getItemInHand();
    if (stack.has(DataComponents.CUSTOM_MODEL_DATA) && stack.getItem() == Items.POTATO) {
//      System.out.println(stack.get(DataComponents.CUSTOM_MODEL_DATA));
    }
    return originalState;
  }
}
