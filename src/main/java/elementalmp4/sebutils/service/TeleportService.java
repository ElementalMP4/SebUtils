package main.java.elementalmp4.sebutils.service;

import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.entity.TeleportRequest;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TeleportService {

    public static final Component ACCEPT_COMPONENT = Component.text("[ACCEPT]", NamedTextColor.GREEN, TextDecoration.BOLD)
            .clickEvent(ClickEvent.runCommand("/tpaccept"))
            .hoverEvent(HoverEvent.showText(Component.text("Click to accept teleport request")));
    public static final Component DENY_COMPONENT = Component.text("[DENY]", NamedTextColor.RED, TextDecoration.BOLD)
            .clickEvent(ClickEvent.runCommand("/tpdeny"))
            .hoverEvent(HoverEvent.showText(Component.text("Click to deny teleport request")));

    private static final List<TeleportRequest> requests = new ArrayList<>();

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("tp"));
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
            commandSender.sendMessage(Component.text("You cannot send a teleport request whilst you have pending incoming or outgoing teleport requests", NamedTextColor.RED));
            return Optional.empty();
        }

        if (args.length == 0) {
            commandSender.sendMessage(Component.text("You must specify another player!", NamedTextColor.RED));
            return Optional.empty();
        }

        Player player = SebUtils.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            commandSender.sendMessage(Component.text("Player could not be found!", NamedTextColor.RED));
            return Optional.empty();
        }

        if (player.getName().equals(commandSender.getName())) {
            commandSender.sendMessage(Component.text("You cannot teleport to yourself!", NamedTextColor.RED));
            return Optional.empty();
        }

        if (TeleportService.userIsAlreadyWaiting(player.getName())) {
            commandSender.sendMessage(Component.text("That player already has a pending teleport request!", NamedTextColor.RED));
            return Optional.empty();
        }

        return Optional.of(player);
    }

    public static void playTeleportEffects(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 10, 1);

        int particles = 80;
        double radius = 0.8;
        double height = 2.0;

        for (int i = 0; i < particles; i++) {
            double angle1 = (4 * Math.PI / particles) * i;
            double angle2 = angle1 + Math.PI;

            double y = (i / (double) particles) * height;

            double x1 = Math.cos(angle1) * radius;
            double z1 = Math.sin(angle1) * radius;

            double x2 = Math.cos(-angle2) * radius;
            double z2 = Math.sin(-angle2) * radius;

            Location particle1 = player.getLocation().clone().add(x1, y, z1);
            Location particle2 = player.getLocation().clone().add(x2, y, z2);

            player.spawnParticle(
                    Particle.COPPER_FIRE_FLAME,
                    particle1,
                    1,
                    0, 0, 0,
                    0
            );

            player.spawnParticle(
                    Particle.SOUL_FIRE_FLAME,
                    particle2,
                    1,
                    0, 0, 0,
                    0
            );
        }
    }
}