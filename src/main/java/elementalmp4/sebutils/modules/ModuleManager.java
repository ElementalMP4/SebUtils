package main.java.elementalmp4.sebutils.modules;

import main.java.elementalmp4.sebutils.config.GlobalConfig;
import main.java.elementalmp4.sebutils.service.GlobalConfigService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModuleManager {

    private final Map<Class<? extends AbstractModule>, Supplier<? extends AbstractModule>> factories = new HashMap<>();
    private final Map<Class<? extends AbstractModule>, AbstractModule> instances = new HashMap<>();
    private final Map<Class<? extends AbstractModule>, GlobalConfig> toggles = new HashMap<>();

    public synchronized <T extends AbstractModule> void register(Class<T> type, Supplier<T> factory) {
        register(type, factory, null);
    }

    public synchronized <T extends AbstractModule> void register(Class<T> type, Supplier<T> factory, GlobalConfig optionalToggle) {
        factories.put(type, factory);
        if (optionalToggle != null) toggles.put(type, optionalToggle);

        if (optionalToggle == null) {
            start(type);
        } else if (GlobalConfigService.getAsBoolean(optionalToggle)) {
            start(type);
        }
    }

    public synchronized <T extends AbstractModule> T start(Class<T> type) {
        stop(type);

        Supplier<? extends AbstractModule> factory = factories.get(type);
        if (factory == null) {
            throw new IllegalStateException("Module not registered: " + type.getName());
        }

        T module = type.cast(factory.get());
        module.start();

        instances.put(type, module);
        return module;
    }

    public synchronized void stop(Class<? extends AbstractModule> type) {
        AbstractModule existing = instances.remove(type);
        if (existing != null) {
            existing.stop();
        }
    }

    public synchronized void restart(Class<? extends AbstractModule> type) {
        if (isRunning(type)) {
            stop(type);
        }

        start(type);
    }

    public synchronized void stopAll() {
        for (AbstractModule module : instances.values()) {
            module.stop();
        }
        instances.clear();
    }

    public <T extends AbstractModule> T get(Class<T> type) {
        return type.cast(instances.get(type));
    }

    public boolean isRunning(Class<? extends AbstractModule> type) {
        return instances.containsKey(type);
    }

    public GlobalConfig getToggle(Class<? extends AbstractModule> type) {
        return toggles.get(type);
    }
}