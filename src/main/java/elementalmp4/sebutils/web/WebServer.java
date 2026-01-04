package main.java.elementalmp4.sebutils.web;

import io.javalin.Javalin;
import main.java.elementalmp4.sebutils.utils.NamedThreadFactory;
import org.eclipse.jetty.server.ServerConnector;

public class WebServer {

    private static final NamedThreadFactory namedThreadFactory = new NamedThreadFactory("web");
    private static Javalin app;
    private static Thread serverThread;

    public static void start(String bindAddress, int port) {
        app = Javalin.create(javalinConfig -> {
            javalinConfig.jetty.modifyServer(server -> {
                ServerConnector connector = new ServerConnector(server);
                connector.setHost(bindAddress);
                connector.setPort(port);
                server.addConnector(connector);
            });

            javalinConfig.showJavalinBanner = false;
        });

        app.get("/health", ctx -> ctx.result("OK"));

        serverThread = namedThreadFactory.newThread(() -> app.start());
        serverThread.start();
    }

    public static void stop() {
        if (app != null) {
            app.stop();
            serverThread.interrupt();
        }
    }

}
