package me.Jedi.minimap.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("minimap|map|mm")
@Description("test command")
public class CommandMap extends BaseCommand {

    @Subcommand("test")
   public void onCommand(CommandSender sender) {
       if(sender instanceof Player) {
           sender.sendMessage("You're a player!");
       } else {
           sender.sendMessage("This command must be exectued by a player");
       }
       sender.sendMessage("This command actually executed. It's a mirace!");
   }

}
