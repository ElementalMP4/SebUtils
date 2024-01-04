package main.java.elementalmp4.service;

import main.java.elementalmp4.utils.TeleportRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TeleportService {

    private static final List<TeleportRequest> requests = new ArrayList<>();

    static {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
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

    public static void denyTeleport(String name) {
        TeleportRequest tr = requests.stream().filter(t -> t.getAuthority().equals(name)).findFirst().orElse(null);
        assert tr != null;
        requests.remove(tr);
    }
}