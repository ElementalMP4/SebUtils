package main.java.elementalmp4.utils;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PortalCreateRequest extends TimedRequest {

    private final Player player;
    private final int x;
    private final int y;
    private final int z;

    public PortalCreateRequest(Player player) {
        super(3 * 60 * 60);
        this.player = player;
        this.x = player.getLocation().getBlockX();
        this.y = player.getLocation().getBlockY();
        this.z = player.getLocation().getBlockZ();
    }

    public boolean positionIsDifferent(Location location) {
        return location.getBlockX() != x || location.getBlockY() != y || location.getBlockZ() != z;

    }

    public String getPlayerName() {
        return this.player.getName();
    }

    @Override
    public void whenExpired() {
        player.sendMessage(ChatColor.RED + "Portal creation request expired");
    }
}
