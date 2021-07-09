package me.Jedi.minimap.util.minecraft;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Blocks {

    public String normalName(Material material) {
        ItemStack itemStack = new ItemStack(material);
        return itemStack.getItemMeta().getDisplayName();
    }

}
