package main.java.elementalmp4.sebutils.web;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.modules.AbstractModule;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static main.java.elementalmp4.sebutils.SebUtils.getModuleManager;

public class ConfigUpdateHandler implements Handler {

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        JSONObject configChanges = new JSONObject(ctx.body());
        for (String key : configChanges.keySet()) {
            if (!GlobalConfigService.listConfig().containsKey(key)) {
                throw new IllegalArgumentException("Unknown key " + key);
            }
        }

        List<Class<? extends AbstractModule>> modulesRequiringAttention = new ArrayList<>();
        for (String key : configChanges.keySet()) {
            String value = configChanges.getString(key);
            GlobalConfig config = GlobalConfig.getByKey(key);
            if (value.equals("REDACTED")) continue;
            if (!value.equals(GlobalConfigService.getValue(config))) {
                GlobalConfigService.set(config, value);
                if (config.getConfiguredModule() != null) {
                    modulesRequiringAttention.add(config.getConfiguredModule());
                }
            }
        }

        List<Class<? extends AbstractModule>> done = new ArrayList<>();
        for (Class<? extends AbstractModule> module: modulesRequiringAttention) {
            if (done.contains(module)) continue;
            GlobalConfig toggle = getModuleManager().getToggle(module);

            if (GlobalConfigService.getAsBoolean(toggle)) {
                getModuleManager().restart(module);
            } else {
                getModuleManager().stop(module);
            }

            done.add(module);
        }

        ctx.result("OK");
    }
}
