package main.java.elementalmp4.sebutils.modules;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.listener.DiscordListener;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class DiscordModule extends AbstractModule {

    private static final String HEAD_URL_FORMAT = "https://api.mineatar.io/head/%s?scale=16";
    private static final String FACE_URL_FORMAT = "https://api.mineatar.io/face/%s?scale=16";
    private static final String BODY_URL_FORMAT = "https://api.mineatar.io/body/full/%s?scale=4";
    private static final String WEBHOOK_NAME = "sebutils_chat";

    private JDA jda;
    private WebhookClient webhookClient;

    public static String getHeadUrl(Player player) {
        return HEAD_URL_FORMAT.formatted(player.getUniqueId().toString());
    }

    public static String getFaceUrl(Player player) {
        return FACE_URL_FORMAT.formatted(player.getUniqueId().toString());
    }

    public static String getBodyUrl(UUID uuid) {
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
                    .addEventListeners(new DiscordListener(jda))
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

    public void sendAccessRequest(UUID uuid, String playerName) {
        MessageEmbed accessEmbed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle("Pending Access Request")
                .setImage(getBodyUrl(uuid))
                .setDescription("**" + playerName + "** has requested to join the server")
                .setFooter(uuid.toString())
                .build();

        TextChannel channel = jda.getTextChannelById(GlobalConfigService.getValue(GlobalConfig.DISCORD_ACCESS_REQUEST_CHANNEL));
        if (channel == null) {
            getPluginLogger().severe("Couldn't send access request message to Discord - channel ID may be incorrect");
            return;
        }
        channel.sendMessageEmbeds(accessEmbed).setActionRow(createApprovalButtons(uuid)).queue();
    }

    private List<ActionComponent> createApprovalButtons(UUID uuid) {
        List<ActionComponent> approvalButtons = new ArrayList<>();
        approvalButtons.add(Button.success("sebutils:access:approve:" + uuid, "Approve"));
        approvalButtons.add(Button.danger("sebutils:access:deny:" + uuid, "Deny"));
        return approvalButtons;
    }
}
