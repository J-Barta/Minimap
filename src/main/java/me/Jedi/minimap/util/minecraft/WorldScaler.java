package me.Jedi.minimap.util.minecraft;

import me.Jedi.minimap.util.geometry.Point2d;
import me.Jedi.minimap.util.geometry.Rectangle;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.util.*;
import java.util.List;

import static me.Jedi.minimap.Minimap.pl;
import static org.bukkit.Material.*;

public class WorldScaler {
    public static HashMap<Material, Integer> totalDetectedBlocks = new HashMap<>();

    static List<Material> IGNORE_BLOCKS = new ArrayList<Material>(Arrays.asList(
            AIR,
            OAK_LEAVES,
            SPRUCE_LEAVES,
            JUNGLE_LEAVES,
            ACACIA_LEAVES,
            AZALEA_LEAVES,
            BIRCH_LEAVES,
            DARK_OAK_LEAVES,
            FLOWERING_AZALEA_LEAVES,
            GRASS,
            TALL_GRASS,
            VINE,
            RED_MUSHROOM_BLOCK,
            KELP,
            ROSE_BUSH,
            SEAGRASS,
            TALL_SEAGRASS,
            POPPY,
            LILY_OF_THE_VALLEY,
            CORNFLOWER,
            CAVE_AIR,
            LILAC,
            DANDELION,
            OXEYE_DAISY,
            SUGAR_CANE,
            PEONY,
            BROWN_MUSHROOM_BLOCK,
            RED_MUSHROOM,
            BROWN_MUSHROOM,
            KELP_PLANT));
    static Map<Material, Material> REPLACE_BLOCKS = new HashMap<Material, Material>() {{
        put(WATER, LAPIS_BLOCK);
        put(OAK_LOG, OAK_WOOD);
        put(DARK_OAK_LOG, DARK_OAK_WOOD);
        put(BIRCH_LOG, BIRCH_WOOD);
        put(JUNGLE_LOG, JUNGLE_WOOD);
        put(SPRUCE_LOG, SPRUCE_WOOD);
        put(OAK_STAIRS, OAK_PLANKS);
        put(DARK_OAK_STAIRS, DARK_OAK_PLANKS);
        put(BIRCH_STAIRS, BIRCH_PLANKS);
        put(JUNGLE_STAIRS, JUNGLE_PLANKS);
        put(SPRUCE_STAIRS, SPRUCE_PLANKS);
    }};

    static List<Material> OVERRIDE_BLOCKS = new ArrayList<>(Arrays.asList(
            DIRT_PATH
    ));

    public static List<Material> FALLING_BLOCKS = new ArrayList<Material>(Arrays.asList(
            SAND,
            RED_SAND,
            GRAVEL,
            BLACK_CONCRETE_POWDER,
            WHITE_CONCRETE_POWDER,
            YELLOW_CONCRETE_POWDER,
            PINK_CONCRETE_POWDER,
            MAGENTA_CONCRETE_POWDER,
            GRAY_CONCRETE_POWDER,
            BLUE_CONCRETE_POWDER,
            LIGHT_BLUE_CONCRETE_POWDER,
            LIGHT_GRAY_CONCRETE_POWDER,
            ORANGE_CONCRETE_POWDER,
            LIME_CONCRETE_POWDER,
            GREEN_CONCRETE_POWDER,
            CYAN_CONCRETE_POWDER,
            PURPLE_CONCRETE_POWDER,
            BROWN_CONCRETE_POWDER,
            RED_CONCRETE_POWDER
    ));

    public static Material getChunkMode(Chunk chunk, World world, CommandSender sender) {
        List<Material> materials = new ArrayList<>();
        int X = chunk.getX() * 16;
        int Z = chunk.getZ() * 16;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 255; y > 0; y--) {
                    Block currentBLock = world.getBlockAt(X+x, y, Z+z);
                    if (!IGNORE_BLOCKS.contains(currentBLock.getType())) {
                        materials.add(currentBLock.getType());
                        break;
                    }
                }
            }
        }

        List<Material> uniqueList = new ArrayList<>(new HashSet<>(materials));

        HashMap<Material, Integer> map = new HashMap<>();

        for(Material m : uniqueList) {
            map.put(m, Collections.frequency(materials, m));

            MessageFormat format = new MessageFormat("");
        }

        for(Map.Entry<Material, Integer> entry : map.entrySet()) {
            if(totalDetectedBlocks.containsKey(entry.getKey())) {
                totalDetectedBlocks.put(entry.getKey(), totalDetectedBlocks.get(entry.getKey()) + entry.getValue());
            } else {
                totalDetectedBlocks.put(entry.getKey(), entry.getValue());
            }
        }

        int highestValue = Collections.max(map.values());

        Material mode = DIAMOND_BLOCK;

        for(Map.Entry<Material, Integer> entry : map.entrySet()) {
            if(entry.getValue() == highestValue) {
                mode = entry.getKey();
            }
        }

        pl.getLogger().info("Block chosen for chunk (" + chunk.getX() + ", " + chunk.getZ() + ")" + " is " + mode.name().toLowerCase());

        if(REPLACE_BLOCKS.containsKey(mode)) {
            mode = REPLACE_BLOCKS.get(mode);
        }

        for(Material block : OVERRIDE_BLOCKS) {
            if(map.containsKey(block)) {
                mode = block;
                break;
            }
        }

        return mode;
    }

    public static PlayerInfo TeleportPlayerIntoMap(Player player, Plugin pl) {
        PlayerInfo playerInfo = new PlayerInfo(player.getLocation(), player);

        //Prepping the start location to put it inside the bounding box
        Location startLocation = player.getLocation();
        startLocation.setWorld(Bukkit.getWorld(pl.getConfig().getString("mapworld")));
        startLocation.setX(startLocation.getX()/16);
        startLocation.setY(pl.getConfig().getInt("map-y")+1);
        startLocation.setZ(startLocation.getZ()/16);

        int x1 = pl.getConfig().getInt("bounding-box.pos1.x") / 16;
        int z1 = pl.getConfig().getInt("bounding-box.pos1.z") / 16;
        int x2 = pl.getConfig().getInt("bounding-box.pos2.x") / 16;
        int z2 = pl.getConfig().getInt("bounding-box.pos2.z") / 16;

        Rectangle normalRectangle = new Rectangle(
                getBottomLeftCorner(x1, z1, x2, z2),
                getTopRight(x1, z1, x2, z2)
        );

        Location finalLocation = startLocation;

        // This is where we handle the eight different cases
        if(normalRectangle.getCorner1().getX() <= startLocation.getX()
                && startLocation.getX() <= normalRectangle.getCorner2().getX()
                && normalRectangle.getCorner1().getY() <= startLocation.getZ()
                && startLocation.getZ() <= normalRectangle.getCorner2().getY()) {
            //The player is inside the rectangle so we don't change the location
        } else {
            //The player is not inside the retangle.
            if(startLocation.getZ() > normalRectangle.getCorner2().getY() && startLocation.getX() < normalRectangle.getCorner1().getX()) {
                //Top left
                finalLocation.setX(normalRectangle.getCorner1().getX());
                finalLocation.setZ(normalRectangle.getCorner2().getY());
            } else if(startLocation.getX() >= normalRectangle.getCorner1().getX() && startLocation.getX() < normalRectangle.getCorner2().getX() && startLocation.getZ() > normalRectangle.getCorner2().getY()) {
                //Top

                finalLocation.setZ(normalRectangle.getCorner2().getY());
            }else if(startLocation.getZ() > normalRectangle.getCorner2().getY() && startLocation.getX() > normalRectangle.getCorner2().getX()) {
                //Top right

                finalLocation.setX(normalRectangle.getCorner2().getX());
                finalLocation.setZ(normalRectangle.getCorner2().getY());
            } else if(startLocation.getZ() >= normalRectangle.getCorner1().getY() && startLocation.getZ() <= normalRectangle.getCorner2().getY() && startLocation.getX() > normalRectangle.getCorner2().getX()) {
                //Right

                finalLocation.setX(normalRectangle.getCorner2().getX());
            } else if(startLocation.getX() > normalRectangle.getCorner2().getX() && startLocation.getZ() < normalRectangle.getCorner1().getY()) {
                //Bottom Right

                finalLocation.setX(normalRectangle.getCorner2().getX());
                finalLocation.setZ(normalRectangle.getCorner1().getY());
            } else if(startLocation.getX() >= normalRectangle.getCorner1().getX() && startLocation.getX() < normalRectangle.getCorner2().getX() && startLocation.getZ() < normalRectangle.getCorner1().getY()) {
                //Bottom

                finalLocation.setZ(normalRectangle.getCorner1().getY());
            } else if(startLocation.getX() < normalRectangle.getCorner1().getX() && startLocation.getZ() < normalRectangle.getCorner1().getY()) {
                //Bottom Left

                finalLocation.setX(normalRectangle.getCorner1().getX());
                finalLocation.setZ(normalRectangle.getCorner1().getY());
            } else if(startLocation.getZ() >= normalRectangle.getCorner1().getY() && startLocation.getZ() <= normalRectangle.getCorner2().getY() && startLocation.getX() < normalRectangle.getCorner1().getX()) {
                //left

                finalLocation.setX(normalRectangle.getCorner1().getX());
            }
        }

        player.teleport(finalLocation);

        return playerInfo;
    }

    static Point2d getBottomLeftCorner(int x1, int y1, int x2, int y2) {
        Point2d point = new Point2d(0, 0);
        point.setX(x1 > x2 ? x2 : x1);
        point.setY(y1 > y2 ? y2 : y1);

        return point;
    }

    static Point2d getTopRight(int x1, int y1, int x2, int y2) {
        Point2d point = new Point2d(0, 0);
        point.setX(x1 > x2 ? x1 : x2);
        point.setY(y1 > y2 ? y1 : y2);

        return point;
    }

    public static int getTopBlock(Location location) {
        location.getChunk().load();
        World world = location.getWorld();

        int value = 100;
        for(int i = 255; i > 0; i--) {
            if(world.getBlockAt((int) location.getX(), i, (int) location.getZ()).getType() != AIR) {
                value = i;
                break;
            }

        }
        return value;
    }

}
