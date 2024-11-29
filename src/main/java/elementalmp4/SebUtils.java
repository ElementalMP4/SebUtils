package main.java.elementalmp4;

import main.java.elementalmp4.annotation.SebUtilsCommand;
import main.java.elementalmp4.annotation.SebUtilsListener;
import main.java.elementalmp4.command.AbstractCommand;
import main.java.elementalmp4.service.DatabaseService;
import main.java.elementalmp4.service.DiscordService;
import main.java.elementalmp4.service.GlobalConfigService;
import main.java.elementalmp4.service.WebService;
import main.java.elementalmp4.utils.ConsoleColours;
import main.java.elementalmp4.utils.ReflectiveInstantiator;
import org.bukkit.ChatColor;
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

        logger.info(ConsoleColours.YELLOW + "Initialising Global Config");
        GlobalConfigService.initialiseGlobalConfig();

        logger.info(ConsoleColours.YELLOW + "Registering commands");
        List<AbstractCommand> commands = new ReflectiveInstantiator<AbstractCommand>("main.java.elementalmp4")
                .findAnnotatedClasses(SebUtilsCommand.class, AbstractCommand.class)
                .getInstances();
        for (AbstractCommand command : commands) {
            PluginCommand pCommand = getCommand(command.getCommandName());
            pCommand.setExecutor(command);
            if (command.getTabCompleter() != null) pCommand.setTabCompleter(command.getTabCompleter());
        }

        logger.info(ConsoleColours.YELLOW + "Registering listeners");
        List<Listener> listeners = new ReflectiveInstantiator<Listener>("main.java.elementalmp4")
                .findAnnotatedClasses(SebUtilsListener.class, Listener.class)
                .getInstances();
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }

        boolean discord = DiscordService.connectDiscordOnBoot();
        if (discord) logger.info(ConsoleColours.YELLOW + "Connected to Discord");

        WebService.startServer();
        logger.info(ConsoleColours.GREEN + "Ready!" + ConsoleColours.RESET);
    }

    @Override
    public void onDisable() {
        DiscordService.close(true);
        logger.info("Stopped.");
        databaseService.close();
    }
}