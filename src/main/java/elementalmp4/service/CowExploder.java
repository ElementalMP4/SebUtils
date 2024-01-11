package main.java.elementalmp4.service;

import org.bukkit.Particle;
import org.bukkit.entity.Cow;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class CowExploder {
    public static void tryExplodeCow(PlayerInteractEntityEvent e) {
        if (GlobalConfigService.getOrDefault("cows_explode", false)) {
            e.setCancelled(true);
            e.getPlayer().getWorld().createExplosion(e.getRightClicked().getLocation(), 0, false, false);
            e.getPlayer().getWorld().spawnParticle(Particle.EXPLOSION_HUGE, e.getPlayer().getLocation(), 1);
            ((Cow) e.getRightClicked()).setHealth(0);
        }
    }
}