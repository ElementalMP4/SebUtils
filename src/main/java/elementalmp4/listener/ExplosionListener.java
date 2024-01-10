package main.java.elementalmp4.listener;

import main.java.elementalmp4.service.GlobalConfigService;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;

public class ExplosionListener implements Listener {
    @EventHandler
    public void onEntityExplodeEvent(ExplosionPrimeEvent e) {
        if (tntDisabled() && e.getEntityType().equals(EntityType.MINECART_TNT)) e.setCancelled(true);
    }

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        if (tntDisabled() && event.getVehicle().getType().equals(EntityType.MINECART_TNT)) {
            event.setCancelled(true);
        }
    }

    private boolean tntDisabled() {
        return !Boolean.parseBoolean(GlobalConfigService.getOrDefault("tnt_explodes", "true"));
    }
}
