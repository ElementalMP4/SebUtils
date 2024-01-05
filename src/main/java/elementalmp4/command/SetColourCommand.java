package main.java.elementalmp4.command;

import main.java.elementalmp4.service.NicknameService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetColourCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            return false;
        }

        String colour = args[0];
        if (!NicknameService.getColourNamesList().contains(colour)) {
            sender.sendMessage(ChatColor.RED + "Nickname colour must be one of the following: " +
                    ChatColor.RESET + String.join(", ", NicknameService.getColourNamesList()));
            return true;
        }

        NicknameService.updateColour(sender.getName(), colour);
        sender.sendMessage("Updated colour to " + NicknameService.getColourByNameAsChatColour(colour) + colour);
        return true;
    }
}
