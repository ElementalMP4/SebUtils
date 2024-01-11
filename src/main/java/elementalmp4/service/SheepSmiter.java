package main.java.elementalmp4.service;

import main.java.elementalmp4.GlobalConfig;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class SheepSmiter {
    public static void trySmiteSheep(PlayerShearEntityEvent e) {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.SHEEP_SMITE)) {
            e.getPlayer().getWorld().strikeLightning(e.getEntity().getLocation());
            ((Sheep) e.getEntity()).setHealth(0);
        }
    }
}
