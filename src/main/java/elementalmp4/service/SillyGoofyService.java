package main.java.elementalmp4.service;

import main.java.elementalmp4.GlobalConfig;
import org.bukkit.Particle;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Sheep;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;

public class SillyGoofyService {
    public static void tryExplodeCow(PlayerInteractEntityEvent e) {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.COWS_EXPLODE)) {
            e.getPlayer().getWorld().createExplosion(e.getRightClicked().getLocation(), 0, false, false);
            e.getPlayer().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, e.getPlayer().getLocation(), 1);
            ((Cow) e.getRightClicked()).setHealth(0);
        }
    }

    public static void trySmiteSheep(PlayerShearEntityEvent e) {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.SHEEP_SMITE)) {
            e.getPlayer().getWorld().strikeLightning(e.getEntity().getLocation());
            ((Sheep) e.getEntity()).setHealth(0);
        }
    }
}