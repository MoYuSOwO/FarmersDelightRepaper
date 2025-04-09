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
        ShapelessRecipe tomatoSeed = new ShapelessRecipe(new NamespacedKey(thisPlugin, "tomatoSeed"), Items.get(Items.TOMATO_SEED));
        tomatoSeed = tomatoSeed.addIngredient(new RecipeChoice.ExactChoice(Items.get(Items.TOMATO)));
        Bukkit.addRecipe(tomatoSeed);
        ShapelessRecipe rice = new ShapelessRecipe(new NamespacedKey(thisPlugin, "rice"), Items.get(Items.RICE));
        rice = rice.addIngredient(new RecipeChoice.ExactChoice(Items.get(Items.RICE_PANICLE)));
        Bukkit.addRecipe(rice);
        ShapelessRecipe tomatoSauce = new ShapelessRecipe(new NamespacedKey(thisPlugin, "tomatoSauce"), Items.get(Items.TOMATO_SAUCE));
        tomatoSauce = tomatoSauce.addIngredient(new RecipeChoice.ExactChoice(Items.get(Items.TOMATO)))
                .addIngredient(new RecipeChoice.ExactChoice(Items.get(Items.TOMATO)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.BOWL)));
        Bukkit.addRecipe(tomatoSauce);
        ShapelessRecipe rawPasta = new ShapelessRecipe(new NamespacedKey(thisPlugin, "rawPasta"), Items.get(Items.RAW_PASTA, 2));
        rawPasta = rawPasta.addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WATER_BUCKET)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)));
        Bukkit.addRecipe(rawPasta);
        ShapelessRecipe rawPasta1 = new ShapelessRecipe(new NamespacedKey(thisPlugin, "rawPasta1"), Items.get(Items.RAW_PASTA));
        rawPasta1 = rawPasta1.addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WATER_BUCKET)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)));
        Bukkit.addRecipe(rawPasta1);
        ShapelessRecipe rawPasta2 = new ShapelessRecipe(new NamespacedKey(thisPlugin, "rawPasta2"), Items.get(Items.RAW_PASTA));
        rawPasta2 = rawPasta2.addIngredient(new RecipeChoice.ExactChoice(new ItemStack(Material.EGG)))
                .addIngredient( new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)))
                .addIngredient( new RecipeChoice.ExactChoice(new ItemStack(Material.WHEAT)));
        Bukkit.addRecipe(rawPasta2);
        ShapelessRecipe dough = new ShapelessRecipe(new NamespacedKey(thisPlugin, "dough"), Items.get(Items.WHEAT_DOUGH, 3));
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
                if (item != null && item.getType() == Material.BEETROOT && Items.is(item)) {
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
                if (item != null && item.getType() == Material.WHEAT && Items.is(item)) {
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
                if (item != null && item.getType() == Material.CRAFTING_TABLE && Items.is(item)) {
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
                if (Items.is(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_CHICKEN) {
            if (event.getSource().getType() == Material.CHICKEN) {
                if (Items.is(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_COD) {
            if (event.getSource().getType() == Material.COD) {
                if (Items.is(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_PORKCHOP) {
            if (event.getSource().getType() == Material.PORKCHOP) {
                if (Items.is(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_BEEF) {
            if (event.getSource().getType() == Material.BEEF) {
                if (Items.is(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
