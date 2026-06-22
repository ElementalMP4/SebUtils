package main.java.elementalmp4.sebutils.modules;

import io.javalin.Javalin;
import main.java.elementalmp4.sebutils.config.DataType;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import main.java.elementalmp4.sebutils.service.WebAuthService;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;
import main.java.elementalmp4.sebutils.web.ConfigUpdateHandler;

import java.util.List;

import static main.java.elementalmp4.sebutils.SebUtils.getModuleManager;
import static main.java.elementalmp4.sebutils.SebUtils.getPluginLogger;

public class WebServerModule extends AbstractModule {

    private static final List<String> OPEN_ENDPOINTS = List.of("/login", "/health", "/login.html", "/styles.css");

    private final NamedThreadFactory namedThreadFactory = new NamedThreadFactory("web");
    private Javalin app;
    private Thread serverThread;

    public void onStart() {
        getPluginLogger().info("Starting web server...");

        app = Javalin.create(javalinConfig -> {
            javalinConfig.showJavalinBanner = false;
            javalinConfig.staticFiles.add("/static");
        });

        app.before(ctx -> {
            String path = ctx.path();
            if (OPEN_ENDPOINTS.contains(path)) {
                return;
            }

            String token = ctx.cookie("web_token");
            if (!WebAuthService.validateToken(token)) {
                if (path.startsWith("/api")) {
                    ctx.status(401).result("Invalid token");
                } else {
                    ctx.redirect("/login.html");
                }
                ctx.skipRemainingHandlers();
                return;
            }

            ctx.attribute("userHasBeenAuthenticated", true);
        });

        app.post("/login", ctx -> {
            String otp = ctx.formParam("otp");
            if (otp == null) {
                ctx.status(400).result("Missing username or otp");
                return;
            }

            String token = WebAuthService.verifyOtpAndIssueToken(otp, ctx.ip(), ctx.userAgent());
            if (token == null) {
                ctx.status(401).result("Invalid OTP");
                ctx.skipRemainingHandlers();
            } else {
                String cookie = "web_token=" + token
                        + "; HttpOnly"
                        + "; Path=/"
                        + "; SameSite=Strict"
                        + "; Secure"
                        + "; Max-Age=" + (30 * 24 * 60 * 60);

                ctx.res().addHeader("Set-Cookie", cookie);
                ctx.redirect("/");
            }
        });

        app.post("/logout", ctx -> {
            WebAuthService.revokeToken(ctx.cookie("web_token"));
            ctx.removeCookie("web_token", "/");
            ctx.redirect("/login.html");
        });

        app.get("/health", ctx -> ctx.result("OK"));
        app.get("/status", ctx -> ctx.result("Logged in"));

        app.get("/api/config", ctx -> {
            if (!WebAuthService.hasAuthenticated(ctx)) {
                return;
            }
            ctx.json(GlobalConfigService.listConfig());
        });

        for (GlobalConfig conf : GlobalConfig.values()) {
            app.get("/api/config/" + conf.getKey(), ctx -> {
                if (!WebAuthService.hasAuthenticated(ctx)) {
                    return;
                }
                ctx.json(GlobalConfigService.getAsConfig(conf));
            });
        }

        app.post("/api/config", new ConfigUpdateHandler()).exception(IllegalArgumentException.class, (e, ctx) -> {
            ctx.status(400).result(e.getMessage());
        });

        app.get("/api/tiles/{world}/{zoom}/{x}/{z}", ctx -> {
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