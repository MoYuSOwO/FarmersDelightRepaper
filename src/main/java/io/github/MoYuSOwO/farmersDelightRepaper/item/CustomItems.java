package io.github.MoYuSOwO.farmersDelightRepaper.item;

import io.github.MoYuSOwO.farmersDelightRepaper.block.CustomBlocks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public enum CustomItems {
    ONION(233001, "洋葱", Material.BEETROOT, 2, 1.6F, "ONIONS_STAGE_0", true),
    TOMATO(233002, "西红柿", Material.BEETROOT, 1, 0.6F),
    RICE(233003, "稻米", Material.WHEAT),
    CABBAGE(233004, "卷心菜", Material.BEETROOT, 2, 1.6F),
    RICE_PANICLE(233005, "稻米穗", Material.WHEAT),
    TOMATO_SAUCE(233006, "番茄酱", Material.MUSHROOM_STEW, 2, 3.2F),
    RAW_PASTA(233007, "生意面", Material.CHICKEN, 3, 1.2F),
    PUMPKIN_SLICE(233008, "南瓜片", Material.PUMPKIN_PIE, 3, 1.8F),
    CABBAGE_LEAF(233009, "卷心菜叶", Material.BEETROOT, 1, 0.8F),
    TOMATO_SEED(233010, "番茄种子", Material.BEETROOT_SEEDS),
    CABBAGE_SEED(233011, "卷心菜种子", Material.BEETROOT_SEEDS),
    COOKING_POT(233012, "厨锅", Material.CRAFTING_TABLE, "COOKING_POT"),
    CUTTING_BOARD(233013, "砧板", Material.CRAFTING_TABLE, "CUTTING_BOARD"),
    MINCED_BEEF(233019, "牛肉馅", Material.BEEF, 2, 1.2F),
    RAW_CHICKEN_CUTS(233014, "生鸡肉丁", Material.CHICKEN, 1, 0.6F),
    RAW_COD_SLICE(233015, "生鳕鱼片", Material.COD, 1, 0.2F),
    RAW_SALMON_SLICE(233016, "生鲑鱼片", Material.COD, 1, 0.2F),
    WHEAT_DOUGH(233017, "面团", Material.CHICKEN, 2, 1.2F),
    RAW_BACON(233018, "生培根", Material.PORKCHOP, 2, 1.2F);

    private static final CustomItems[] CACHED_VALUES = values();

    public static CustomItems toItems(String name) {
        try {
            return Optional.of(CustomItems.valueOf(name.toUpperCase())).get();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static boolean isCustomItem(ItemStack itemStack) {
        if (itemStack == null) return false;
        for (CustomItems customItems : CustomItems.values()) {
            if (customItems.is(itemStack)) {
                return true;
            }
        }
        return false;
    }

    public static @NotNull CustomItems getItems(@NotNull ItemStack itemStack) throws Exception {
        for (CustomItems customItems : CACHED_VALUES) {
            if (customItems.is(itemStack)) {
                return customItems;
            }
        }
        throw new IllegalArgumentException("Please use isCustomItem method to check first!");
    }

    private final int modelId;
    private final String name;
    private final Material rawMaterial;
    private final Integer nutrition;
    private final Float saturation;
    private final String placedBlockId;
    private final boolean isCrop;

    CustomItems(int modelId, String name, Material rawMaterial, Integer nutrition, Float saturation, String placedBlockId, boolean isCrop) {
        this.modelId = modelId;
        this.name = name;
        this.rawMaterial = rawMaterial;
        this.nutrition = nutrition;
        this.saturation = saturation;
        this.placedBlockId = placedBlockId;
        this.isCrop = isCrop;
    }

    CustomItems(int modelId, String name, Material rawMaterial) {
        this(modelId, name, rawMaterial, null, null, null, false);
    }

    CustomItems(int modelId, String name, Material rawMaterial, Integer nutrition, Float saturation) {
        this(modelId, name, rawMaterial, nutrition, saturation, null, false);
    }

    CustomItems(int modelId, String name, Material rawMaterial, String placedBlockId) {
       this(modelId, name, rawMaterial, null, null, placedBlockId, false);
    }

    CustomItems(int modelId, String name, Material rawMaterial, Integer nutrition, Float saturation, String placedBlockId) {
        this(modelId, name, rawMaterial, nutrition, saturation, placedBlockId, false);
    }

    CustomItems(int modelId, String name, Material rawMaterial, String placedBlockId, boolean isCrop) {
        this(modelId, name, rawMaterial, null, null, placedBlockId, isCrop);
    }

    public boolean is(ItemStack itemStack) {
        if (itemStack.getType() == this.rawMaterial) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            return itemMeta.hasCustomModelData() && itemMeta.getCustomModelData() == this.modelId;
        }
        return false;
    }

    public boolean isFood() {
        return this.saturation != null || this.nutrition != null;
    }

    public boolean canPlace() {
        return this.placedBlockId != null;
    }

    public boolean isCrop() {
        return this.isCrop;
    }

    public Material getRawMaterial() { return this.rawMaterial; }

    public ItemStack get() {
        ItemStack itemStack = new ItemStack(this.rawMaterial);
        ItemMeta itemMeta = itemStack.getItemMeta();
        TextComponent displayName = Component.text(this.name).decoration(TextDecoration.ITALIC, false);
        itemMeta.customName(displayName);
        itemMeta.setCustomModelData(this.modelId);
        if (isFood()) {
            FoodComponent foodComponent = itemMeta.getFood();
            foodComponent.setNutrition(this.nutrition);
            foodComponent.setSaturation(this.saturation);
            itemMeta.setFood(foodComponent);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack get(int count) {
        ItemStack itemStack = this.get();
        itemStack.setAmount(count);
        return itemStack;
    }

    public CustomBlocks getPlacedBlock() {
        return CustomBlocks.toBlocks(this.placedBlockId);
    }
}
