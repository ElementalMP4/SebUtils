package main.java.elementalmp4.web;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.HandlerType;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractWebHandler implements Handler{

    public abstract HandlerType getMethod();
    public abstract String getPath();
    public abstract void handle(@NotNull Context ctx) throws Exception;

}
