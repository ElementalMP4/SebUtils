package main.java.elementalmp4.utils;

import main.java.elementalmp4.service.TeleportService;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeleportRequest extends TimedRequest {

    private final Player player;
    private final Player target;
    private final String authority;

    public TeleportRequest(Player player, Player target, String authority) {
        super(60000);
        this.player = player;
        this.target = target;
        this.authority = authority;
    }

    public void authorise() {
        player.teleport(target);
        TeleportService.playTeleportEffects(player);
        TeleportService.playTeleportEffects(target);
        player.sendMessage("Teleported " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " to " + ChatColor.YELLOW + target.getName());
        target.sendMessage("Teleported " + ChatColor.YELLOW + player.getName() + ChatColor.GREEN + " to " + ChatColor.YELLOW + target.getName());
    }

    public void deny(Player denier) {
        player.sendMessage(ChatColor.YELLOW + (denier.getName().equals(player.getName()) ? "You" : denier.getName())
                + ChatColor.RED + " denied teleport request");
        target.sendMessage(ChatColor.YELLOW + (denier.getName().equals(target.getName()) ? "You" : denier.getName())
                + ChatColor.RED + " denied teleport request");
    }

    public String getAuthority() {
        return authority;
    }

    public boolean playerIsInvolved(String name) {
        return player.getName().equals(name) || target.getName().equals(name) || authority.equals(name);
    }

    @Override
    public void whenExpired() {
        player.sendMessage(ChatColor.RED + "Teleport request expired");
        target.sendMessage(ChatColor.RED + "Teleport request expired");
    }
}
