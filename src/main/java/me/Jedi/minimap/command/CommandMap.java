package me.Jedi.minimap.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
import io.papermc.lib.PaperLib;
import me.Jedi.minimap.util.minecraft.PlayerInfo;
import me.Jedi.minimap.util.minecraft.WorldScaler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static me.Jedi.minimap.Minimap.*;
import static me.Jedi.minimap.util.minecraft.WorldScaler.*;
import static org.bukkit.Material.*;

@CommandAlias("minimap|map|m")
@Description("Minimap base command")
public class CommandMap extends BaseCommand {
    //TODO: Implement permissions :D
    //TODO: Make it so that players being stupid with their config won't throw errors
    List<PlayerInfo> playerInfos = new ArrayList<>();

    Map<Location, Material> blocksToPlace = new HashMap<>();


    @Default
    public void base(CommandSender sender, String[] args) {
        //This actually loads the map
        if(args.length == 0) {
            if (sender instanceof Player) {
                //Send the player to the map world
            } else {
                sender.sendMessage("You must be a player to execute this command");
            }
        } else {
            //Send the named player to the map world
        }
    }

    @Subcommand("leave")
    public void leave(CommandSender sender) {

    }

    @CommandCompletion("@blocks")
    @Subcommand("setblock|sb")
    public void setBlock(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            if(args.length > 0) {
                Player player = null;
                if(args.length == 1) {
                    player = (Player) sender;
                } else if(args.length == 2) {
                    player = Bukkit.getPlayer(args[2]);
                    if(player == null) {
                        sender.sendMessage("Couldn't find " + args[2] + "!");
                        return;
                    }
                }
                Material block = matchMaterial(args[0]);

                player.sendBlockChange(player.getTargetBlock(null, 5).getLocation(),
                        block.createBlockData());

                sender.sendMessage("Targeted Block changed to a " + block.name());
            } else {
                sender.sendMessage("Please send a valid block to be placed on a client");
            }
        } else {
            sender.sendMessage("You must be a player to perform this action");
        }
    }

    @Subcommand("setmap")
    public void setMap(CommandSender sender, String[] args) {
        if(args.length > 0) {
            String name = args[0];
            List<MultiverseWorld> worlds = new ArrayList<>(worldManager.getMVWorlds());
            List<String> worldNames = new ArrayList<>();
            for(MultiverseWorld w : worlds) {
                worldNames.add(w.getName());
            }
            if(worldNames.contains(name)) {
                pl.getConfig().set("mapworld", name);
                pl.saveConfig();
                sender.sendMessage(name + " has been set to the minimap world!");
            } else {
                sender.sendMessage("§4[" + name + "]is not a valid world name!");
            }
        } else {
            sender.sendMessage("You need to name a multiverse world.");
        }
    }

    @Subcommand("currentmap")
    public void currentMap(CommandSender sender, String[] args) {
        String map = pl.getConfig().getString("mapworld");
        if(map != "NONE") {
            sender.sendMessage("The current world for the minimap is " + map);
        } else {
            sender.sendMessage("You haven't set a world for the minimap");
        }
    }

    @Subcommand("create")
    public void createMap(CommandSender sender, String[] args) {
        if(args.length > 0) {
            //Make sure that the world doesn't already exist
            List<MultiverseWorld> worlds = new ArrayList<>(worldManager.getMVWorlds());
            List<String> worldNames = new ArrayList<>();
            for(MultiverseWorld w : worlds) {
                worldNames.add(w.getName());
            }

            if(!worldNames.contains(args[0])) {
                sender.sendMessage("Creating your map world...");
                worldManager.addWorld(args[0], //World name
                        World.Environment.NORMAL, // environment
                        null, // seed
                        WorldType.FLAT, // worldtype
                        false, // structures
                        null // generator
                );

                MultiverseWorld world = worldManager.getMVWorld(args[0]);
                world.setGameMode(GameMode.ADVENTURE);
                world.setAllowFlight(true);
                world.setHunger(false);
                world.setBedRespawn(false);
                world.setAutoLoad(true);
                world.setAllowAnimalSpawn(false);
                world.setAllowMonsterSpawn(false);

                pl.getConfig().set("worldname", args[0]);
                pl.getConfig().options().copyDefaults();
                pl.saveConfig();
                sender.sendMessage("World created and saved as " + args[0]);
            } else {
                sender.sendMessage("There is already a world named [" + args[0] + "]!");
            }
        }else {
            sender.sendMessage("You need to give a name of the world you want to create.");
        }
    }

    @Subcommand("createcopy|createscalecopy")
    @CommandCompletion("@new_world @x1 @x2 @x2 @z2 @scale @y_level @worlds @nothing")
    public void createScaleCopy(CommandSender sender, String[] args) {
        //args: worldname x1 z1 x2 z2 scale y-level originalname
        if(args.length >= 8) {
            //Create a scaled version of the world
            String newName = args[0];
            int x1 = Integer.parseInt(args[1]);
            int z1 = Integer.parseInt(args[2]);
            int x2 = Integer.parseInt(args[3]);
            int z2 = Integer.parseInt(args[4]);

            x1 = x1 > 0 ? (x1+15)/16 * 16 : (x1-15)/16*16;
            z1 = z1 > 0 ? (z1+15)/16 * 16 : (z1-15)/16*16;
            x2 = x2 > 0 ? (x2+15)/16 * 16 : (x2-15)/16*16;
            z2 = z2 > 0 ? (z2+15)/16 * 16 : (z2-15)/16*16;

            double scale = Double.parseDouble(args[5]);
            int yLevel = Integer.parseInt(args[6]);
            World fromWorld = Bukkit.getWorld(args[7]);

            if(!isWorld(newName)) {
                Player p = (Player) sender;

                int finalX1 = x1;
                int finalX2 = x2;
                int finalZ1 = z1;
                int finalZ2 = z2;

                sender.sendMessage("Creating your world");
                worldManager.addWorld(newName, World.Environment.NORMAL, null, WorldType.FLAT, false, null);

                worldManager.getMVWorld(newName).setGameMode(GameMode.ADVENTURE);
                worldManager.getMVWorld(newName).setAllowFlight(true);
                worldManager.getMVWorld(newName).setHunger(false);
                worldManager.getMVWorld(newName).setBedRespawn(false);
                worldManager.getMVWorld(newName).setAutoLoad(true);
                worldManager.getMVWorld(newName).setAllowAnimalSpawn(false);
                worldManager.getMVWorld(newName).setAllowMonsterSpawn(false);
                worldManager.getMVWorld(newName).setSpawnLocation(new Location(Bukkit.getWorld(newName), (x1+x2)/2, yLevel+1, (z1+z2)/2));

                sender.sendMessage("World created! Beginning the scaling process (this might take a while)");
                p.teleport(new Location(Bukkit.getWorld(newName), finalX1 /16, yLevel+5, finalX2 /16));

                blocksToPlace.clear();

                BukkitRunnable placeLoop = new BukkitRunnable() {

                    @Override
                    public void run() {
                        for(Map.Entry<Location, Material> entry : blocksToPlace.entrySet()) {
                            entry.getKey().getWorld().getBlockAt(entry.getKey()).setType(entry.getValue());
                        }
                    }
                };

                new BukkitRunnable() {
                    @Override
                    public void run() {

                        //Go through each chunk systematically and save each chosen block in multidimensional List (is that even a thing)
                        double xDirection = finalX1 > finalX2 ? -16 : 16;
                        double zDirection = finalZ1 > finalZ2 ? -16 : 16;

                        int currentX = finalX1;
                        int currentZ = finalZ1;

                        boolean doneZ = false;
                        boolean doneX = false;

                        World newWorld = Bukkit.getWorld(newName);

                        Chunk mapChunk;
                        Chunk prevMapChunk = new Location(newWorld, finalX2, 0, finalZ2).getChunk();

                        while(!doneZ) {
                            while(!doneX) {
                                Chunk currentChunk = new Location(fromWorld, currentX, 0, currentZ).getChunk();
                                //PaperLib.getChunkAtAsync(new Location(fromWorld, currentX, 0, currentZ), true);
                                Material block = getChunkMode(currentChunk, fromWorld, sender);

                                mapChunk = new Location(newWorld, currentX/16, 0, currentZ/16).getChunk();
                                if(mapChunk != prevMapChunk) {
                                    PaperLib.getChunkAtAsync(new Location(newWorld, currentX/16, 0, currentZ/16));
                                }

                                prevMapChunk = mapChunk;

                                if(FALLING_BLOCKS.contains(block)) {
                                    sender.sendMessage(block.name().toLowerCase(Locale.ROOT) + " is a falling block! Placing stone under it");
                                    blocksToPlace.put(new Location(newWorld, currentX/16, yLevel, currentZ/16), STONE);
                                }

                                blocksToPlace.put(new Location(newWorld, currentX/16, yLevel, currentZ/16), block);

                                currentX += xDirection;

                                if(xDirection > 0 && currentX >= finalX2) {
                                    doneX = true;
                                } else if(xDirection < 0 && currentX <= finalX2){
                                    doneX = true;
                                }
                            }

                            doneX = false;
                            currentX = finalX1;
                            currentZ += zDirection;

                            if(zDirection > 0 && currentZ >= finalZ2) {
                                doneZ = true;
                            } else if(zDirection < 0 && currentZ <= finalZ2){
                                doneZ = true;
                            }

                        }

                        sender.sendMessage("World generation complete!");

                        this.cancel();
                    }

                    @Override
                    public void cancel() {
                        sender.sendMessage("Placing in blocks now!");
                        placeLoop.runTask(pl);
                        super.cancel();
                    }
                }.runTaskLaterAsynchronously(pl, 200L);


            }else {
                sender.sendMessage("You've entered the name of a world that already exists");
            }

        } else {
            sender.sendMessage("You haven't entered enough arguments");
        }
    }

    @Subcommand("createLocation|cl|create")
    @CommandCompletion("@nothing @tilda @tilda @tilda @worlds @tilda @tilda @tilda @nothing")
    public void createLocation(CommandSender sender, String[] args) {
        if(args.length >= 8) {
            String name = args[0];
            int destX = Integer.parseInt(args[1]);
            int destY = Integer.parseInt(args[2]);
            int destZ = Integer.parseInt(args[3]);
            String worldName = args[4];
            int startX = Integer.parseInt(args[5]);
            int startY = Integer.parseInt(args[5]);
            int startZ = Integer.parseInt(args[5]);

            sender.sendMessage("Creating new location...");
            sender.sendMessage("---Details---");
            sender.sendMessage("Name: " + name);
            sender.sendMessage("Destination: " + "(" + destX + ", " + destY + ", " + destZ + ") in world " + worldName);
            sender.sendMessage("Location in map world: (" + startX + ", " + startY + ", " + startZ + ")");

            if(isWorld(worldName)) {
                FileConfiguration config = pl.getConfig();
                config.options().copyDefaults(true);
                config.createSection("locations." + name);
                config.createSection("locations." + name + ".destination");
                config.createSection("locations." + name + ".map-pos");
                config.addDefault("locations." + name + ".destination.x", destX);
                config.addDefault("locations." + name + ".destination.y", destY);
                config.addDefault("locations." + name + ".destination.z", destZ);
                config.addDefault("locations." + name + ".destination.world", worldName);
                config.addDefault("locations." + name + ".map-pos.x", startX);
                config.addDefault("locations." + name + ".map-pos.y", startY);
                config.addDefault("locations." + name + ".map-pos.z", startZ);
                pl.saveConfig();
                sender.sendMessage("Location successfully created and saved!");
            } else {
                sender.sendMessage("You selected an invalid world!");
                return;
            }

        } else {
            sender.sendMessage("You haven't entered enough arguments. The correct form for this command is:");
            sender.sendMessage("mm create <name> <x> <y> <z> <world> <x> <y> <z>");
        }
    }

    @Subcommand("locationdetails")
    public void locationDetails(CommandSender sender, String[] args) {
        /*
        for(String locs : pl.getConfig().getConfigurationSection("locations").getKeys(false)) {
            // note String locs is either "example1" or "example2"
            Location location = new Location(Bukkit.getWorld(pl.getConfig().getString("locations." + locs + ".destination.world")), pl.getConfig().getInt("locations." + locs + ".destination.x"), pl.getConfig().getInt("locations." + locs + ".destination.y"),pl. getConfig().getInt("locations." + locs + ".destination.z"));
            // do something here with the location
            sender.sendMessage("Location " +locs + " detected at (" + location.getX() + ", " + location.getY() + ", "
            + location.getZ() + ")");
        }
         */

        if(args.length > 0) {
            String name = args[0];
            String path = "locations." + name;
            if(pl.getConfig().getConfigurationSection("locations").getKeys(false).contains(name)) {
                sender.sendMessage("---Location '" + name + "'---");
                sender.sendMessage("Destination: (" + pl.getConfig().getInt(path + "destination.x")
                        + ", " + pl.getConfig().getInt(path + "destination.y") + ", " +
                        pl.getConfig().getInt(path + "destination.z") + ") in world '" +
                        pl.getConfig().getString(path + "destination.world"));
                sender.sendMessage("Source location: (" + pl.getConfig().getInt(path + "map-pos.x") + ", " +
                        pl.getConfig().getInt(path + "map-pos.y") + ", " +
                        pl.getConfig().getInt(path + "map-pos.z") + ") in the map world");
            } else {
                sender.sendMessage("You haven't entered a valid location");
            }
        } else {
            sender.sendMessage("Please provide the name of the location you'd like information about");
        }
    }

    @Subcommand("locations")
    public void locations(CommandSender sender) {
        sender.sendMessage("---Locations---");
        for(String loc : pl.getConfig().getConfigurationSection("locations").getKeys(false)) {
            sender.sendMessage(loc);
        }
        sender.sendMessage("Use /minimap locationdetails <location> for details about that location");
    }

    @Subcommand("reload")
    public void reload(CommandSender sender) {
        pl.reloadConfig();
        sender.sendMessage("Reloaded the config");
    }

    @Subcommand("save")
    public void save(CommandSender sender) {
        pl.saveConfig();
        sender.sendMessage("Saved the config");
    }

    public boolean isWorld(String name) {
        List<String> names = new ArrayList<>();
        for(MultiverseWorld w : worldManager.getMVWorlds()) {
            names.add(w.getName());
        }

        return names.contains(name);
    }

    public void sendPlayerToMap(Player player) {
        playerInfos.add(new PlayerInfo(player.getLocation(), player));

        Location teleportLocation = player.getLocation();

        //Todo: Finish what was being done here.
    }

    public boolean outsideBoundingBox(Location location) {

        //TODO: Finish what was bein done here
        return false;
    }

    @Subcommand("chunkinfo")
    public void ChunkInfo(CommandSender sender) {
        if(sender instanceof Player) {
            getChunkMode(((Player) sender).getLocation().getChunk(), ((Player) sender).getWorld(), sender);
        } else {
            sender.sendMessage("You must be a player to perform this action!");
        }
    }

}
