package main.java.elementalmp4.command.nicknames;

import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.command.SebUtilsCommand;
import main.java.elementalmp4.completer.ColourTabCompleter;
import main.java.elementalmp4.service.NicknameService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

@SebUtilsCommand
public class SetColourCommand extends AbstractCommand {
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

    @Override
    public String getCommandName() {
        return "namecolour";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new ColourTabCompleter();
    }
}
