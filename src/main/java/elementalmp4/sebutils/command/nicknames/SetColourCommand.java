package main.java.elementalmp4.sebutils.command.nicknames;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.ColourTabCompleter;
import main.java.elementalmp4.sebutils.service.NicknameService;
import net.kyori.adventure.text.Component;
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
            Component message = Component.text("Nickname colour must be one of the following: ", NamedTextColor.RED)
                    .append(Component.text(String.join(", ", NicknameService.getColourNamesList()), NamedTextColor.WHITE));
            sender.sendMessage(message);
            return true;
        }

        NicknameService.updateColour(sender.getName(), colour);
        Component message = Component.text("Updated colour to ", NamedTextColor.WHITE)
                .append(Component.text(colour, NicknameService.getColour(colour)));
        sender.sendMessage(message);
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
