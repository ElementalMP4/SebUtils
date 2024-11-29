package main.java.elementalmp4.service;

import io.javalin.Javalin;
import main.java.elementalmp4.GlobalConfig;
import main.java.elementalmp4.SebUtils;
import main.java.elementalmp4.annotation.SebUtilsWebHandler;
import main.java.elementalmp4.utils.ConsoleColours;
import main.java.elementalmp4.utils.ReflectiveInstantiator;
import main.java.elementalmp4.web.AbstractWebHandler;

import java.util.List;

public class WebService {

    private static Javalin createServer() {
        List<AbstractWebHandler> handlers = new ReflectiveInstantiator<AbstractWebHandler>("main.java.elementalmp4")
                .findAnnotatedClasses(SebUtilsWebHandler.class, AbstractWebHandler.class)
                .getInstances();

        Javalin app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        });

        for (AbstractWebHandler handler : handlers) {
            app.addHttpHandler(handler.getMethod(), handler.getPath(), handler);
        }
        return app;
    }

    public static void startServer() {
        int port = GlobalConfigService.getAsInteger(GlobalConfig.WEB_SERVER_PORT);
        Javalin app = createServer();
        app.start(port);
        SebUtils.getPluginLogger().info(ConsoleColours.YELLOW + "Web server started on port " + port);
    }

}
