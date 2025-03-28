package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import org.bukkit.inventory.ItemStack;

public record CuttingRecipe(
        ItemStack input,
        ItemStack[] output
) {}
