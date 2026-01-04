package main.java.elementalmp4.sebutils;

import main.java.elementalmp4.sebutils.annotation.SebUtilsCommand;
import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.command.AbstractCommand;
import main.java.elementalmp4.sebutils.service.DatabaseService;
import main.java.elementalmp4.sebutils.service.DiscordService;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.OllamaService;
import main.java.elementalmp4.sebutils.utils.ConsoleColours;
import main.java.elementalmp4.sebutils.utils.ReflectiveInstantiator;
import main.java.elementalmp4.sebutils.web.WebServer;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public class SebUtils extends JavaPlugin {

    private static JavaPlugin plugin;
    private static Logger logger;

    public static Logger getPluginLogger() {
        return logger;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
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

        logger.info(ConsoleColours.YELLOW + "Initialising Database" + ConsoleColours.RESET);
        DatabaseService.connect();

        logger.info(ConsoleColours.YELLOW + "Starting web server" + ConsoleColours.RESET);
        String bind = GlobalConfigService.getValue(GlobalConfig.WEB_BIND);
        int port = GlobalConfigService.getAsInteger(GlobalConfig.WEB_PORT);
        WebServer.start(bind, port);

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

        boolean discord = DiscordService.connectDiscordOnBoot();
        if (discord) logger.info(ConsoleColours.YELLOW + "Connected to Discord" + ConsoleColours.RESET);

        boolean ollama = OllamaService.startOllamaOnBoot();
        if (ollama) logger.info(ConsoleColours.YELLOW + "Started Ollama Client" + ConsoleColours.RESET);

        logger.info(ConsoleColours.GREEN + "Ready!" + ConsoleColours.RESET);
    }

    @Override
    public void onDisable() {
        DiscordService.close(true);
        WebServer.stop();
        DatabaseService.close();
        logger.info("Stopped cleanly");
    }
}