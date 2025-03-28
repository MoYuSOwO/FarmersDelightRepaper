package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class Recipes implements Listener {

    public Recipes() {}

    @EventHandler
    private static void checkCrafting(PrepareItemCraftEvent event) {
        Recipe recipe = event.getRecipe();
        if (recipe == null) return;
        Material result = recipe.getResult().getType();
        if (result == Material.BEETROOT_SOUP || result == Material.RED_DYE) {
            boolean isAllBeetroot = true;
            for (ItemStack item : event.getInventory().getMatrix()) {
                if (item != null && item.getType() == Material.BEETROOT && Item.isNewItem(item)) {
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
                if (item != null && item.getType() == Material.WHEAT && Item.isNewItem(item)) {
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
                if (item != null && item.getType() == Material.CRAFTING_TABLE && Item.isNewItem(item)) {
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
                if (Item.isNewItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_CHICKEN) {
            if (event.getSource().getType() == Material.CHICKEN) {
                if (Item.isNewItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_COD) {
            if (event.getSource().getType() == Material.COD) {
                if (Item.isNewItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_PORKCHOP) {
            if (event.getSource().getType() == Material.PORKCHOP) {
                if (Item.isNewItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
        else if (recipe.getResult().getType() == Material.COOKED_BEEF) {
            if (event.getSource().getType() == Material.BEEF) {
                if (Item.isNewItem(event.getSource())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
