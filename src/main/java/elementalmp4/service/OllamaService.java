package main.java.elementalmp4.service;

import io.github.amithkoujalgi.ollama4j.core.OllamaAPI;
import io.github.amithkoujalgi.ollama4j.core.exceptions.OllamaBaseException;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatMessageRole;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestBuilder;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatRequestModel;
import io.github.amithkoujalgi.ollama4j.core.models.chat.OllamaChatResult;
import main.java.elementalmp4.GlobalConfig;
import org.bukkit.ChatColor;
import org.bukkit.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OllamaService {

    private static final String SYSTEM_PROMPT = "Your name is chatgpsteve. Your responses to the following questions should be no longer than 300 characters. Respond with ONLY your answer to this question, and nothing else.";
    private static final ExecutorService OLLAMA_EXECUTOR = Executors.newSingleThreadExecutor();
    private static OllamaAPI ollamaAPI;
    private static final Map<String, OllamaChatResult> CONVERSATIONS = new HashMap<>();

    public static boolean startOllamaOnBoot() {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.OLLAMA_ENABLED)) {
            startClient();
            return true;
        }
        return false;
    }

    public static void startClient() {
        ollamaAPI = new OllamaAPI(GlobalConfigService.getValue(GlobalConfig.OLLAMA_HOST));
        ollamaAPI.setRequestTimeoutSeconds(60);

    }

    public static void stopClient() {
        ollamaAPI = null;
        CONVERSATIONS.clear();
    }

    public static String getResponse(String prompt, String name) {
        if (ollamaAPI == null) {
            throw new IllegalStateException("Ollama API not initialized. This shouldn't be possible if Seb programmed the plugin correctly.");
        }
        try {
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(GlobalConfigService.getValue(GlobalConfig.OLLAMA_MODEL));
            OllamaChatResult conversation = CONVERSATIONS.get(name);
            if (conversation != null) {
                builder.withMessages(conversation.getChatHistory());
                builder.withMessage(OllamaChatMessageRole.USER, prompt);
            } else {
                builder.withMessage(OllamaChatMessageRole.SYSTEM, SYSTEM_PROMPT);
                builder.withMessage(OllamaChatMessageRole.SYSTEM, "The user who is talking to you is called " + name);
                builder.withMessage(OllamaChatMessageRole.USER, prompt);
            }

            OllamaChatRequestModel requestModel = builder.build();
            conversation = ollamaAPI.chat(requestModel);
            CONVERSATIONS.put(name, conversation);
            return conversation.getResponse();
        } catch (OllamaBaseException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean ollamaEnabled() {
        return GlobalConfigService.getAsBoolean(GlobalConfig.OLLAMA_ENABLED);
    }

    public static void askOllama(String prompt, Server server, String name) {
        OLLAMA_EXECUTOR.submit(() -> {
            String response = getResponse(prompt, name);
            server.broadcastMessage(
                    ChatColor.GREEN + name + ": " + ChatColor.WHITE + prompt + "\n"
                            + ChatColor.GREEN + "ChatGPSteve: " + ChatColor.WHITE + response);
        });
    }

    public static void clearConversation(String name) {
        CONVERSATIONS.remove(name);
    }
}
