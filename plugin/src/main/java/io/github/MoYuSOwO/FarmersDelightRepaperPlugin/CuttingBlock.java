package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public class CuttingBlock implements Listener {

    private static final TextComponent name = Component.text("砧板块").decoration(TextDecoration.ITALIC, false);
    private static final Set<Material> knife = Set.of(
            Material.WOODEN_SWORD,
            Material.STONE_SWORD,
            Material.IRON_SWORD,
            Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD
    );
    private static ArrayList<CuttingRecipe> recipes;
    private static final int KNIFE_SLOT = 11;
    private static final int INPUT_SLOT = 20;
    private static final int[] OUTPUT_SLOT = new int[]{14, 15, 23, 24};

    public CuttingBlock() {
        recipes = new ArrayList<>();
        recipes.add(new CuttingRecipe(new ItemStack(Material.BEEF), new ItemStack[]{new ItemStack(Material.BONE_MEAL), Item.getItemStack(Items.MINCED_BEEF, 2)}));
        recipes.add(new CuttingRecipe(Item.getItemStack(Items.WHEAT_DOUGH), new ItemStack[]{Item.getItemStack(Items.RAW_PASTA)}));
        recipes.add(new CuttingRecipe(new ItemStack(Material.PUMPKIN), new ItemStack[]{Item.getItemStack(Items.PUMPKIN_SLICE, 4)}));
        recipes.add(new CuttingRecipe(Item.getItemStack(Items.CABBAGE), new ItemStack[]{Item.getItemStack(Items.CABBAGE_LEAF, 2)}));
        recipes.add(new CuttingRecipe(new ItemStack(Material.CHICKEN), new ItemStack[]{Item.getItemStack(Items.RAW_CHICKEN_CUTS, 2), new ItemStack(Material.BONE_MEAL)}));
        recipes.add(new CuttingRecipe(new ItemStack(Material.COD), new ItemStack[]{Item.getItemStack(Items.RAW_COD_SLICE, 2), new ItemStack(Material.BONE_MEAL)}));
        recipes.add(new CuttingRecipe(new ItemStack(Material.SALMON), new ItemStack[]{Item.getItemStack(Items.RAW_SALMON_SLICE, 2), new ItemStack(Material.BONE_MEAL)}));
        recipes.add(new CuttingRecipe(new ItemStack(Material.PORKCHOP), new ItemStack[]{Item.getItemStack(Items.RAW_BACON, 2), new ItemStack(Material.BONE_MEAL)}));
    }

    private static boolean isSameItemStack(ItemStack itemStack1, ItemStack itemStack2) {
        ItemStack clone1 = itemStack1.clone();
        ItemStack clone2 = itemStack2.clone();
        clone1.setAmount(1);
        clone2.setAmount(1);
        return clone1.equals(clone2);
    }

    public static boolean isCuttingBlock(Block block) {
        if (block.getType() != Material.OAK_LEAVES) return false;
        Leaves data = (Leaves) block.getBlockData();
        return data.getDistance() == 2 && data.isPersistent();
    }

    private static void updateInventoryView(InventoryView inventoryView) {
        Inventory inventory = inventoryView.getTopInventory();
        for (int i = 0; i < 4; i++) {
            inventory.setItem(OUTPUT_SLOT[i], null);
        }
        if (inventory.getItem(KNIFE_SLOT) == null || !knife.contains(inventory.getItem(KNIFE_SLOT).getType())) return;
        if (inventory.getItem(INPUT_SLOT) == null) return;
        ItemStack item = inventory.getItem(INPUT_SLOT);
        for (int i = 0; i < recipes.size(); i++) {
            CuttingRecipe recipe = recipes.get(i);
            if (isSameItemStack(item, recipe.input())) {
                for (int j = 0; j < recipe.output().length; j++) {
                    inventory.setItem(OUTPUT_SLOT[j], recipe.output()[j]);
                }
            }
        }
    }

    @EventHandler
    public void openCuttingBlockGUI(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!isCuttingBlock(event.getClickedBlock())) return;
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        Inventory inv = Bukkit.createInventory(event.getPlayer(), 4 * 9, name);
        ItemStack exitItem = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.setCustomModelData(628002);
        exitMeta.customName(Component.empty());
        exitItem.setItemMeta(exitMeta);
        inv.setItem(0, exitItem);
        event.getPlayer().openInventory(inv);
    }

    @EventHandler
    public void placeItemIntoInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = player.getOpenInventory();
        Inventory inventory = inventoryView.getTopInventory();
        if (!inventoryView.title().equals(name)) return;
        if (event.getClickedInventory() != inventory && event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            ItemStack click = inventoryView.getItem(event.getRawSlot());
            if (click == null) return;
            event.setCancelled(true);
            if (knife.contains(click.getType())) {
                if (inventory.getItem(KNIFE_SLOT) == null) {
                    inventory.setItem(KNIFE_SLOT, click);
                    click.setAmount(0);
                }
                else if (isSameItemStack(inventory.getItem(KNIFE_SLOT), click)) {
                    if (click.getAmount() + inventory.getItem(KNIFE_SLOT).getAmount() <= inventory.getItem(KNIFE_SLOT).getMaxStackSize()) {
                        inventory.getItem(KNIFE_SLOT).setAmount(click.getAmount() + inventory.getItem(KNIFE_SLOT).getAmount());
                    }
                    else {
                        int t = inventory.getItem(KNIFE_SLOT).getMaxStackSize() - inventory.getItem(KNIFE_SLOT).getAmount();
                        inventory.getItem(KNIFE_SLOT).setAmount(inventory.getItem(KNIFE_SLOT).getMaxStackSize());
                        click.setAmount(click.getAmount() - t);
                    }
                }
                else {
                    ItemStack itemStack = click.clone();
                    inventoryView.setItem(event.getRawSlot(), inventory.getItem(KNIFE_SLOT));
                    inventory.setItem(KNIFE_SLOT, itemStack);
                }
            }
            else {
                if (inventory.getItem(INPUT_SLOT) == null) {
                    inventory.setItem(INPUT_SLOT, click);
                    click.setAmount(0);
                }
                else if (isSameItemStack(inventory.getItem(INPUT_SLOT), click)) {
                    if (click.getAmount() + inventory.getItem(INPUT_SLOT).getAmount() <= inventory.getItem(INPUT_SLOT).getMaxStackSize()) {
                        inventory.getItem(INPUT_SLOT).setAmount(click.getAmount() + inventory.getItem(INPUT_SLOT).getAmount());
                    }
                    else {
                        int t = inventory.getItem(INPUT_SLOT).getMaxStackSize() - inventory.getItem(INPUT_SLOT).getAmount();
                        inventory.getItem(INPUT_SLOT).setAmount(inventory.getItem(INPUT_SLOT).getMaxStackSize());
                        click.setAmount(click.getAmount() - t);
                    }
                }
                else {
                    ItemStack itemStack = click.clone();
                    inventoryView.setItem(event.getRawSlot(), inventory.getItem(INPUT_SLOT));
                    inventory.setItem(INPUT_SLOT, itemStack);
                }
            }
            updateInventoryView(inventoryView);
        }
        else if (event.getClickedInventory() == inventory && event.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
            if (event.getRawSlot() != KNIFE_SLOT && event.getRawSlot() != INPUT_SLOT) {
                event.setCancelled(true);
            }
            else {
                ItemStack itemStack = event.getCursor().clone();
                player.setItemOnCursor(inventory.getItem(event.getRawSlot()));
                inventory.setItem(event.getRawSlot(), itemStack);
                updateInventoryView(inventoryView);
                event.setCancelled(true);
            }
        }
        else if (event.getClickedInventory() == inventory && event.getAction() == InventoryAction.PLACE_ALL) {
            if (event.getRawSlot() != KNIFE_SLOT && event.getRawSlot() != INPUT_SLOT) {
                event.setCancelled(true);
            }
            else {
                if (inventory.getItem(event.getRawSlot()) == null) {
                    inventory.setItem(event.getRawSlot(), event.getCursor());
                    event.setCancelled(true);
                    event.getCursor().setAmount(0);
                }
                else {
                    inventory.getItem(event.getRawSlot()).setAmount(inventory.getItem(event.getRawSlot()).getAmount() + event.getCursor().getAmount());
                    event.setCancelled(true);
                    event.getCursor().setAmount(0);
                }
                updateInventoryView(inventoryView);
            }
        }
        else if (event.getClickedInventory() == inventory && event.getAction() == InventoryAction.PLACE_SOME) {
            if (event.getRawSlot() != KNIFE_SLOT && event.getRawSlot() != INPUT_SLOT) {
                event.setCancelled(true);
            }
            else {
                ItemStack inv = inventory.getItem(event.getRawSlot());
                int placeAmount = inv.getMaxStackSize() - inv.getAmount();
                event.setCancelled(true);
                event.getCursor().setAmount(event.getCursor().getAmount() - placeAmount);
                inv.setAmount(inv.getMaxStackSize());
                updateInventoryView(inventoryView);
            }
        }
        else if (event.getClickedInventory() == inventory && event.getAction() == InventoryAction.PLACE_ONE) {
            if (event.getRawSlot() != KNIFE_SLOT && event.getRawSlot() != INPUT_SLOT) {
                event.setCancelled(true);
            }
            else {
                if (inventory.getItem(event.getRawSlot()) == null) {
                    ItemStack inv = event.getCursor().clone();
                    inv.setAmount(1);
                    inventory.setItem(event.getRawSlot(), inv);
                    event.setCancelled(true);
                    event.getCursor().setAmount(event.getCursor().getAmount() - 1);
                }
                else {
                    inventory.getItem(event.getRawSlot()).setAmount(inventory.getItem(event.getRawSlot()).getAmount() + 1);
                    event.setCancelled(true);
                    event.getCursor().setAmount(event.getCursor().getAmount() - 1);
                }
                updateInventoryView(inventoryView);
            }
        }
    }

    @EventHandler
    public void pickItemFromInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = player.getOpenInventory();
        Inventory inventory = inventoryView.getTopInventory();
        if (!inventoryView.title().equals(name)) return;
        if (event.getClickedInventory() != inventory) return;
        final Set<Integer> output = Set.of(
                OUTPUT_SLOT[0],
                OUTPUT_SLOT[1],
                OUTPUT_SLOT[2],
                OUTPUT_SLOT[3]
        );
        if (!output.contains(event.getRawSlot()) && event.getRawSlot() != INPUT_SLOT && event.getRawSlot() != KNIFE_SLOT) {
            event.setCancelled(true);
            return;
        }
        else if (output.contains(event.getRawSlot())) {
            ItemStack clickedItem = inventory.getItem(event.getRawSlot());
            if (clickedItem == null) return;
            inventory.getItem(INPUT_SLOT).setAmount(inventory.getItem(INPUT_SLOT).getAmount() - 1);
            event.setCancelled(true);
            for (int i = 0; i < 4; i++) {
                ItemStack outputItem = inventory.getItem(OUTPUT_SLOT[i]);
                if (outputItem == null) continue;
                player.give(outputItem);
                outputItem.setAmount(0);
            }
            ItemStack knife = inventory.getItem(KNIFE_SLOT);
            Damageable knifeDamage = (Damageable) knife.getItemMeta();
            knifeDamage.setDamage(knifeDamage.getDamage() + 2);
            knife.setItemMeta(knifeDamage);
            updateInventoryView(inventoryView);
        }
        else {
            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                event.setCancelled(true);
                player.setItemOnCursor(inventory.getItem(event.getRawSlot()));
                inventory.setItem(event.getRawSlot(), null);
            }
            else if (event.getAction() == InventoryAction.PICKUP_HALF || event.getAction() == InventoryAction.PICKUP_SOME) {
                event.setCancelled(true);
                int giveAmount = inventory.getItem(event.getRawSlot()).getAmount() / 2;
                inventory.getItem(event.getRawSlot()).setAmount(inventory.getItem(event.getRawSlot()).getAmount() - giveAmount);
                player.setItemOnCursor(inventory.getItem(event.getRawSlot()));
                event.getCursor().setAmount(giveAmount);
            }
            else if (event.getAction() == InventoryAction.PICKUP_ONE) {
                event.setCancelled(true);
                inventory.getItem(event.getRawSlot()).setAmount(inventory.getItem(event.getRawSlot()).getAmount() - 1);
                player.setItemOnCursor(inventory.getItem(event.getRawSlot()));
                event.getCursor().setAmount(1);
            }
            updateInventoryView(inventoryView);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        InventoryView inventoryView = player.getOpenInventory();
        if (inventoryView.title() != name) return;
        if (inventoryView.getTopInventory().getItem(KNIFE_SLOT) != null) {
            player.give(inventoryView.getTopInventory().getItem(KNIFE_SLOT));
        }
        if (inventoryView.getTopInventory().getItem(INPUT_SLOT) != null) {
            player.give(inventoryView.getTopInventory().getItem(INPUT_SLOT));
        }
    }

    @EventHandler
    public void onCuttingBlockPlace(BlockPlaceEvent event) {
        if (!Item.isNewItem(event.getItemInHand(), Items.CUTTING_BLOCK)) return;
        Block target = event.getBlockPlaced();
        target.setType(Material.OAK_LEAVES);
        Leaves targetData = (Leaves) target.getBlockData();
        targetData.setPersistent(true);
        targetData.setDistance(2);
        target.setBlockData(targetData);
        target.getState().update();
    }

    @EventHandler
    public void onCuttingBlockBreak(BlockBreakEvent event) {
        if (!isCuttingBlock(event.getBlock())) return;
        event.setDropItems(false);
        event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), Item.getItemStack(Items.CUTTING_BLOCK));
    }
}
