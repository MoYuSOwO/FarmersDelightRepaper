package io.github.MoYuSOwO.farmersDelightRepaper.item;

import io.github.MoYuSOwO.farmersDelightRepaper.block.Blocks;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;

import java.util.Optional;

public enum Items {
    ONION(233001, "洋葱", Material.POTATO, 2, 1.6F, Blocks.ONIONS_STAGE_0),
    TOMATO(233002, "西红柿", Material.BEETROOT, 1, 0.6F),
    RICE(233003, "稻米", Material.WHEAT),
    CABBAGE(233004, "卷心菜", Material.BEETROOT, 2, 1.6F),
    RICE_PANICLE(233005, "稻米穗", Material.WHEAT),
    TOMATO_SAUCE(233006, "番茄酱", Material.MUSHROOM_STEW, 2, 3.2F),
    RAW_PASTA(233007, "生意面", Material.CHICKEN, 3, 1.2F),
    PUMPKIN_SLICE(233008, "南瓜片", Material.PUMPKIN_PIE, 3, 1.8F),
    CABBAGE_LEAF(233009, "卷心菜叶", Material.BEETROOT, 1, 0.8F),
    TOMATO_SEED(233010, "番茄种子", Material.BEETROOT_SEEDS, Blocks.TOMATOES_STAGE_0),
    CABBAGE_SEED(233011, "卷心菜种子", Material.BEETROOT_SEEDS, Blocks.CABBAGES_STAGE_0),
    COOKING_POT(233012, "厨锅", Material.CRAFTING_TABLE, Blocks.COOKING_POT),
    CUTTING_BOARD(233013, "砧板", Material.CRAFTING_TABLE, Blocks.CUTTING_BOARD),
    MINCED_BEEF(233019, "牛肉馅", Material.BEEF, 2, 1.2F),
    RAW_CHICKEN_CUTS(233014, "生鸡肉丁", Material.CHICKEN, 1, 0.6F),
    RAW_COD_SLICE(233015, "生鳕鱼片", Material.COD, 1, 0.2F),
    RAW_SALMON_SLICE(233016, "生鲑鱼片", Material.COD, 1, 0.2F),
    WHEAT_DOUGH(233017, "面团", Material.CHICKEN, 2, 1.2F),
    RAW_BACON(233018, "生培根", Material.PORKCHOP, 2, 1.2F);

    private final ItemData itemData;

    Items(int modelId, String name, Material material) {
        itemData = new ItemData(modelId, name, material, null, null, null);
    }

    Items(int modelId, String name, Material material, Integer nutrition, Float saturation) {
        itemData = new ItemData(modelId, name, material, nutrition, saturation, null);
    }

    Items(int modelId, String name, Material material, Blocks placedBlock) {
        itemData = new ItemData(modelId, name, material, null, null, placedBlock);
    }

    Items(int modelId, String name, Material material, Integer nutrition, Float saturation, Blocks placedBlock) {
        itemData = new ItemData(modelId, name, material, nutrition, saturation, placedBlock);
    }

    private static final Items[] CACHED_VALUES = values();

    public static boolean is(ItemStack itemStack, Items items) {
        return items.itemData.is(itemStack);
    }

    public static boolean is(ItemStack itemStack) {
        if (itemStack == null) return false;
        for (Items typeValue : Items.values()) {
            if (Items.is(itemStack, typeValue)) {
                return true;
            }
        }
        return false;
    }

    public static Items getItems(ItemStack itemStack) {
        for (Items items : CACHED_VALUES) {
            if (Items.is(itemStack, items)) {
                return items;
            }
        }
        return null;
    }

    public static Material getRawMaterial(Items item) {
        return item.itemData.material();
    }

    public static ItemStack get(Items item) {
        return item.itemData.get();
    }

    public static ItemStack get(Items item, int count) {
        return item.itemData.get(count);
    }

    public static boolean canEat(Items item) {
        return item.itemData.canEat();
    }

    public static boolean canPlace(Items item) {
        return item.itemData.canPlace();
    }

    public static Optional<Items> toItems(String name) {
        try {
            return Optional.of(Items.valueOf(name.toUpperCase()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public static String toString(Items type) {
        return type.name();
    }

    private record ItemData(int modelId, String name, Material material, Integer nutrition, Float saturation, Blocks placedBlock) {
        public boolean is(ItemStack itemStack) {
            if (itemStack.getType() == material) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                return itemMeta.hasCustomModelData() && itemMeta.getCustomModelData() == modelId;
            }
            return false;
        }

        public ItemStack get() {
            ItemStack itemStack = new ItemStack(material);
            ItemMeta itemMeta = itemStack.getItemMeta();
            TextComponent displayName = Component.text(name).decoration(TextDecoration.ITALIC, false);
            itemMeta.customName(displayName);
            itemMeta.setCustomModelData(modelId);
            if (nutrition != null && saturation != null) {
                FoodComponent foodComponent = itemMeta.getFood();
                foodComponent.setNutrition(nutrition);
                foodComponent.setSaturation(saturation);
                itemMeta.setFood(foodComponent);
            }
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        }

        public ItemStack get(int count) {
            ItemStack itemStack = get();
            itemStack.setAmount(count);
            return itemStack;
        }

        public boolean canEat() {
            return saturation != null || nutrition != null;
        }

        public boolean canPlace() {
            return placedBlock != null;
        }
    }
}
