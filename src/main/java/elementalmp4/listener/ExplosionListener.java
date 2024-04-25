package main.java.elementalmp4.listener;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.annotation.SebUtilsListener;
import main.java.elementalmp4.service.GlobalConfigService;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.Random;

@SebUtilsListener
public class ExplosionListener implements Listener {

    @EventHandler
    public void onEntityExplodeEvent(ExplosionPrimeEvent e) {
        if (tntDisabled()) {
            if (e.getEntityType().equals(EntityType.PRIMED_TNT)) {
                spawnFireworks(e.getEntity().getLocation(), 10, 10);
                e.setCancelled(true);
            } else if (e.getEntityType().equals(EntityType.CREEPER)) {
                spawnFireworks(e.getEntity().getLocation(), 6, 5);
                ((Creeper) e.getEntity()).setHealth(0);
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        if (tntDisabled() && event.getVehicle().getType().equals(EntityType.MINECART_TNT)) {
            event.setCancelled(true);
        }
    }

    private boolean tntDisabled() {
        return !GlobalConfigService.getAsBoolean(GlobalConfig.TNT_EXPLODES);
    }

    private void spawnFireworks(Location location, int amount, int power) {
        for (int i = 0; i < amount; i++) {
            Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.setPower(power);
            fwm.addEffect(FireworkEffect.builder().withColor(getRandomColour()).flicker(true).build());
            fw.setFireworkMeta(fwm);
            fw.detonate();
        }
    }

    private Color getRandomColour() {
        Random r = new Random();
        return Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255));
    }

}
