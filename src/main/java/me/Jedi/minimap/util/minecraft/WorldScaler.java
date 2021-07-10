package me.Jedi.minimap.util.minecraft;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.FallingBlock;

import java.text.MessageFormat;
import java.util.*;

import static org.bukkit.Material.*;

public class WorldScaler {
    static List<Material> IGNORE_BLOCKS = new ArrayList<Material>(Arrays.asList(
            AIR,
            OAK_LEAVES,
            SPRUCE_LEAVES,
            JUNGLE_LEAVES,
            ACACIA_LEAVES,
            AZALEA_LEAVES,
            BIRCH_LEAVES,
            DARK_OAK_LEAVES,
            FLOWERING_AZALEA_LEAVES));
    static Map<Material, Material> REPLACE_BLOCKS = new HashMap<Material, Material>() {{
        put(WATER, LAPIS_BLOCK);
    }};

    public static List<Material> FALLING_BLOCKS = new ArrayList<Material>(Arrays.asList(
            SAND,
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

        int highestValue = Collections.max(map.values());

        Material mode = DIAMOND_BLOCK;

        for(Map.Entry<Material, Integer> entry : map.entrySet()) {
            if(entry.getValue() == highestValue) {
                mode = entry.getKey();
            }
        }

        sender.sendMessage("Block chosen for chunk (" + chunk.getX() + ", " + chunk.getZ() + ")" + " is " + mode.name().toLowerCase());

        if(REPLACE_BLOCKS.containsKey(mode)) {
            mode = REPLACE_BLOCKS.get(mode);
        }

        return mode;
    }
}
