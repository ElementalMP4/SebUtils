package main.java.elementalmp4.command;

import main.java.elementalmp4.service.HomeService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (HomeService.userHasHome(commandSender.getName())) {
            commandSender.sendMessage("Taking you home...");
            HomeService.teleportUserHome(((Player)commandSender));
        } else {
            commandSender.sendMessage("You have not set a home yet!");
        }
        return true;
    }
}
