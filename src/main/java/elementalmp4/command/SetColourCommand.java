package main.java.elementalmp4.command;

import main.java.elementalmp4.service.NicknameService;
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
            sender.sendMessage("Nickname colour must be one of the following: " + String.join(", ", NicknameService.getColourNamesList()));
            return true;
        }

        NicknameService.updateColour(sender.getName(), colour);
        sender.sendMessage("Updated colour to " + colour);

        return true;
    }
}
