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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OllamaService {

    private static final String SYSTEM_PROMPT = "Your responses to the following questions should be no longer than 300 characters. Try and be funny and witty, your end goal is to make the user laugh. You will receive questions in the format <username>: <prompt>. You may use the username in your response. Respond with ONLY your answer to this question, and nothing else.";
    private static final ExecutorService OLLAMA_EXECUTOR = Executors.newSingleThreadExecutor();
    private static final String MODEL = "llama3.2:latest";
    private static OllamaAPI ollamaAPI;
    private static OllamaChatResult conversation;

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
        conversation = null;
    }

    public static String getResponse(String prompt, String name) {
        if (ollamaAPI == null) {
            throw new IllegalStateException("Ollama API not initialized. This shouldn't be possible if Seb programmed the plugin correctly.");
        }
        try {
            OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(MODEL);
            if (conversation != null) {
                builder.withMessages(conversation.getChatHistory());
                builder.withMessage(OllamaChatMessageRole.USER, formatPrompt(name, prompt));
            } else {
                builder.withMessage(OllamaChatMessageRole.SYSTEM, SYSTEM_PROMPT);
                builder.withMessage(OllamaChatMessageRole.USER, formatPrompt(name, prompt));
            }

            OllamaChatRequestModel requestModel = builder.build();
            conversation = ollamaAPI.chat(requestModel);
            return conversation.getResponse();
        } catch (OllamaBaseException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String formatPrompt(String name, String prompt) {
        return "%s: %s".formatted(name, prompt);
    }

    public static boolean ollamaEnabled() {
        return GlobalConfigService.getAsBoolean(GlobalConfig.OLLAMA_ENABLED);
    }

    public static void askOllama(String prompt, Server server, String name) {
        OLLAMA_EXECUTOR.submit(() -> {
            String response = getResponse(prompt, name);
            server.broadcastMessage(
                    ChatColor.GREEN + name + " asked: " + ChatColor.WHITE + prompt + "\n"
                            + ChatColor.GREEN + "AI Responded: " + ChatColor.WHITE + response);
        });
    }
}
