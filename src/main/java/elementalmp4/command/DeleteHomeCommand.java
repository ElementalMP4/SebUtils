package main.java.elementalmp4.command;

import main.java.elementalmp4.service.HomeService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DeleteHomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        HomeService.deleteHome(commandSender.getName());
        commandSender.sendMessage(ChatColor.RED + "Deleted your home");
        return true;
    }
}
