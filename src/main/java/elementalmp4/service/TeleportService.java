package main.java.elementalmp4.service;

import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.utils.TeleportRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TeleportService {

    public static final TextComponent ACCEPT_COMPONENT = new TextComponent("" + ChatColor.GREEN + ChatColor.BOLD + "[ACCEPT]" + ChatColor.RESET);
    public static final TextComponent DENY_COMPONENT = new TextComponent("" + ChatColor.RED + ChatColor.BOLD + "[DENY]" + ChatColor.RESET);

    static {
        ACCEPT_COMPONENT.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        DENY_COMPONENT.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpdeny"));

        ACCEPT_COMPONENT.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to accept").create()));
        DENY_COMPONENT.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to deny").create()));
    }

    private static final List<TeleportRequest> requests = new ArrayList<>();

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(TeleportService::expireRequests, 0, 1, TimeUnit.SECONDS);
    }

    private static void expireRequests() {
        List<TeleportRequest> expired = requests.stream().filter(TeleportRequest::expired).toList();
        for (TeleportRequest t : expired) {
            requests.remove(t);
        }
    }

    public static void createNewTeleportRequest(TeleportRequest teleportRequest) {
        requests.add(teleportRequest);
    }

    public static boolean userIsAlreadyWaiting(String username) {
        return requests.stream().anyMatch(t -> t.playerIsInvolved(username));
    }

    public static void authoriseTeleport(String name) {
        TeleportRequest tr = requests.stream().filter(t -> t.getAuthority().equals(name)).findFirst().orElse(null);
        assert tr != null;
        tr.authorise();
        requests.remove(tr);
    }

    public static void denyTeleport(Player player) {
        TeleportRequest tr = requests.stream().filter(t -> t.getAuthority().equals(player.getName())).findFirst().orElse(null);
        assert tr != null;
        tr.deny(player);
        requests.remove(tr);
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

    public static void playTeleportEffects(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);
    }
}