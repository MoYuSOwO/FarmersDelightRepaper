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

public class Item implements Listener {
    public enum Type {
        ONION,
        TOMATO,
        RICE,
        CABBAGE
    }

    private record NewItemCustom(int modelId, String name, Material material, Integer nutrition, Float saturation) {}
    private static final HashMap<Type, NewItemCustom> item = new HashMap<>();

    public Item() {
        item.put(Type.ONION, new NewItemCustom(233001, "洋葱", Material.BEETROOT, 2, 1.6F));
        item.put(Type.TOMATO, new NewItemCustom(233002, "西红柿", Material.BEETROOT, 1, 0.6F));
        item.put(Type.RICE, new NewItemCustom(233003, "稻米", Material.BEETROOT_SEEDS, null, null));
        item.put(Type.CABBAGE, new NewItemCustom(233004, "卷心菜", Material.BEETROOT, 2, 1.6F));
    }

    public static boolean isNewItem(ItemStack itemStack, Type type) {
        if (itemStack.getType() == item.get(type).material) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            return itemMeta.hasCustomModelData() && itemMeta.getCustomModelData() == item.get(type).modelId;
        }
        return false;
    }

    public static boolean isNewItem(ItemStack itemStack) {
        if (itemStack == null) return false;
        for (Type typeValue : Type.values()) {
            if (isNewItem(itemStack, typeValue)) {
                return true;
            }
        }
        return false;
    }


    public static Type getItemType(ItemStack itemStack) {
        for (Type typeValue : Type.values()) {
            if (isNewItem(itemStack, typeValue)) {
                return typeValue;
            }
        }
        return null;
    }

    public static Material getOriginalMaterial(Type type) {
        return item.get(type).material;
    }

    public static ItemStack getItemStack(Type type) {
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

    @EventHandler
    private static void checkCrafting(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) {
            return;
        }
        if (recipe.getResult().getType() == Material.BEETROOT_SOUP || recipe.getResult().getType() == Material.RED_DYE) {
            boolean isAllBeetroot = true;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && item.getType() == Material.BEETROOT && isNewItem(item)) {
                    isAllBeetroot = false;
                    break;
                }
            }
            if (!isAllBeetroot) {
                event.getInventory().setResult(null);
            }
        }
    }

    public static Type toType(String name) {
        return switch (name) {
            case "onion" -> Type.ONION;
            case "tomato" -> Type.TOMATO;
            case "rice" -> Type.RICE;
            case "cabbage" -> Type.CABBAGE;
            default -> null;
        };
    }

    public static String toString(Type type) {
        return switch (type) {
            case Type.ONION -> "onion";
            case Type.TOMATO -> "tomato";
            case Type.RICE -> "rice";
            case Type.CABBAGE -> "cabbage";
            default -> null;
        };
    }
}
