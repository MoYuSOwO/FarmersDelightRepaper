package io.github.MoYuSOwO.FarmersDelightRepaperPlugin;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class CuttingBlock implements Listener {

    public CuttingBlock() {}

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
}
