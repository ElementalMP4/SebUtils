package main.java.elementalmp4.sebutils.command.misc;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.EmptyTabCompleter;
import main.java.elementalmp4.sebutils.service.WebAuthService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

@SebUtilsCommand
public class RegisterCommand extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        String username = commandSender.getName();
        WebAuthService.invalidateTokenForUser(username);
        String otp = WebAuthService.generateOtpForUser(username);
        commandSender.sendMessage(Component.text("One-Time Password: ", NamedTextColor.GREEN)
                .append(Component.text(otp, NamedTextColor.AQUA)));
        commandSender.sendMessage(Component.text("Use this one-time password to access the web dashboard and map!", NamedTextColor.GRAY));
        return true;
    }

    @Override
    public String getCommandName() {
        return "register";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new EmptyTabCompleter();
    }
}
