package main.java.elementalmp4.sebutils.command.nicknames;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.ColourTabCompleter;
import main.java.elementalmp4.sebutils.service.NicknameService;
import net.kyori.adventure.text.format.NamedTextColor;
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
            sender.sendMessage(NamedTextColor.RED + "Nickname colour must be one of the following: " +
                    NamedTextColor.WHITE + String.join(", ", NicknameService.getColourNamesList()));
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
