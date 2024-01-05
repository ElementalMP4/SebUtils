package main.java.elementalmp4.utils;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.service.TeleportService;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

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

    public static Optional<Player> validateTeleportRequest(CommandSender commandSender, String[] args) {
        if (TeleportService.userIsAlreadyWaiting(commandSender.getName())) {
            commandSender.sendMessage(ChatColor.RED + "You cannot send a teleport request whilst you have pending incoming or outgoing teleport requests");
            return Optional.empty();
        }

        if (args.length == 0) {
            commandSender.sendMessage(ChatColor.RED + "You must specify another player!");
            return Optional.empty();
        }

        Player player = SebUtils.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(ChatColor.RED + "Player could not be found!");
            return Optional.empty();
        }

        if (player.getName().equals(commandSender.getName())) {
            commandSender.sendMessage(ChatColor.RED + "You cannot teleport to yourself!");
            return Optional.empty();
        }

        if (TeleportService.userIsAlreadyWaiting(player.getName())) {
            commandSender.sendMessage(ChatColor.RED + "That player already has a pending teleport request!");
            return Optional.empty();
        }

        return Optional.of(player);
    }

}
