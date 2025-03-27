package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import it.unimi.dsi.fastutil.Hash;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class CookingPot implements Listener {

    public CookingPot() {}

    private static final TextComponent name = Component.text("厨锅").decoration(TextDecoration.ITALIC, false);
    private static final boolean[] canPlace = new boolean[]{
            false, true, true, true, false, false, false, false, false,
            false, true, true, true, false, false, false, false, false,
            false, false, false, false, true, false, false, false, false,
            false, false, false, false, false, false, false, false, false,
    };

    public static boolean isCookingPot(Block block) {
        if (block.getType() != Material.OAK_LEAVES) return false;
        Leaves data = (Leaves) block.getBlockData();
        if (data.getDistance() == 3 && data.isPersistent()) return true;
        return false;
    }

    @EventHandler
    public void openCookingPotGUI(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!isCookingPot(event.getClickedBlock())) return;
        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);
        Inventory inv = Bukkit.createInventory(event.getPlayer(), 4 * 9, name);
        ItemStack exitItem = new ItemStack(Material.BARRIER);
        ItemMeta exitMeta = exitItem.getItemMeta();
        exitMeta.setCustomModelData(628001);
        exitMeta.customName(Component.empty());
        exitItem.setItemMeta(exitMeta);
        inv.setItem(0, exitItem);
        event.getPlayer().openInventory(inv);
    }

    @EventHandler
    public void protectGUI(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryView inventoryView = player.getOpenInventory();
        if (inventoryView.title().equals(name)) {
            if (event.getRawSlot() == 24 || event.getRawSlot() == 6) {
                event.setCancelled(true);
                inventoryView.close();
            }
            if (event.getRawSlot() >= 0 && event.getRawSlot() <= 35 && !canPlace[event.getRawSlot()]){
                event.setCancelled(true);
            }
            if (canPlace[event.getRawSlot()]) {

            }
        }
    }

    @EventHandler
    public void onCookingPotPlace(BlockPlaceEvent event) {
        if (!Item.isNewItem(event.getItemInHand(), Items.COOKING_POT)) return;
        Block target = event.getBlockPlaced();
        target.setType(Material.OAK_LEAVES);
        Leaves targetData = (Leaves) target.getBlockData();
        targetData.setPersistent(true);
        targetData.setDistance(3);
        target.setBlockData(targetData);
        target.getState().update();
    }

    @EventHandler
    public void onCookingPotBreak(BlockBreakEvent event) {
        if (!isCookingPot(event.getBlock())) return;
    }
}
