package main.java.elementalmp4.sebutils.listener;

import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.PVPToggleService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

@SebUtilsListener
public class PVPListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player target)) return;

        Player attacker = resolveAttacker(event.getDamager());
        if (attacker == null) return;

        if (!GlobalConfigService.getAsBoolean(GlobalConfig.PVP_TOGGLE_ENABLED)) return;

        if (PVPToggleService.playerHasDisabledPvp(attacker.getName())) {
            attacker.sendMessage(Component.text(
                    "You cannot attack other players if you have disabled PVP!",
                    NamedTextColor.RED
            ));
            event.setCancelled(true);
        } else if (PVPToggleService.playerHasDisabledPvp(target.getName())) {
            attacker.sendMessage(Component.text(
                    target.getName() + " has PVP disabled!",
                    NamedTextColor.RED
            ));
            event.setCancelled(true);
        }
    }

    private Player resolveAttacker(Entity damager) {
        // Direct hit from another player
        if (damager instanceof Player player) {
            return player;
        }

        // Projectile (arrow, trident, snowball, etc)
        if (damager instanceof Projectile projectile) {
            Object shooter = projectile.getShooter();
            if (shooter instanceof Player player) {
                return player;
            }
        }

        return null;
    }
}
