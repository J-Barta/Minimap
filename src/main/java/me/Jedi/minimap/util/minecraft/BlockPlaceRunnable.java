package me.Jedi.minimap.util.minecraft;

import me.Jedi.minimap.Minimap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;


public class BlockPlaceRunnable extends BukkitRunnable {

    Minimap plugin = null;
    Location location = null;
    Material block = null;

    public BlockPlaceRunnable(Minimap pl, Location location, Material block) {
        plugin = pl;
        this.location = location;
        this.block = block;
    }

    @Override
    public void run() {
        //This part already has access to the plugin and the blocks to place
        location.getWorld().getBlockAt(location).setType(block);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Material getBlock() {
        return block;
    }

    public void setBlock(Material block) {
        this.block = block;
    }
}
