package main.java.elementalmp4.sebutils.utils;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface CommandRunnable {
    void run(CommandSender sender, List<String> args);
}