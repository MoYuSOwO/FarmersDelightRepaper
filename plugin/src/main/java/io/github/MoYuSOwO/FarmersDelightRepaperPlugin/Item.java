package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;

import java.util.HashMap;

public class Item {

    private record NewItemCustom(int modelId, String name, Material material, Integer nutrition, Float saturation) {}
    private static final HashMap<Items, NewItemCustom> item = new HashMap<>();

    public static void loadItem() {
        item.put(Items.ONION, new NewItemCustom(233001, "洋葱", Material.POTATO, 2, 1.6F));
        item.put(Items.TOMATO, new NewItemCustom(233002, "西红柿", Material.BEETROOT, 1, 0.6F));
        item.put(Items.RICE, new NewItemCustom(233003, "稻米", Material.WHEAT, null, null));
        item.put(Items.CABBAGE, new NewItemCustom(233004, "卷心菜", Material.BEETROOT, 2, 1.6F));
        item.put(Items.RICE_PANICLE, new NewItemCustom(233005, "稻米穗", Material.WHEAT, null, null));
        item.put(Items.TOMATO_SAUCE, new NewItemCustom(233006, "番茄酱", Material.MUSHROOM_STEW, 2, 3.2F));
        item.put(Items.RAW_PASTA, new NewItemCustom(233007, "生意面", Material.CHICKEN, 3, 1.2F));
        item.put(Items.PUMPKIN_SLICE, new NewItemCustom(233008, "南瓜片", Material.PUMPKIN_PIE, 3, 1.8F));
        item.put(Items.CABBAGE_LEAF, new NewItemCustom(233009, "卷心菜叶", Material.BEETROOT, 1, 0.8F));
        item.put(Items.TOMATO_SEED, new NewItemCustom(233010, "番茄种子", Material.BEETROOT_SEEDS, null, null));
        item.put(Items.CABBAGE_SEED, new NewItemCustom(233011, "卷心菜种子", Material.BEETROOT_SEEDS, null, null));
        item.put(Items.COOKING_POT, new NewItemCustom(233012, "厨锅", Material.CRAFTING_TABLE, null, null));
        item.put(Items.CUTTING_BLOCK, new NewItemCustom(233013, "砧板块", Material.CRAFTING_TABLE, null, null));
        item.put(Items.RAW_CHICKEN_CUTS, new NewItemCustom(233014, "生鸡肉丁", Material.CHICKEN, 1, 0.6F));
        item.put(Items.RAW_COD_SLICE, new NewItemCustom(233015, "生鳕鱼片", Material.COD, 1, 0.2F));
        item.put(Items.RAW_SALMON_SLICE, new NewItemCustom(233016, "生鲑鱼片", Material.COD, 1, 0.2F));
        item.put(Items.WHEAT_DOUGH, new NewItemCustom(233017, "面团", Material.CHICKEN, 2, 1.2F));
        item.put(Items.RAW_BACON, new NewItemCustom(233018, "生培根", Material.PORKCHOP, 2, 1.2F));
        item.put(Items.MINCED_BEEF, new NewItemCustom(233019, "牛肉馅", Material.BEEF, 2, 1.2F));
    }

    public static boolean isNewItem(ItemStack itemStack, Items type) {
        if (itemStack.getType() == item.get(type).material) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            return itemMeta.hasCustomModelData() && itemMeta.getCustomModelData() == item.get(type).modelId;
        }
        return false;
    }

    public static boolean isNewItem(ItemStack itemStack) {
        if (itemStack == null) return false;
        for (Items typeValue : Items.values()) {
            if (isNewItem(itemStack, typeValue)) {
                return true;
            }
        }
        return false;
    }


    public static Items getItemType(ItemStack itemStack) {
        for (Items typeValue : Items.values()) {
            if (isNewItem(itemStack, typeValue)) {
                return typeValue;
            }
        }
        return null;
    }

    public static Material getOriginalMaterial(Items type) {
        return item.get(type).material;
    }

    public static ItemStack getItemStack(Items type) {
        if (type == null) return null;
        ItemStack itemStack = new ItemStack(item.get(type).material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        TextComponent name = Component.text(item.get(type).name).decoration(TextDecoration.ITALIC, false);
        itemMeta.customName(name);
        itemMeta.setCustomModelData(item.get(type).modelId);
        if (item.get(type).nutrition != null && item.get(type).saturation != null) {
            FoodComponent foodComponent = itemMeta.getFood();
            foodComponent.setNutrition(item.get(type).nutrition);
            foodComponent.setSaturation(item.get(type).saturation);
            itemMeta.setFood(foodComponent);
        }
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static ItemStack getItemStack(Items type, int count) {
        if (type == null) return null;
        ItemStack itemStack = new ItemStack(item.get(type).material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        TextComponent name = Component.text(item.get(type).name).decoration(TextDecoration.ITALIC, false);
        itemMeta.customName(name);
        itemMeta.setCustomModelData(item.get(type).modelId);
        if (item.get(type).nutrition != null && item.get(type).saturation != null) {
            FoodComponent foodComponent = itemMeta.getFood();
            foodComponent.setNutrition(item.get(type).nutrition);
            foodComponent.setSaturation(item.get(type).saturation);
            itemMeta.setFood(foodComponent);
        }
        itemStack.setItemMeta(itemMeta);
        itemStack.setAmount(count);
        return itemStack;
    }

    public static Items toType(String name) {
        return switch (name) {
            case "onion" -> Items.ONION;
            case "tomato" -> Items.TOMATO;
            case "rice" -> Items.RICE;
            case "cabbage" -> Items.CABBAGE;
            case "cabbage_leaf" -> Items.CABBAGE_LEAF;
            case "pumpkin_slice" -> Items.PUMPKIN_SLICE;
            case "cabbage_seed" -> Items.CABBAGE_SEED;
            case "tomato_sauce" -> Items.TOMATO_SAUCE;
            case "tomato_seed" -> Items.TOMATO_SEED;
            case "raw_pasta" -> Items.RAW_PASTA;
            case "rice_panicle" -> Items.RICE_PANICLE;
            case "cooking_pot" -> Items.COOKING_POT;
            case "cutting_block" -> Items.CUTTING_BLOCK;
            case "raw_chicken_cuts" -> Items.RAW_CHICKEN_CUTS;
            case "raw_cod_slice" -> Items.RAW_COD_SLICE;
            case "raw_salmon_slice" -> Items.RAW_SALMON_SLICE;
            case "wheat_dough" -> Items.WHEAT_DOUGH;
            case "raw_bacon" -> Items.RAW_BACON;
            case "minced_beef" -> Items.MINCED_BEEF;
            default -> null;
        };
    }

    public static String toString(Items type) {
        return switch (type) {
            case ONION -> "onion";
            case TOMATO -> "tomato";
            case RICE -> "rice";
            case CABBAGE -> "cabbage";
            case CABBAGE_LEAF -> "cabbage_leaf";
            case PUMPKIN_SLICE -> "pumpkin_slice";
            case CABBAGE_SEED -> "cabbage_seed";
            case TOMATO_SAUCE -> "tomato_sauce";
            case TOMATO_SEED -> "tomato_seed";
            case RAW_PASTA -> "raw_pasta";
            case RICE_PANICLE -> "rice_panicle";
            case COOKING_POT -> "cooking_pot";
            case CUTTING_BLOCK -> "cutting_block";
            case RAW_CHICKEN_CUTS -> "raw_chicken_cuts";
            case RAW_COD_SLICE -> "raw_cod_slice";
            case RAW_SALMON_SLICE -> "raw_salmon_slice";
            case WHEAT_DOUGH -> "wheat_dough";
            case RAW_BACON -> "raw_bacon";
            case MINCED_BEEF -> "minced_beef";
        };
    }
}
