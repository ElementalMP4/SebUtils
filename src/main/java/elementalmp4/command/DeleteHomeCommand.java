package main.java.elementalmp4.command;

import main.java.elementalmp4.service.HomeService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        String homeName = args.length == 0 ? "default" : args[0];
        if (HomeService.userHasHome(commandSender.getName(), homeName)) {
            HomeService.deleteHome(commandSender.getName(), homeName);
            commandSender.sendMessage(ChatColor.RED + "Deleted your home " + ChatColor.YELLOW + homeName);
        } else {
            commandSender.sendMessage(ChatColor.RED + "You don't have a home by that name, or you haven't set a default home yet!");
        }
        return true;
    }
}
