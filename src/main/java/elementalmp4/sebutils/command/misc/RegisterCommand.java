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
        // invalidate any existing token when generating a new registration OTP
        WebAuthService.invalidateTokenForUser(username);
        String otp = WebAuthService.generateOtpForUser(username);
        commandSender.sendMessage(Component.text("Registration OTP generated: ", NamedTextColor.GREEN)
                .append(Component.text(otp, NamedTextColor.AQUA)));
        commandSender.sendMessage(Component.text("Use this one-time password on the web login page to obtain your token.", NamedTextColor.GRAY));
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
