package main.java.elementalmp4.sebutils;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.modules.*;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.utils.ConsoleColours;
import main.java.elementalmp4.sebutils.utils.ReflectiveInstantiator;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

public class SebUtils extends JavaPlugin {

    private static JavaPlugin plugin;
    private static Logger logger;
    private static ModuleManager moduleManager;

    public static Logger getPluginLogger() {
        return logger;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static ModuleManager getModuleManager() {
        return moduleManager;
    }

    public static Connection getDatabaseConnection() {
        return moduleManager.get(DatabaseModule.class).getConnection();
    }

    @Override
    public void onEnable() {
        logger = getLogger();
        plugin = getPlugin(SebUtils.class);

        logger.info(ConsoleColours.RED + "   _____      __    __  ____  _ __    " + ConsoleColours.RESET);
        logger.info(ConsoleColours.YELLOW + "  / ___/___  / /_  / / / / /_(_) /____" + ConsoleColours.RESET);
        logger.info(ConsoleColours.GREEN + "  \\__ \\/ _ \\/ __ \\/ / / / __/ / / ___/" + ConsoleColours.RESET);
        logger.info(ConsoleColours.BLUE + " ___/ /  __/ /_/ / /_/ / /_/ / (__  ) " + ConsoleColours.RESET);
        logger.info(ConsoleColours.PURPLE + "/____/\\___/_.___/\\____/\\__/_/_/____/  " + ConsoleColours.RESET);
        logger.info("");

        if (!plugin.getDataFolder().exists()) {
            logger.info(ConsoleColours.YELLOW + "Creating data directory" + ConsoleColours.RESET);
            plugin.getDataFolder().mkdir();
        }

        logger.info(ConsoleColours.YELLOW + "Initialising Global Config" + ConsoleColours.RESET);
        GlobalConfigService.initialiseGlobalConfig();

        moduleManager = new ModuleManager();
        moduleManager.register(DatabaseModule.class, DatabaseModule::new);
        moduleManager.register(DiscordModule.class, DiscordModule::new, GlobalConfig.DISCORD_ENABLED);
        moduleManager.register(WebServerModule.class, WebServerModule::new, GlobalConfig.WEB_ENABLED);
        moduleManager.register(OllamaModule.class, OllamaModule::new, GlobalConfig.OLLAMA_ENABLED);

        logger.info(ConsoleColours.YELLOW + "Registering commands");
        List<AbstractCommand> commands = new ReflectiveInstantiator<AbstractCommand>("main.java.elementalmp4.sebutils.command")
                .findAnnotatedClasses(SebUtilsCommand.class, AbstractCommand.class)
                .getInstances();
        for (AbstractCommand command : commands) {
            PluginCommand pCommand = getCommand(command.getCommandName());
            pCommand.setExecutor(command);
            if (command.getTabCompleter() != null) pCommand.setTabCompleter(command.getTabCompleter());
        }

        logger.info(ConsoleColours.YELLOW + "Registering listeners");
        List<Listener> listeners = new ReflectiveInstantiator<Listener>("main.java.elementalmp4.sebutils.listener")
                .findAnnotatedClasses(SebUtilsListener.class, Listener.class)
                .getInstances();
        for (Listener listener : listeners) {
            getServer().getPluginManager().registerEvents(listener, this);
        }

        logger.info(ConsoleColours.GREEN + "Ready!" + ConsoleColours.RESET);
    }

    @Override
    public void onDisable() {
        moduleManager.stopAll();
        logger.info("Stopped successfully!");
    }
}