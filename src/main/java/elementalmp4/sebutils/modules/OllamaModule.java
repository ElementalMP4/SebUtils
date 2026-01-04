package main.java.elementalmp4.sebutils.modules;

import io.github.ollama4j.Ollama;
import io.github.ollama4j.exceptions.OllamaException;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequest;
import io.github.ollama4j.models.chat.OllamaChatResult;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class OllamaModule extends AbstractModule {

    private static final String SYSTEM_PROMPT = "Your name is chatgpsteve. Your responses to the following questions should be no longer than 300 characters. Respond with ONLY your answer to this question, and nothing else.";
    private final ExecutorService OLLAMA_EXECUTOR = Executors.newSingleThreadExecutor();
    private Ollama ollamaAPI;
    private final Map<String, OllamaChatResult> CONVERSATIONS = new HashMap<>();

    public void onStart() {
        getPluginLogger().info("Starting Ollama...");
        ollamaAPI = new Ollama(GlobalConfigService.getValue(GlobalConfig.OLLAMA_HOST));
        ollamaAPI.setRequestTimeoutSeconds(60);
        getPluginLogger().info("Ollama ready!");
    }

    public void onStop() {
        getPluginLogger().info("Stopping ollama module");
    }

    public String getResponse(String prompt, String name) {
        if (ollamaAPI == null) {
            throw new IllegalStateException("Ollama API not initialized. This shouldn't be possible if Seb programmed the plugin correctly.");
        }
        try {
            OllamaChatRequest builder = OllamaChatRequest.builder();
            builder.withModel(GlobalConfigService.getValue(GlobalConfig.OLLAMA_MODEL));

            OllamaChatResult conversation = CONVERSATIONS.get(name);
            if (conversation != null) {
                builder.withMessages(conversation.getChatHistory());
                builder.withMessage(OllamaChatMessageRole.USER, prompt);
            } else {
                builder.withMessage(OllamaChatMessageRole.SYSTEM, SYSTEM_PROMPT);
                builder.withMessage(OllamaChatMessageRole.SYSTEM, "The user who is talking to you is called " + name + ". All subsequent messages will be from them.");
                builder.withMessage(OllamaChatMessageRole.USER, prompt);
            }

            OllamaChatRequest requestModel = builder.build();
            conversation = ollamaAPI.chat(requestModel, null);
            CONVERSATIONS.put(name, conversation);
            return conversation.getResponseModel().getMessage().getResponse();
        } catch (OllamaException e) {
            throw new RuntimeException(e);
        }
    }

    public void askOllama(String prompt, Server server, String name) {
        OLLAMA_EXECUTOR.submit(() -> {
            String response = getResponse(prompt, name);
            Component message = Component.text(name, NamedTextColor.GREEN)
                    .append(Component.text(": ", NamedTextColor.WHITE))
                    .append(Component.text(prompt, NamedTextColor.WHITE))
                    .append(Component.text("\n", NamedTextColor.WHITE))
                    .append(Component.text("ChatGPSteve: ", NamedTextColor.GREEN))
                    .append(Component.text(response, NamedTextColor.WHITE));
            server.broadcast(message);
        });
    }

    public void clearConversation(String name) {
        CONVERSATIONS.remove(name);
    }
}
