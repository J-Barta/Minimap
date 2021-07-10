package me.Jedi.minimap.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class RandomListener implements Listener {

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        event.getBlockPlaced().setType(Material.DIAMOND_BLOCK);
    }
}
