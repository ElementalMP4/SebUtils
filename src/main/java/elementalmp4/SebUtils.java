package main.java.elementalmp4;

import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.command.SebUtilsCommand;
import main.java.elementalmp4.listener.SebUtilsListener;
import main.java.elementalmp4.service.DatabaseService;
import main.java.elementalmp4.utils.ConsoleColours;
import main.java.elementalmp4.utils.ReflectiveInstantiator;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public class SebUtils extends JavaPlugin {

    private static JavaPlugin plugin;
    private static Logger logger;
    private static DatabaseService databaseService;

    public static Logger getPluginLogger() {
        return logger;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static DatabaseService getDatabaseService() {
        return databaseService;
    }

    @Override
    public void onEnable() {
        logger = getLogger();
        plugin = getPlugin(SebUtils.class);

        logger.info(ConsoleColours.RED + "   _____      __    __  ____  _ __    ");
        logger.info(ConsoleColours.YELLOW + "  / ___/___  / /_  / / / / /_(_) /____");
        logger.info(ConsoleColours.GREEN + "  \\__ \\/ _ \\/ __ \\/ / / / __/ / / ___/");
        logger.info(ConsoleColours.BLUE + " ___/ /  __/ /_/ / /_/ / /_/ / (__  ) ");
        logger.info(ConsoleColours.PURPLE + "/____/\\___/_.___/\\____/\\__/_/_/____/  ");
        logger.info("");

        if (!plugin.getDataFolder().exists()) {
            logger.info(ConsoleColours.YELLOW + "Creating data directory");
            plugin.getDataFolder().mkdir();
        }

        logger.info(ConsoleColours.YELLOW + "Initialising H2");
        databaseService = new DatabaseService(plugin.getDataFolder().getAbsolutePath());

        logger.info(ConsoleColours.YELLOW + "Registering commands");
        List<AbstractCommand> commands = new ReflectiveInstantiator<AbstractCommand>(SebUtilsCommand.class).getInstances();
        for (AbstractCommand command : commands) {
            PluginCommand pCommand = getCommand(command.getCommandName());
            pCommand.setExecutor(command);
            if (command.getTabCompleter() != null) pCommand.setTabCompleter(command.getTabCompleter());
        }

        logger.info(ConsoleColours.YELLOW + "Registering listeners");
        List<Listener> listeners = new ReflectiveInstantiator<Listener>(SebUtilsListener.class).getInstances();
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }

        logger.info(ConsoleColours.GREEN + "Ready!");
    }

    @Override
    public void onDisable() {
        logger.info("Stopped.");
        databaseService.close();
    }
}