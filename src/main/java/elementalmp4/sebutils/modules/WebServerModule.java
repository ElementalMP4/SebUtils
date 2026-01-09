package main.java.elementalmp4.sebutils.modules;

import io.javalin.Javalin;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;
import main.java.elementalmp4.sebutils.web.ConfigUpdateHandler;

import static main.java.elementalmp4.sebutils.SebUtils.getModuleManager;
import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class WebServerModule extends AbstractModule {

    private final NamedThreadFactory namedThreadFactory = new NamedThreadFactory("web");
    private Javalin app;
    private Thread serverThread;

    public void onStart() {
        getPluginLogger().info("Starting web server...");

        app = Javalin.create(javalinConfig -> {
            javalinConfig.showJavalinBanner = false;
            javalinConfig.staticFiles.add("/static");
        });

        app.get("/config", ctx -> ctx.json(GlobalConfigService.listConfig()));
        app.get("/health", ctx -> ctx.result("OK"));

        for (GlobalConfig conf : GlobalConfig.values()) {
            app.get("/config/" + conf.getKey(), ctx -> ctx.json(GlobalConfigService.getAsConfig(conf)));
        }

        app.post("/config", new ConfigUpdateHandler()).exception(IllegalArgumentException.class, (e, ctx) -> {
           ctx.status(400).result(e.getMessage());
        });

        app.get("/tiles/{world}/{zoom}/{x}/{z}", ctx -> {
            if (!GlobalConfigService.getAsBoolean(GlobalConfig.MAP_ENABLED)) {
                ctx.status(400).result("Tiles disabled");
                return;
            }
            getModuleManager().get(MapModule.class).serveTile(ctx);
        });

        String bind = GlobalConfigService.getValue(GlobalConfig.WEB_BIND);
        int port = GlobalConfigService.getAsInteger(GlobalConfig.WEB_PORT);

        serverThread = namedThreadFactory.newThread(() -> app.start(bind, port));
        serverThread.start();
        getPluginLogger().info("Web server ready!");
    }

    public void onStop() {
        getPluginLogger().info("Stopping web server module");
        if (app != null) {
            app.stop();
            serverThread.interrupt();
        }
    }
}