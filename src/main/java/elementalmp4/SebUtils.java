package main.java.elementalmp4;

import main.java.elementalmp4.command.*;
import main.java.elementalmp4.listener.ChatInterceptor;
import main.java.elementalmp4.listener.PlayerJoinListener;
import main.java.elementalmp4.service.DatabaseService;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class SebUtils extends JavaPlugin {

    private static JavaPlugin plugin;
    private static Logger logger;
    private static DatabaseService databaseService;

    @Override
    public void onEnable() {
        logger = getLogger();
        plugin = getPlugin(SebUtils.class);
        logger.info("Starting...");

        if (!plugin.getDataFolder().exists()) {
            logger.info("Creating data directory");
            plugin.getDataFolder().mkdir();
        }

        logger.info("Initialising H2");
        databaseService = new DatabaseService(plugin.getDataFolder().getAbsolutePath());

        logger.info("Registering commands");
        getCommand("nickname").setExecutor(new SetNicknameCommand());
        getCommand("namecolour").setExecutor(new SetColourCommand());
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("home").setExecutor(new HomeCommand());
        getCommand("delhome").setExecutor(new DeleteHomeCommand());
        getCommand("tpa").setExecutor(new TeleportCommand());
        getCommand("tpahere").setExecutor(new TeleportHereCommand());
        getCommand("tpaccept").setExecutor(new TeleportAcceptCommand());
        getCommand("tpdeny").setExecutor(new TeleportDenyCommand());

        logger.info("Registering listeners");
        getServer().getPluginManager().registerEvents(new ChatInterceptor(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);

        logger.info("Ready!");
    }

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
    public void onDisable() {
        logger.info("Stopped.");
    }
}