package main.java.elementalmp4.service;

import main.java.elementalmp4.utils.PortalCreateRequest;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PortalService {
    private static final Map<String, PortalCreateRequest> requests = new HashMap<>();

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(PortalService::expireRequests, 0, 1, TimeUnit.SECONDS);
    }

    private static void expireRequests() {
        List<PortalCreateRequest> expired = requests.keySet().stream()
                .map(PortalService.requests::get)
                .filter(PortalCreateRequest::expired).toList();
        for (PortalCreateRequest t : expired) {
            requests.remove(t.getPlayerName());
        }
    }


    public static void handleBlazeRodClick(PlayerInteractEvent e) {
        if (requests.containsKey(e.getPlayer().getName())) {
            PortalCreateRequest request = requests.get(e.getPlayer().getName());
            boolean valid = validateRequest(request, e);
            if (valid) {
                createPortal(request, e);
            } else {
                e.getPlayer().sendMessage(ChatColor.RED + "You can't place both ends of a portal on the same block!");
            }
            requests.remove(e.getPlayer().getName());
        } else {
            PortalCreateRequest request = new PortalCreateRequest(e.getPlayer());
            requests.put(e.getPlayer().getName(), request);
            Location location = e.getPlayer().getLocation();
            e.getPlayer().sendMessage(ChatColor.GOLD + "Started a new portal at " + ChatColor.YELLOW + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
        }
    }

    private static void createPortal(PortalCreateRequest request, PlayerInteractEvent e) {

    }

    private static boolean validateRequest(PortalCreateRequest request, PlayerInteractEvent e) {
        return request.positionIsDifferent(e.getClickedBlock().getLocation());
    }

}
