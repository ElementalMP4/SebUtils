package main.java.elementalmp4;

import main.java.elementalmp4.command.admin.*;
import main.java.elementalmp4.command.homes.DeleteHomeCommand;
import main.java.elementalmp4.command.homes.HomeCommand;
import main.java.elementalmp4.command.homes.HomesCommand;
import main.java.elementalmp4.command.homes.SetHomeCommand;
import main.java.elementalmp4.command.nicknames.SetColourCommand;
import main.java.elementalmp4.command.nicknames.SetNicknameCommand;
import main.java.elementalmp4.command.plots.*;
import main.java.elementalmp4.command.tpa.TeleportAcceptCommand;
import main.java.elementalmp4.command.tpa.TeleportCommand;
import main.java.elementalmp4.command.tpa.TeleportDenyCommand;
import main.java.elementalmp4.command.tpa.TeleportHereCommand;
import main.java.elementalmp4.completer.*;
import main.java.elementalmp4.listener.*;
import main.java.elementalmp4.service.DatabaseService;
import main.java.elementalmp4.utils.ConsoleColours;
import org.bukkit.plugin.java.JavaPlugin;

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
        getCommand("nickname").setExecutor(new SetNicknameCommand());
        getCommand("namecolour").setExecutor(new SetColourCommand());
        getCommand("sethome").setExecutor(new SetHomeCommand());
        getCommand("home").setExecutor(new HomeCommand());
        getCommand("delhome").setExecutor(new DeleteHomeCommand());
        getCommand("tpa").setExecutor(new TeleportCommand());
        getCommand("tpahere").setExecutor(new TeleportHereCommand());
        getCommand("tpaccept").setExecutor(new TeleportAcceptCommand());
        getCommand("tpdeny").setExecutor(new TeleportDenyCommand());
        getCommand("homes").setExecutor(new HomesCommand());
        getCommand("allowtnt").setExecutor(new AllowTntCommand());
        getCommand("listhomes").setExecutor(new ListHomesCommand());
        getCommand("cowsexplode").setExecutor(new CowsExplodeCommand());
        getCommand("sheepsmite").setExecutor(new SheepSmiteCommand());
        getCommand("showconfig").setExecutor(new ShowConfigCommand());
        getCommand("plots").setExecutor(new PlotsCommand());
        getCommand("deleteplot").setExecutor(new DeletePlotCommand());
        getCommand("plot").setExecutor(new PlotCommand());
        getCommand("grantpermit").setExecutor(new GrantPermitCommand());
        getCommand("revokepermit").setExecutor(new RevokePermitCommand());
        getCommand("permits").setExecutor(new PermitsCommand());
        getCommand("smite").setExecutor(new SmiteCommand());
        getCommand("enablebilly").setExecutor(new EnableBillyCommand());
        getCommand("adminplotoverride").setExecutor(new AdminPlotOverride());

        logger.info(ConsoleColours.YELLOW + "Registering autofill");
        getCommand("namecolour").setTabCompleter(new ColourTabCompleter());
        getCommand("home").setTabCompleter(new HomesTabCompleter());
        getCommand("delhome").setTabCompleter(new HomesTabCompleter());
        getCommand("allowtnt").setTabCompleter(new BooleanTabCompleter());
        getCommand("cowsexplode").setTabCompleter(new BooleanTabCompleter());
        getCommand("sheepsmite").setTabCompleter(new BooleanTabCompleter());
        getCommand("grantpermit").setTabCompleter(new PermitCompleter());
        getCommand("revokepermit").setTabCompleter(new PermitCompleter());
        getCommand("permits").setTabCompleter(new PlotCompleter());
        getCommand("deleteplot").setTabCompleter(new PlotCompleter());
        getCommand("enablebilly").setTabCompleter(new BooleanTabCompleter());
        getCommand("adminplotoverride").setTabCompleter(new BooleanTabCompleter());

        logger.info(ConsoleColours.YELLOW + "Registering listeners");
        getServer().getPluginManager().registerEvents(new ChatInterceptor(), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerMoveListener(), this);
        getServer().getPluginManager().registerEvents(new ExplosionListener(), this);
        getServer().getPluginManager().registerEvents(new AnimalInteractionListener(), this);
        getServer().getPluginManager().registerEvents(new PlotListener(), this);
        getServer().getPluginManager().registerEvents(new VillagerInteractionListener(), this);

        logger.info(ConsoleColours.GREEN + "Ready!");
    }

    @Override
    public void onDisable() {
        logger.info("Stopped.");
        databaseService.close();
    }
}