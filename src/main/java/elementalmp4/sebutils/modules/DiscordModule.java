package main.java.elementalmp4.sebutils.modules;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import main.java.elementalmp4.sebutils.SebUtils;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.utils.ConsoleColours;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Optional;

import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class DiscordModule extends AbstractModule {

    private static final String HEAD_URL_FORMAT = "https://api.mineatar.io/head/%s?scale=16";
    private static final String FACE_URL_FORMAT = "https://api.mineatar.io/face/%s?scale=16";
    private static final String WEBHOOK_NAME = "sebutils_chat";

    private JDA jda;
    private WebhookClient webhookClient;

    private String getHeadUrl(Player player) {
        return HEAD_URL_FORMAT.formatted(player.getUniqueId().toString());
    }

    private String getFaceUrlFormat(Player player) {
        return FACE_URL_FORMAT.formatted(player.getUniqueId().toString());
    }

    public void sendJoinMessage(Player player) {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
            MessageEmbed joinEmbed = new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setAuthor(player.getName() + " joined the game", getHeadUrl(player), getHeadUrl(player))
                    .build();
            sendEmbed(joinEmbed);
        }
    }

    public void sendLeaveMessage(Player player) {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
            MessageEmbed joinEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setAuthor(player.getName() + " disconnected", getHeadUrl(player), getHeadUrl(player))
                    .build();
            sendEmbed(joinEmbed);
        }
    }

    @Override
    public void onStart() {
        try {
            getPluginLogger().info("Connecting to discord...");
            jda = JDABuilder.createLight(GlobalConfigService.getValue(GlobalConfig.DISCORD_TOKEN))
                    .addEventListeners(new DiscordMessageListener())
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

    private void sendEmbed(MessageEmbed embed) {
        String channelId = GlobalConfigService.getValue(GlobalConfig.DISCORD_CHANNEL);
        TextChannel channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            SebUtils.getPluginLogger().warning("Text channel " + channelId + " is inaccessible");
        } else {
            channel.sendMessageEmbeds(embed).queue();
        }
    }

    private WebhookClient getOrCreateWebhookClient() {
        if (webhookClient != null) return webhookClient;

        TextChannel channel = jda.getTextChannelById(GlobalConfigService.getValue(GlobalConfig.DISCORD_CHANNEL));
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
                    .setAvatarUrl(getFaceUrlFormat(author))
                    .setContent(removeVolatileMentions(message))
                    .build();
            client.send(msg);
        }
    }

    private String removeVolatileMentions(String text) {
        return text.replaceAll("@everyone", "``@``everyone").replaceAll("@here", "``@``here").replaceAll("<@&", "<");
    }

    private void forwardDiscordMessage(String name, String contentStripped) {
        TextComponent component = new TextComponent();
        component.addExtra("" + NamedTextColor.BLUE + TextDecoration.BOLD + "[DISCORD] ");
        component.addExtra(NamedTextColor.YELLOW + "[%s] ".formatted(name));
        component.addExtra(NamedTextColor.WHITE + contentStripped);
        SebUtils.getPlugin().getServer().broadcast(component);
        SebUtils.getPluginLogger().info("%s[DISCORD] %s[%s] %s%s"
                .formatted(ConsoleColours.BLUE, ConsoleColours.YELLOW, name, ConsoleColours.WHITE, contentStripped));
    }

    private class DiscordMessageListener extends ListenerAdapter {

        @Override
        public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
            if (event.isWebhookMessage()) return;
            if (event.getAuthor().getId().equals(jda.getSelfUser().getId())) return;
            forwardDiscordMessage(event.getAuthor().getName(), event.getMessage().getContentStripped());
        }

    }

}
