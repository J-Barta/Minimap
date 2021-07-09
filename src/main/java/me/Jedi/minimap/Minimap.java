package me.Jedi.minimap;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import me.Jedi.minimap.command.CommandMap;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Minimap extends JavaPlugin {

    public static PaperCommandManager manager;
    public static MultiverseCore core;
    public static MVWorldManager worldManager;
    public static FileConfiguration config;
    public static LuckPerms lp;

    public static Minimap pl;

    @Override
    public void onEnable() {
        manager = new PaperCommandManager(this);
        core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
        worldManager = core.getMVWorldManager();
        config = this.getConfig();
        lp = LuckPermsProvider.get();

        setupConfig();

        setupACF();

        pl = Minimap.getPlugin(Minimap.class);

        this.saveDefaultConfig();
    }

    void setupConfig() {
        /*
        config.addDefault("mapworld", "NONE");
        config.addDefault("locations", new String[1]);
        config.options().copyDefaults(true);
        this.saveDefaultConfig();

         */
    }

    void setupACF() {
        manager.registerCommand(new CommandMap());

        manager.getCommandCompletions().registerCompletion("blocks", c -> {
            List<String> materials = new ArrayList<>();
            for(Material m : Material.values()) {
                getLogger().info(m.name());
                materials.add(m.name());
            }
            return ImmutableList.copyOf(materials);
        });

        manager.getCommandCompletions().registerCompletion("tilda", c -> {
            return ImmutableList.of("~");
        });

        manager.getCommandCompletions().registerCompletion("locations", c -> {
            return ImmutableList.copyOf(getConfig().getConfigurationSection("locations").getKeys(false));
        });

    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling Minimap...");
    }
}
