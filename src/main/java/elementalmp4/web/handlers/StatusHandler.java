package main.java.elementalmp4.web.handlers;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import main.java.elementalmp4.annotation.SebUtilsWebHandler;
import main.java.elementalmp4.web.AbstractWebHandler;
import org.jetbrains.annotations.NotNull;

@SebUtilsWebHandler
public class StatusHandler extends AbstractWebHandler {
    @Override
    public HandlerType getMethod() {
        return HandlerType.GET;
    }

    @Override
    public String getPath() {
        return "/status";
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ctx.status(HttpStatus.OK);
        ctx.result("OK");
    }
}
