package main.java.elementalmp4.sebutils.web;

import io.javalin.Javalin;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;

public class WebServer {

    private static final NamedThreadFactory namedThreadFactory = new NamedThreadFactory("web");
    private static Javalin app;
    private static Thread serverThread;

    public static void start(String bindAddress, int port) {
        app = Javalin.create(javalinConfig -> {
            javalinConfig.showJavalinBanner = false;
            javalinConfig.staticFiles.add("/static");
        });

        app.get("/config", ctx -> ctx.json(GlobalConfigService.listConfig()));
        app.get("/health", ctx -> ctx.result("OK"));

        for (GlobalConfig conf : GlobalConfig.values()) {
            app.get("/config/" + conf.getKey(), ctx -> ctx.json(GlobalConfigService.getAsConfig(conf)));
        }

        serverThread = namedThreadFactory.newThread(() -> app.start(bindAddress, port));
        serverThread.start();
    }

    public static void stop() {
        if (app != null) {
            app.stop();
            serverThread.interrupt();
        }
    }

}
