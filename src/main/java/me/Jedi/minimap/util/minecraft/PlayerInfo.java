package me.Jedi.minimap.util.minecraft;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerInfo {
    Location prevLocation;
    Player player;

    public PlayerInfo(Location prevLocation, Player player) {
        this.prevLocation = prevLocation;
        this.player = player;
    }

    public Location getPrevLocation() {
        return prevLocation;
    }

    public Player getPlayer() {
        return player;
    }
}
