package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.completer.WebCompleter;
import main.java.elementalmp4.service.WebAuthService;
import main.java.elementalmp4.utils.CommandRunnable;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SebUtilsCommand
public class WebCommand extends AbstractCommand {

    private static String formatAuthCode(String input) {
        return input.substring(0, 4) + "-" + input.substring(4);
    }

    private static final CommandRunnable registerCommand = (CommandSender sender, List<String> args) -> {
        String authCode = WebAuthService.createSignUpRequest(sender.getName());
        String formattedAuthCode = formatAuthCode(authCode);
        sender.sendMessage(ChatColor.GOLD
                + "Enter your Minecraft username and this code to create an account on your dashboard: "
                + ChatColor.GREEN + formattedAuthCode);
    };

    private static final Map<String, CommandRunnable> VALID_COMMANDS = Map.of(
            "register", registerCommand
    );

    public static List<String> getValidCommands() {
        return new ArrayList<>(VALID_COMMANDS.keySet());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String alias, String[] args) {
        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You need to specify a subcommand!");
        } else {
            if (!VALID_COMMANDS.containsKey(args[0])) {
                commandSender.sendMessage(ChatColor.RED + "Command must be one of " + String.join(", ", getValidCommands()));
                return true;
            }
            CommandRunnable subCommand = VALID_COMMANDS.get(args[0]);
            subCommand.run(commandSender, List.of(args).subList(1, args.length));
        }
        return true;
    }

    @Override
    public String getCommandName() {
        return "web";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new WebCompleter();
    }
}
