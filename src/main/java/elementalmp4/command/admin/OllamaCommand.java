package main.java.elementalmp4.command.admin;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.completer.OllamaTabCompleter;
import main.java.elementalmp4.service.GlobalConfigService;
import main.java.elementalmp4.service.OllamaService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Set;

@SebUtilsCommand
public class OllamaCommand extends AbstractCommand {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (args.length == 0) {
            boolean ollamaEnabled = GlobalConfigService.getAsBoolean(GlobalConfig.OLLAMA_ENABLED);
            commandSender.sendMessage("Ollama is currently " + format(ollamaEnabled));
            return true;
        }

        if (!Set.of("enable", "disable").contains(args[0])) {
            commandSender.sendMessage(ChatColor.RED + "You must specify enable or disable");
            return true;
        }

        boolean isEnabled = args[0].equals("enable");
        if (isEnabled) {
            OllamaService.startClient();
        } else {
            OllamaService.stopClient();
        }

        GlobalConfigService.set(GlobalConfig.OLLAMA_ENABLED, String.valueOf(isEnabled));
        commandSender.sendMessage("Ollama is now " + format(isEnabled));
        return true;
    }

    private String getFormattedHost() {
        return ChatColor.YELLOW + GlobalConfigService.getValue(GlobalConfig.OLLAMA_HOST);
    }

    private String format(boolean enabled) {
        return (enabled ? ChatColor.GREEN + "enabled" : ChatColor.RED + "disabled")
                + " and the host is " + getFormattedHost();
    }

    @Override
    public String getCommandName() {
        return "ollama";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new OllamaTabCompleter();
    }
}
