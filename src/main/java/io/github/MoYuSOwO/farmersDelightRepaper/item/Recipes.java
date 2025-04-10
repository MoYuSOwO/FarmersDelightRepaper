package io.github.MoYuSOwO.farmersDelightRepaper.item;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

public class Recipes implements Listener {

    private static JavaPlugin thisPlugin;

    public static void init(JavaPlugin plugin) {
        thisPlugin = plugin;
        addCraftingTableRecipes();
    }

    private static void addCraftingTableRecipes() {
        ShapelessRecipe tomatoSeed = new ShapelessRecipe(new NamespacedKey(thisPlugin, "tomatoSeed"), CustomItems.TOMATO_SEED.get());
        tomatoSeed = tomatoSeed.addIngredient(new RecipeChoice.ExactChoice(CustomItems.TOMATO.get()));
        Bukkit.addRecipe(tomatoSeed);
        ShapelessRecipe rice = new ShapelessRecipe(new NamespacedKey(thisPlugin, "rice"), CustomItems.RICE.get());
        rice = rice.addIngredient(new RecipeChoice.ExactChoice(CustomItems.RICE_PANICLE.get()));
        Bukkit.addRecipe(rice);
        ShapelessRecipe tomatoSauce = new ShapelessRecipe(new NamespacedKey(thisPlugin, "tomatoSauce"), CustomItems.TOMATO_SAUCE.get());
        tomatoSauce = tomatoSauce.addIngredient(new RecipeChoice.ExactChoice(CustomItems.TOMATO.get()))
                .addIngredient(new RecipeChoice.ExactChoice(CustomItems.TOMATO.get()))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.BOWL)));
        Bukkit.addRecipe(tomatoSauce);
        ShapelessRecipe rawPasta = new ShapelessRecipe(new NamespacedKey(thisPlugin, "rawPasta"), CustomItems.RAW_PASTA.get(2));
        rawPasta = rawPasta.addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WATER_BUCKET)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)));
        Bukkit.addRecipe(rawPasta);
        ShapelessRecipe rawPasta1 = new ShapelessRecipe(new NamespacedKey(thisPlugin, "rawPasta1"), CustomItems.RAW_PASTA.get());
        rawPasta1 = rawPasta1.addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WATER_BUCKET)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)));
        Bukkit.addRecipe(rawPasta1);
        ShapelessRecipe rawPasta2 = new ShapelessRecipe(new NamespacedKey(thisPlugin, "rawPasta2"), CustomItems.RAW_PASTA.get());
        rawPasta2 = rawPasta2.addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.EGG)))
                .addIngredient( new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient( new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)));
        Bukkit.addRecipe(rawPasta2);
        ShapelessRecipe dough = new ShapelessRecipe(new NamespacedKey(thisPlugin, "dough"), CustomItems.WHEAT_DOUGH.get(3));
        dough = dough.addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WATER_BUCKET)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)));
        Bukkit.addRecipe(dough);
    }

    @EventHandler
    private static void checkCrafting(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;
        Material result = recipe.getResult().getType();
        if (result == Material.BEETROOT_SOUP || result == Material.RED_DYE) {
            boolean isAllBeetroot = true;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && item.getType() == Material.BEETROOT && CustomItems.isCustomItem(item)) {
                    isAllBeetroot = false;
                    break;
                }
            }
            if (!isAllBeetroot) {
                event.getInventory().setResult(null);
            }
        }
        else if (result == Material.BREAD || result == Material.CAKE || result == Material.COOKIE || result == Material.HAY_BLOCK || result == Material.PACKED_MUD) {
            boolean isAllWheat = true;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && item.getType() == Material.WHEAT && CustomItems.isCustomItem(item)) {
                    isAllWheat = false;
                    break;
                }
            }
            if (!isAllWheat) {
                event.getInventory().setResult(null);
            }
        }
        else if (result == Material.CRAFTER) {
            boolean isAllCraftingTable = true;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && item.getType() == Material.CRAFTING_TABLE && CustomItems.isCustomItem(item)) {
                    isAllCraftingTable = false;
                    break;
                }
            }
            if (!isAllCraftingTable) {
                event.getInventory().setResult(null);
            }
        }
    }

    @EventHandler
    private static void checkFurnace(FurnaceSmeltEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;
        if (recipe.getResult().getType() == Material.BAKED_POTATO) {
            if (event.getSource().getType() == Material.POTATO) {
                if (CustomItems.isCustomItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_CHICKEN) {
            if (event.getSource().getType() == Material.CHICKEN) {
                if (CustomItems.isCustomItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_COD) {
            if (event.getSource().getType() == Material.COD) {
                if (CustomItems.isCustomItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_PORKCHOP) {
            if (event.getSource().getType() == Material.PORKCHOP) {
                if (CustomItems.isCustomItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_BEEF) {
            if (event.getSource().getType() == Material.BEEF) {
                if (CustomItems.isCustomItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
