package main.java.elementalmp4.sebutils.listener;

import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.ban.BanListType;
import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.PendingAccessService;
import main.java.elementalmp4.sebutils.utils.StatusCache;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static main.java.elementalmp4.sebutils.SebUtils.getPlugin;
import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;
import static main.java.elementalmp4.sebutils.modules.DiscordModule.getBodyUrl;

public class DiscordListener extends ListenerAdapter {

    private static final long PROFILE_LOOKUP_TIMEOUT_SECONDS = 5;
    private final JDA jda;

    public DiscordListener(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        // Ignore webhooks
        if (event.isWebhookMessage()) {
            return;
        }

        // Ignore self
        if (event.getAuthor().getId().equals(jda.getSelfUser().getId())) {
            return;
        }

        // Only listen to messages in specific channel
        if (!event.getChannel().getId().equals(GlobalConfigService.getValue(GlobalConfig.DISCORD_CHAT_CHANNEL))) {
            return;
        }

        forwardDiscordMessage(event.getAuthor().getName(), event.getMessage().getContentStripped());
    }

    @Override
    public void onButtonInteraction(@Nonnull ButtonInteractionEvent event) {
        // Ignore if the approval system has been turned off
        if (!GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_APPROVAL_ENABLED)) {
            return;
        }

        // Ignore if the button press didn't come from the access request channel
        if (!event.getChannel().getId().equals(GlobalConfigService.getValue(GlobalConfig.DISCORD_ACCESS_REQUEST_CHANNEL))) {
            return;
        }

        // Ignore if it's not from a server member
        if (event.getMember() == null) {
            return;
        }

        // Ignore if they don't have the approval role
        if (!hasApprovalRole(event.getMember())) {
            return;
        }

        // Let discord know we will update the message soon
        event.deferEdit().queue();

        String id = event.getComponentId();
        UUID userId = UUID.fromString(id.split(":")[3]);
        if (id.startsWith("sebutils:access:approve")) {
            approveAccessRequest(userId, event);
            cleanup(userId);
        } else if (id.startsWith("sebutils:access:deny")) {
            denyAccessRequest(userId, event);
            cleanup(userId);
        } else {
            getPluginLogger().warning("Unknown interaction type: " + id);
        }
    }

    private void cleanup(UUID userId) {
        PendingAccessService.removePendingRequest(userId);
        StatusCache.refresh();
    }

    private void approveAccessRequest(UUID uuid, ButtonInteractionEvent event) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            player.setWhitelisted(true);
            Bukkit.reloadWhitelist();
        });

        PlayerProfile profile = Bukkit.createProfile(uuid);
        profile.update()
                .orTimeout(PROFILE_LOOKUP_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .handle((updated, ex) -> {
                    String name = (ex == null && updated.getName() != null) ? updated.getName() : uuid.toString();
                    if (ex != null) {
                        getPlugin().getLogger().warning("Profile lookup failed for " + uuid + ": " + ex.getMessage());
                    }

                    MessageEmbed approvedEmbed = new EmbedBuilder()
                            .setColor(Color.GREEN)
                            .setTitle("Access Granted")
                            .setImage(getBodyUrl(uuid))
                            .setDescription("**" + name + "** has been granted access to the server")
                            .build();
                    event.getMessage().editMessageEmbeds(approvedEmbed).setComponents().queue();
                    return null;
                });
    }

    private void denyAccessRequest(UUID uuid, ButtonInteractionEvent event) {
        PlayerProfile profile = Bukkit.createProfile(uuid);

        Bukkit.getScheduler().runTask(getPlugin(), () -> {
            Bukkit.getBanList(BanListType.PROFILE).addBan(
                    profile,
                    "Your access request was rejected.",
                    (Instant) null,
                    "Server Access Requests"
            );
        });

        profile.update()
                .orTimeout(PROFILE_LOOKUP_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .handle((updated, ex) -> {
                    String name = (ex == null && updated.getName() != null) ? updated.getName() : uuid.toString();
                    if (ex != null) {
                        getPlugin().getLogger().warning("Profile lookup failed for " + uuid + ": " + ex.getMessage());
                    }

                    MessageEmbed deniedEmbed = new EmbedBuilder()
                            .setColor(Color.RED)
                            .setTitle("Access Denied")
                            .setImage(getBodyUrl(uuid))
                            .setDescription("**" + name + "** has been denied access to the server")
                            .setFooter(uuid.toString())
                            .build();
                    event.getMessage().editMessageEmbeds(deniedEmbed).setComponents().queue();
                    return null;
                });
    }

    private boolean hasApprovalRole(Member member) {
        String roleId = GlobalConfigService.getValue(GlobalConfig.DISCORD_ACCESS_APPROVAL_ROLE);
        return member.getRoles().stream().map(ISnowflake::getId).collect(Collectors.toSet()).contains(roleId);
    }

    private void forwardDiscordMessage(String name, String contentStripped) {
        Component component = Component
                .text("[Discord] ", NamedTextColor.BLUE)
                .append(net.kyori.adventure.text.Component.text("[%s] ".formatted(name), NamedTextColor.YELLOW))
                .append(Component.text(contentStripped, NamedTextColor.WHITE));
        SebUtils.getPlugin().getServer().broadcast(component);
    }
}