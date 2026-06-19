package main.java.elementalmp4.sebutils.modules;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.destroystokyo.paper.profile.PlayerProfile;
import io.papermc.paper.ban.BanListType;
import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.PendingAccessService;
import main.java.elementalmp4.sebutils.utils.ConsoleColours;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;
import main.java.elementalmp4.sebutils.utils.StatusCache;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static main.java.elementalmp4.sebutils.SebUtils.getPlugin;
import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class DiscordModule extends AbstractModule {

    private static final String HEAD_URL_FORMAT = "https://api.mineatar.io/head/%s?scale=16";
    private static final String FACE_URL_FORMAT = "https://api.mineatar.io/face/%s?scale=16";
    private static final String BODY_URL_FORMAT = "https://api.mineatar.io/body/full/%s?scale=4";
    private static final String WEBHOOK_NAME = "sebutils_chat";

    private JDA jda;
    private WebhookClient webhookClient;

    private String getHeadUrl(Player player) {
        return HEAD_URL_FORMAT.formatted(player.getUniqueId().toString());
    }

    private String getFaceUrl(Player player) {
        return FACE_URL_FORMAT.formatted(player.getUniqueId().toString());
    }

    private String getBodyUrl(UUID uuid) {
        return BODY_URL_FORMAT.formatted(uuid.toString());
    }

    public void sendJoinMessage(Player player) {
        if (!GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
            return;
        }
        String channelId = GlobalConfigService.getValue(GlobalConfig.DISCORD_CHAT_CHANNEL);
        MessageEmbed joinEmbed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setAuthor(player.getName() + " joined the game", getHeadUrl(player), getHeadUrl(player))
                .build();
        sendEmbed(joinEmbed, channelId);
    }

    public void sendLeaveMessage(Player player) {
        if (!GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
            return;
        }
        String channelId = GlobalConfigService.getValue(GlobalConfig.DISCORD_CHAT_CHANNEL);
        MessageEmbed joinEmbed = new EmbedBuilder()
                .setColor(Color.RED)
                .setAuthor(player.getName() + " disconnected", getHeadUrl(player), getHeadUrl(player))
                .build();
        sendEmbed(joinEmbed, channelId);
    }

    @Override
    public void onStart() {
        try {
            getPluginLogger().info("Connecting to discord...");
            jda = JDABuilder.createLight(GlobalConfigService.getValue(GlobalConfig.DISCORD_TOKEN))
                    .addEventListeners(new DiscordListener())
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.of(Activity.ActivityType.PLAYING, "Minecraft"))
                    .build()
                    .awaitReady();
            getPluginLogger().info("Discord ready!");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStop() {
        getPluginLogger().info("Stopping discord module");
        jda.shutdown();
    }

    private void sendEmbed(MessageEmbed embed, String channelID) {
        TextChannel channel = jda.getTextChannelById(channelID);
        if (channel == null) {
            SebUtils.getPluginLogger().warning("Text channel " + channelID + " is inaccessible");
        } else {
            channel.sendMessageEmbeds(embed).queue();
        }
    }

    private WebhookClient getOrCreateWebhookClient() {
        if (webhookClient != null) return webhookClient;
        TextChannel channel = jda.getTextChannelById(GlobalConfigService.getValue(GlobalConfig.DISCORD_CHAT_CHANNEL));
        List<Webhook> webhooks = channel.retrieveWebhooks().complete();
        Optional<Webhook> optHook = webhooks.stream().filter(w -> w.getName().equals(WEBHOOK_NAME)).findFirst();
        Webhook webhook = optHook.orElseGet(() -> channel.createWebhook(WEBHOOK_NAME).complete());

        String url = webhook.getUrl();
        webhookClient = new WebhookClientBuilder(url)
                .setThreadFactory(new NamedThreadFactory("webhook"))
                .build();

        return webhookClient;
    }

    public void forwardPlayerMessage(Player author, String message) {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
            WebhookClient client = getOrCreateWebhookClient();
            WebhookMessage msg = new WebhookMessageBuilder()
                    .setUsername(author.getName())
                    .setAvatarUrl(getFaceUrl(author))
                    .setContent(removeVolatileMentions(message))
                    .build();
            client.send(msg);
        }
    }

    private String removeVolatileMentions(String text) {
        return text.replaceAll("@everyone", "``@``everyone").replaceAll("@here", "``@``here").replaceAll("<@&", "<");
    }

    private void forwardDiscordMessage(String name, String contentStripped) {
        Component component = Component
                .text("[Discord] ", NamedTextColor.BLUE)
                .append(Component.text("[%s] ".formatted(name), NamedTextColor.YELLOW))
                .append(Component.text(contentStripped, NamedTextColor.WHITE));
        SebUtils.getPlugin().getServer().broadcast(component);
    }

    public void sendAccessRequest(UUID uuid, String playerName) {
        MessageEmbed accessEmbed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle("Pending Access Request")
                .setImage(getBodyUrl(uuid))
                .setDescription("**" + playerName + "** has requested to join the server")
                .build();
        TextChannel channel = jda.getTextChannelById(GlobalConfigService.getValue(GlobalConfig.DISCORD_ACCESS_REQUEST_CHANNEL));
        channel.sendMessageEmbeds(accessEmbed).setActionRow(createApprovalButtons(uuid)).queue();
    }

    private List<ActionComponent> createApprovalButtons(UUID uuid) {
        List<ActionComponent> approvalButtons = new ArrayList<>();
        approvalButtons.add(Button.success("sebutils:access:approve:" + uuid.toString(), "Approve"));
        approvalButtons.add(Button.danger("sebutils:access:deny:" + uuid.toString(), "Deny"));
        return approvalButtons;
    }

    private class DiscordListener extends ListenerAdapter {

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
            if (!event.getChannel().getId().equals(GlobalConfigService.getValue(GlobalConfig.DISCORD_ACCESS_REQUEST_CHANNEL))) {
                return;
            }

            if (event.getMember() == null) {
                return;
            }

            if (!hasApproverRole(event.getMember())) {
                return;
            }

            // Let discord know we're gonna update the message
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

        private void denyAccessRequest(UUID uuid, ButtonInteractionEvent event) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            player.getPlayerProfile().complete();

            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                Bukkit.getBanList(BanListType.PROFILE).addBan(
                        player.getPlayerProfile(),
                        "Your access request was rejected.",
                        (Instant) null,
                        "Server Access Requests"
                );
            });

            MessageEmbed approvedEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("Access Denied")
                    .setImage(getBodyUrl(uuid))
                    .setDescription("**" + player.getName() + "** has been denied access to the server")
                    .build();
            event.getMessage().editMessageEmbeds(approvedEmbed).setComponents().queue();
        }

        private void approveAccessRequest(UUID uuid, ButtonInteractionEvent event) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            player.getPlayerProfile().complete();

            Bukkit.getScheduler().runTask(getPlugin(), () -> {
                player.setWhitelisted(true);
                Bukkit.reloadWhitelist();
            });

            MessageEmbed approvedEmbed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle("Access Granted")
                    .setImage(getBodyUrl(uuid))
                    .setDescription("**" + player.getName() + "** has been granted access to the server")
                    .build();
            event.getMessage().editMessageEmbeds(approvedEmbed).setComponents().queue();
        }

        private boolean hasApproverRole(Member member) {
            String roleId = GlobalConfigService.getValue(GlobalConfig.DISCORD_ACCESS_APPROVAL_ROLE);
            return member.getRoles().stream().map(ISnowflake::getId).collect(Collectors.toSet()).contains(roleId);
        }
    }
}
