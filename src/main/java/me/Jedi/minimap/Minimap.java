package me.Jedi.minimap;

import co.aikar.commands.PaperCommandManager;
import de.erethon.dungeonsxl.api.DungeonsAPI;
import me.Jedi.minimap.command.CommandMap;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Minimap extends JavaPlugin {

    DungeonsAPI dxl;

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("DungeonsXL")) return;
        dxl = (DungeonsAPI) Bukkit.getPluginManager().getPlugin("DungeonsXL");

        PaperCommandManager manager = new PaperCommandManager(this);

        manager.registerCommand(new CommandMap());

    }
    @Override
    public void onDisable() {
        getLogger().info("Disabling Minimap...");
    }
}
