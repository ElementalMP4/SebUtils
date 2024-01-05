package main.java.elementalmp4.utils;

import org.bukkit.entity.Player;

public class TeleportRequest {

    private final Player player;
    private final Player target;
    private final String authority;
    private final long expiryTime;

    public TeleportRequest(Player player, Player target, String authority) {
        this.player = player;
        this.target = target;
        this.authority = authority;
        this.expiryTime = System.currentTimeMillis() + 60000;
    }

    public void authorise() {
        player.teleport(target);
    }

    public String getAuthority() {
        return authority;
    }

    public boolean playerIsInvolved(String name) {
        return player.getName().equals(name) || target.getName().equals(name) || authority.equals(name);
    }

    public boolean expired() {
        return expiryTime < System.currentTimeMillis();
    }

}
