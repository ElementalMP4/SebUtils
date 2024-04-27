package main.java.elementalmp4.service;

import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.utils.AfkStatus;
import main.java.elementalmp4.utils.NamedThreadFactory;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AfkService {

    private static final HashMap<String, AfkStatus> PLAYER_STATUS = new HashMap<>();

    static {
        if (GlobalConfigService.getAsBoolean(GlobalConfig.AFK_ENABLED)) {
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1, new NamedThreadFactory("afk"));
            executor.scheduleAtFixedRate(AfkService::checkForAfk, 0, 5, TimeUnit.SECONDS);
        }
    }

    private static void checkForAfk() {
        for (String name : PLAYER_STATUS.keySet()) {
            PLAYER_STATUS.get(name).checkActivity();
        }
    }

    public static void removeUser(String name) {
        PLAYER_STATUS.remove(name);
    }

    public static void updatePlayerMovement(String name) {
        if (!PLAYER_STATUS.containsKey(name)) {
            PLAYER_STATUS.put(name, new AfkStatus(name));
        } else {
            PLAYER_STATUS.get(name).updateTime();
        }
    }

}
