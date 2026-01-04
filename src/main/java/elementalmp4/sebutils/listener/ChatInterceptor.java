package main.java.elementalmp4.sebutils.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import main.java.elementalmp4.sebutils.annotation.SebUtilsListener;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.modules.DiscordModule;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.NicknameService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static main.java.elementalmp4.sebutils.SebUtils.getModuleManager;

@SebUtilsListener
public class ChatInterceptor implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        String realName = event.getPlayer().getName();
        String displayName = NicknameService.getPlayerNameCustomised(realName);

        Component nameComponent = Component.text(displayName)
                .hoverEvent(
                        HoverEvent.showText(Component.text(realName, NamedTextColor.YELLOW))
                );

        Component finalMessage = nameComponent
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(event.message());

        event.renderer((source, sourceDisplayName, message, viewer) -> finalMessage);

        if (!GlobalConfigService.getAsBoolean(GlobalConfig.DISCORD_ENABLED)) {
            return;
        }

        String messageContent = PlainTextComponentSerializer.plainText()
                .serialize(event.message());
        getModuleManager().get(DiscordModule.class).forwardPlayerMessage(event.getPlayer(), messageContent);
    }
}
