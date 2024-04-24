package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

@SebUtilsCommand
public class SmiteCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You must specify a player!");
            return true;
        }

        Player player = SebUtils.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(ChatColor.RED + "Player could not be found!");
            return true;
        }
        player.getWorld().strikeLightning(player.getLocation());
        player.setHealth(0);
        commandSender.sendMessage(ChatColor.YELLOW + player.getName() + ChatColor.RED + " has been smited");
        return true;
    }

    @Override
    public String getCommandName() {
        return "smite";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return null;
    }
}
