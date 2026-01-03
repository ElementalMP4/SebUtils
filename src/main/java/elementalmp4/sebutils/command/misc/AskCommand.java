package main.java.elementalmp4.sebutils.command.misc;

import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.completer.EmptyTabCompleter;
import main.java.elementalmp4.sebutils.service.OllamaService;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

@SebUtilsCommand
public class AskCommand extends AbstractCommand {
    @Override
    public String getCommandName() {
        return "ask";
    }

    @Override
    public TabCompleter getTabCompleter() {
        return new EmptyTabCompleter();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!OllamaService.ollamaEnabled()) {
            commandSender.sendMessage(ChatColor.RED + "Ollama integration is disabled.");
            return true;
        }
        commandSender.sendMessage(ChatColor.GOLD + "Thinking...");
        String prompt = String.join(" ", args);
        OllamaService.askOllama(prompt, SebUtils.getPlugin().getServer(), commandSender.getName());

        return true;
    }
}
